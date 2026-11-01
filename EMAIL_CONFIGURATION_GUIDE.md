# 📧 Email Notification System - WHOOSH Application

## 🚀 Overview
Sistem notifikasi email ini mengirimkan email HTML yang indah ke **Gmail user yang sedang login** untuk 3 kondisi utama:
1. **Pembelian Tiket** (Purchase) - Email dikirim ke Gmail user setelah payment berhasil
2. **Reschedule Tiket** (Reschedule) - Email dikirim ke Gmail user setelah reschedule berhasil
3. **Refund Tiket** (Refund) - Email dikirim ke Gmail user setelah refund diproses

## 📋 Alur Email Notification

### 🔐 **Sumber Email Recipient**
Email recipient **OTOMATIS** diambil dari:
1. **Tabel User** yang sedang login (via SessionManager)
2. **Gmail user** yang tersimpan di database saat registrasi/login
3. **booking.userEmail** yang ter-sync dengan session user

### 📱 **Alur Lengkap:**

#### 1. **User Login** 
```
User input Gmail + Password → API Login → 
SessionManager.saveAuthToken(email, username, userId) →
BookingViewModel.booking.userEmail = user's Gmail
```

#### 2. **Pemesanan Tiket**
```
User pesan tiket → completePayment() → 
API create booking → Email otomatis ke user's Gmail →
User terima email konfirmasi pembelian
```

#### 3. **Reschedule/Refund**
```
User pilih tiket dari history → reschedule/cancel →
API update booking → Email otomatis ke user's Gmail →
User terima email konfirmasi reschedule/refund  
```

## 🎯 **Keunggulan Sistem Ini:**
- ✅ **Otomatis** - Tidak perlu input email manual
- ✅ **Aman** - Menggunakan session user yang ter-autentikasi  
- ✅ **Konsisten** - Selalu kirim ke Gmail user yang tepat
- ✅ **Real-time** - Email terkirim langsung setelah transaksi

## 📂 File Structure
```
app/src/main/java/com/example/whoos_aplication/utils/
├── EmailSender.kt                  # Core email sending functionality
├── EmailNotificationManager.kt     # Manager untuk mengelola notifikasi email
└── SessionManager.kt              # Session management (existing)

app/src/main/java/com/example/whoos_aplication/viewmodel/
└── bookingsviewmodels.kt          # Terintegrasi dengan email notifications
```

## ⚙️ Configuration Setup

### 1. Email Account Setup
Untuk mengirim email melalui Gmail SMTP, Anda perlu:

1. **Buat Gmail Account untuk Aplikasi**
   ```
   Email: whoosh-app@gmail.com (atau email khusus aplikasi Anda)
   ```

2. **Enable 2-Factor Authentication** di Gmail

3. **Generate App Password**
   - Masuk ke Gmail Settings > Security
   - Enable 2-factor authentication
   - Generate App Password untuk "Mail"
   - Simpan password 16 karakter yang dihasilkan

### 2. Update EmailSender.kt Configuration
```kotlin
// Dalam file EmailSender.kt, update konstanta ini:
companion object {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val SENDER_EMAIL = "whoosh-app@gmail.com"  // Email aplikasi Anda
    private const val SENDER_PASSWORD = "abcd efgh ijkl mnop"  // App password dari Gmail
}
```

### 3. Add Internet Permission
Tambahkan permission di `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🎨 Email Templates

### Purchase Email
- ✅ Konfirmasi pembelian berhasil
- 🎫 Detail lengkap tiket (kode booking, kereta, jadwal, kursi, harga)
- 📋 Informasi penting untuk perjalanan
- 🎯 Warna hijau untuk sukses

### Reschedule Email  
- 🔄 Konfirmasi reschedule berhasil
- 📅 Perbandingan jadwal lama vs baru
- ⚠️ Highlight jadwal baru dengan background kuning
- 🎯 Warna oranye untuk perubahan

### Refund Email
- 💰 Konfirmasi refund berhasil
- 💵 Detail harga asli vs jumlah refund
- ⏰ Timeline proses refund berdasarkan metode pembayaran
- 🎯 Warna biru untuk informasi

## 📱 Usage Examples

### 🔄 **Otomatis (Recommended - Menggunakan Gmail User):**
Email akan terkirim **OTOMATIS ke Gmail user yang sedang login** saat:

```kotlin
// 1. PEMBELIAN - Email ke Gmail user setelah payment
bookingViewModel.completePayment(
    onSuccess = { 
        // Email purchase otomatis dikirim ke user's Gmail
        showSuccess("Tiket berhasil dibeli! Cek email Anda.")
    },
    onError = { error -> showError(error) }
)

// 2. RESCHEDULE - Email ke Gmail user setelah reschedule  
bookingViewModel.rescheduleBooking(
    reservationNo = "WHH12345678",
    newDate = "2026-06-20",
    onSuccess = {
        // Email reschedule otomatis dikirim ke user's Gmail
        showSuccess("Tiket berhasil direschedule! Cek email Anda.")
    },
    onError = { error -> showError(error) }
)

// 3. REFUND - Email ke Gmail user setelah cancel
bookingViewModel.cancelBooking(
    reservationNo = "WHH12345678", 
    totalPrice = 150000,
    onSuccess = {
        // Email refund otomatis dikirim ke user's Gmail
        showSuccess("Tiket berhasil direfund! Cek email Anda.")
    },
    onError = { error -> showError(error) }
)
```

### 🛠️ **Manual (Untuk Testing):**
```kotlin
val emailManager = EmailNotificationManager(context)

// Hanya untuk testing - production menggunakan otomatis
emailManager.sendPurchaseNotification(
    recipientEmail = "user@gmail.com", // Manual input untuk testing
    bookingCode = "WHH240609001",
    // ... parameter lainnya
) { success, error ->
    if (success) {
        Log.i("Email", "✅ Email berhasil dikirim!")
    } else {
        Log.e("Email", "❌ Gagal mengirim email: $error")
    }
}
```

### 🔍 **Debug User Info:**
```kotlin
// Untuk debugging - cek user yang sedang login
val userInfo = bookingViewModel.getCurrentUserInfo()
println(userInfo)
// Output:
// User Info:
// - Email: user@gmail.com
// - Username: John Doe  
// - User ID: 123
// - Is Logged In: true
// - Booking Email: user@gmail.com
```

## 🔧 Troubleshooting

### Common Issues & Solutions

1. **Email tidak terkirim**
   ```
   Problem: Authentication failed
   Solution: 
   - Pastikan App Password benar (bukan password Gmail biasa)
   - Enable 2FA di Gmail account
   - Gunakan App Password 16 karakter
   ```

2. **SMTP Connection Error**
   ```
   Problem: Connection timeout
   Solution:
   - Pastikan ada koneksi internet
   - Check firewall/antivirus tidak memblokir port 587
   - Pastikan SMTP settings benar
   ```

3. **Email masuk ke Spam**
   ```
   Problem: Email masuk spam folder
   Solution:
   - Gunakan domain email yang trusted
   - Set proper email headers
   - Minta user untuk whitelist sender email
   ```

## 🎯 Email Features

### Design Features
- 📱 **Responsive Design** - Tampil sempurna di semua device
- 🎨 **Beautiful HTML Layout** - Gradient header, clean typography
- 🌈 **Color-coded by Type** - Hijau (purchase), Oranye (reschedule), Biru (refund)
- 📧 **Professional Branding** - WHOOSH logo dan footer informasi

### Content Features
- 🔢 **Booking Code Highlight** - Kode booking terlihat jelas
- 📊 **Organized Information** - Data tiket tertata rapi dalam tabel
- ⚠️ **Important Notes** - Tips dan informasi penting untuk penumpang
- 📞 **Contact Information** - Link customer service dan FAQ

### Technical Features
- 🔄 **Asynchronous Sending** - Tidak memblokir UI
- 📱 **Context-aware** - Terintegrasi dengan Android Context
- 🛡️ **Error Handling** - Proper error handling dan callback
- 🎭 **Template System** - Mudah dikustomisasi dan diperluas

## 🚨 Security Notes

1. **Never commit credentials** ke repository
2. **Use environment variables** untuk production
3. **Implement proper error handling** untuk mencegah crash
4. **Validate email addresses** sebelum sending
5. **Rate limit** email sending untuk mencegah spam

## 📈 Monitoring & Analytics

Untuk production, pertimbangkan untuk:
- Log email sending events
- Monitor delivery rates  
- Track email open rates (jika diperlukan)
- Set up email delivery alerts

## 🎉 Conclusion

Sistem email notification ini memberikan:
- ✅ **User Experience yang baik** dengan email konfirmasi profesional
- 📧 **Komunikasi yang jelas** untuk setiap transaksi
- 🎨 **Branding yang konsisten** dengan desain WHOOSH
- 🔧 **Implementasi yang mudah** dan terintegrasi dengan sistem booking

Email akan dikirim otomatis setiap kali ada transaksi pembelian, reschedule, atau refund, memberikan kepercayaan dan transparansi kepada pengguna aplikasi WHOOSH.