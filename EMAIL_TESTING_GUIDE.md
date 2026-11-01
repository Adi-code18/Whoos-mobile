# 🧪 Email System Testing Guide - WHOOSH Application

## 🚀 Testing Scenario

Berikut adalah panduan untuk testing sistem email notification:

## 📋 Pre-Requirements Testing

### 1. **Setup Gmail SMTP**
```kotlin
// Update di EmailSender.kt
companion object {
    private const val SENDER_EMAIL = "whoosh.test@gmail.com"  // Gmail testing Anda
    private const val SENDER_PASSWORD = "abcd efgh ijkl mnop"  // App Password Gmail
}
```

### 2. **Create Test User Account**
```sql
-- Test user di database
INSERT INTO users (full_name, email, phone, password) 
VALUES ('Test User', 'test.user@gmail.com', '08123456789', 'password123');
```

## 🔍 Testing Steps

### **Test 1: Purchase Email** 
```kotlin
// 1. Login dengan test user
bookingViewModel.login(
    email = "test.user@gmail.com",
    pass = "password123",
    onSuccess = { 
        println("✅ Login berhasil")
        // 2. Lakukan pembelian tiket
        // 3. Cek Gmail test.user@gmail.com untuk email purchase
    },
    onError = { error -> println("❌ Login gagal: $error") }
)

// Expected Log Output:
// 📧 WHOOSH_EMAIL: 🎫 PURCHASE EMAIL SENT
// ═══════════════════════════════════════
// 📧 Recipient: test.user@gmail.com
// 🎫 Booking Code: WHH12345678
// 📅 Timestamp: 2026-06-09 14:30:45
// ✅ Status: Email berhasil dikirim ke Gmail user
```

### **Test 2: Reschedule Email**
```kotlin
// 1. Pilih tiket dari booking history
// 2. Klik reschedule
bookingViewModel.rescheduleBooking(
    reservationNo = "WHH12345678",
    newDate = "2026-06-20",
    onSuccess = {
        println("✅ Reschedule berhasil, cek email")
        // 3. Cek Gmail untuk email reschedule
    },
    onError = { error -> println("❌ Error: $error") }
)

// Expected Gmail Content:
// Subject: 🔄 Konfirmasi Reschedule Tiket - WHH12345678
// Content: Orange themed email with old vs new schedule
```

### **Test 3: Refund Email**
```kotlin
// 1. Pilih tiket dari booking history
// 2. Klik cancel/refund
bookingViewModel.cancelBooking(
    reservationNo = "WHH12345678",
    totalPrice = 150000,
    onSuccess = {
        println("✅ Refund berhasil, cek email")
        // 3. Cek Gmail untuk email refund
    },
    onError = { error -> println("❌ Error: $error") }
)

// Expected Gmail Content:
// Subject: 💰 Konfirmasi Refund Tiket - WHH12345678
// Content: Blue themed email with refund amount (75% of original price)
```

## 🐛 Common Testing Issues & Solutions

### **Issue 1: Email tidak terkirim**
```bash
# Check Log Output:
❌ EMAIL ERROR
═══════════════════════════════════════
📧 Type: PURCHASE  
📧 Recipient: test.user@gmail.com
❌ Error: Authentication failed
═══════════════════════════════════════

# Solution:
1. Pastikan Gmail App Password benar (bukan password Gmail biasa)
2. Enable 2FA di Gmail account
3. Generate App Password baru jika perlu
```

### **Issue 2: User email tidak ditemukan**
```bash
# Check Log Output:
❌ EMAIL ERROR
📧 Type: PURCHASE
📧 Recipient: Unknown
❌ Error: User email tidak ditemukan

# Solution:
1. Pastikan user sudah login
2. Check SessionManager.getEmail() tidak null
3. Pastikan login API mengembalikan email dengan benar
```

### **Issue 3: Email masuk ke Spam**
```bash
# Gmail marks email as spam

# Solution:
1. Check sender domain reputation
2. Add "whoosh.test@gmail.com" to Gmail contacts
3. Move email from Spam to Inbox manually untuk training Gmail
```

## 📱 Testing UI Integration

### **Test Purchase Flow**
```kotlin
@Composable
fun TestPurchaseScreen() {
    val bookingViewModel = remember { BookingViewModel(LocalContext.current.applicationContext as Application) }
    
    Button(
        onClick = {
            // Simulasi purchase
            bookingViewModel.completePayment(
                onSuccess = { 
                    // Show success message
                    // Instruksi user untuk cek Gmail
                },
                onError = { error -> 
                    // Show error message
                }
            )
        }
    ) {
        Text("Test Purchase Email")
    }
}
```

### **Debug User Session**
```kotlin
Button(
    onClick = {
        val userInfo = bookingViewModel.getCurrentUserInfo()
        Log.d("DEBUG", userInfo)
    }
) {
    Text("Debug User Session")  
}

// Expected Output:
// User Info:
// - Email: test.user@gmail.com
// - Username: Test User
// - User ID: 123
// - Is Logged In: true
// - Booking Email: test.user@gmail.com
```

## ✅ Testing Checklist

### **Pre-Testing Setup**
- [ ] Gmail SMTP credentials configured
- [ ] Test Gmail account ready
- [ ] App Password generated
- [ ] Internet permission added
- [ ] Dependencies installed

### **Email Purchase Testing**
- [ ] User dapat login dengan Gmail
- [ ] Email tersimpan di SessionManager
- [ ] Purchase email terkirim ke Gmail user
- [ ] Email content sesuai template
- [ ] Booking code dan details benar

### **Email Reschedule Testing**  
- [ ] User dapat reschedule dari booking history
- [ ] Email reschedule terkirim ke Gmail user
- [ ] Old vs new schedule ditampilkan dengan benar
- [ ] Email styling (orange theme) sesuai

### **Email Refund Testing**
- [ ] User dapat cancel booking
- [ ] Refund amount calculation benar (75%)
- [ ] Email refund terkirim ke Gmail user  
- [ ] Timeline refund info ditampilkan
- [ ] Email styling (blue theme) sesuai

### **Error Handling Testing**
- [ ] User tidak login → email tidak dikirim
- [ ] SMTP error → proper error logging
- [ ] Network error → graceful handling
- [ ] Invalid email → error callback triggered

## 🎯 Success Criteria

✅ **Email Successfully Sent When:**
1. User login dengan Gmail berhasil
2. Email tersimpan di session dengan benar  
3. Transaction (purchase/reschedule/refund) berhasil
4. Email dengan template yang tepat terkirim ke Gmail user
5. Log menampilkan status sukses dengan detail lengkap

✅ **Perfect Testing Result:**
```bash
📧 WHOOSH_EMAIL: 🎫 PURCHASE EMAIL SENT
═══════════════════════════════════════
📧 Recipient: test.user@gmail.com
🎫 Booking Code: WHH20240609001
📅 Timestamp: 2026-06-09 14:30:45
✅ Status: Email berhasil dikirim ke Gmail user
═══════════════════════════════════════
```

## 📞 Support

Jika ada issue dalam testing:
1. Check log output di Logcat dengan filter "WHOOSH_EMAIL"
2. Verify Gmail SMTP settings
3. Test dengan different Gmail account
4. Check network connectivity
5. Verify user session data

Happy Testing! 🚀📧