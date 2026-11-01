# Summary Perubahan - Whoosh Application

## Tanggal: 20 Mei 2026

### 1. Penghapusan Data Dummy

#### File yang Diubah:
1. **WhooshUtils.kt**
   - ❌ Dihapus: `trainCodes` array hardcoded
   - ❌ Dihapus: `getTrainCode()` function

2. **bookingsdata.kt**
   - ❌ Dihapus: `whooshSchedules` list hardcoded
   - ✅ Diganti: `emptyList<Schedule>()`

3. **pilihkursi.kt**
   - ❌ Dihapus: Simulasi kursi yang sudah dipesan (`isBooked`)
   - ✅ Semua kursi sekarang tersedia untuk dipilih

4. **dalamproses.kt**
   - ❌ Dihapus: ID nomor identitas dummy (`637800000027`)
   - ✅ Diganti: String kosong

5. **virtualaccount.kt**
   - ❌ Dihapus: Nomor VA dummy (`4010260469883025`)
   - ✅ Diganti: String kosong (akan diisi dari backend)

6. **TicketDetailScreen.kt**
   - ❌ Dihapus: ID nomor identitas dummy (`3206****03`)
   - ✅ Diganti: String kosong

7. **dashboardscreens.kt**
   - ❌ Dihapus: Date default hardcoded (`11/04/2026`)
   - ✅ Menggunakan tanggal dari user input

8. **bookingsviewmodels.kt**
   - ❌ Dihapus: Email default dummy (`user@mail.com`)
   - ❌ Dihapus: Tanggal hardcoded (`2026-05-19`)
   - ✅ Menggunakan tanggal hari ini untuk popular schedules

---

### 2. Perbaikan Koneksi API

#### File yang Diubah:

1. **Retrofit.kt**
   - ✅ Ubah BASE_URL dari `10.10.45.1` ke `10.0.2.2` (untuk emulator)
   - ✅ Tambah timeout configuration (30 detik)
   - ✅ Ubah `get_station.php` dari POST ke GET
   - ✅ Tambah import TimeUnit

2. **AndroidManifest.xml**
   - ✅ Tambah `android:networkSecurityConfig="@xml/network_security_config"`
   - ✅ Permission INTERNET sudah ada
   - ✅ `usesCleartextTraffic="true"` sudah ada

3. **network_security_config.xml** (BARU)
   - ✅ File baru dibuat
   - ✅ Mengizinkan cleartext traffic untuk localhost dan IP lokal
   - ✅ Support untuk berbagai IP: 10.0.2.2, localhost, 127.0.0.1, 192.168.x.x, 10.10.45.1

4. **bookingsviewmodels.kt**
   - ✅ Tambah konversi format tanggal (DD/MM/YYYY → YYYY-MM-DD)
   - ✅ Tambah error handling untuk API calls
   - ✅ Tambah fungsi `selectSchedule()` dan `selectClass()` yang hilang
   - ✅ Gunakan tanggal hari ini untuk `fetchPopularSchedules()`
   - ✅ Tambah import SimpleDateFormat, Locale, Date

---

### 3. File Dokumentasi Baru

1. **API_CONNECTION_GUIDE.md**
   - Panduan lengkap konfigurasi IP
   - Troubleshooting koneksi API
   - Daftar endpoint yang tersedia

2. **TEST_API.md**
   - Cara test API di browser
   - Checklist troubleshooting
   - Sample data SQL
   - Common errors dan solusinya

3. **CHANGES_SUMMARY.md** (file ini)
   - Ringkasan semua perubahan

---

## Cara Menggunakan Aplikasi Setelah Perubahan

### A. Untuk Android Emulator:
1. ✅ IP sudah dikonfigurasi: `http://10.0.2.2/apiwhoosmobile/`
2. Pastikan Laragon running
3. Test API di browser: `http://localhost/apiwhoosmobile/get_station.php`
4. Rebuild aplikasi
5. Jalankan di emulator

### B. Untuk Device Fisik (HP):
1. Cek IP komputer: `ipconfig` di CMD
2. Ubah BASE_URL di `Retrofit.kt`:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100/apiwhoosmobile/"
   ```
   (Ganti dengan IP komputer Anda)
3. Pastikan HP dan komputer di WiFi yang sama
4. Rebuild aplikasi
5. Jalankan di HP

---

## Checklist Sebelum Running

- [ ] Laragon sudah running (Apache + MySQL hijau)
- [ ] Database sudah terisi data (stations, schedules, users)
- [ ] Test API di browser berhasil
- [ ] IP address sudah benar di Retrofit.kt
- [ ] Rebuild aplikasi setelah perubahan
- [ ] Untuk device fisik: HP dan komputer di WiFi yang sama

---

## Endpoint API yang Harus Tersedia

1. ✅ `GET /get_station.php` - Daftar stasiun
2. ✅ `POST /get_schedules.php` - Jadwal kereta
3. ✅ `POST /login.php` - Login
4. ✅ `POST /register.php` - Register
5. ✅ `POST /createbooking.php` - Buat booking
6. ✅ `POST /my_bookings.php` - Riwayat booking

---

## Struktur Database yang Dibutuhkan

### Tabel: `stations`
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- station_name (VARCHAR)

### Tabel: `schedules`
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- train_code (VARCHAR)
- from_station (VARCHAR)
- to_station (VARCHAR)
- departure_time (TIME)
- arrival_time (TIME)
- price (INT)
- date (DATE)

### Tabel: `users`
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- full_name (VARCHAR)
- email (VARCHAR, UNIQUE)
- phone (VARCHAR)
- password (VARCHAR, hashed)

### Tabel: `bookings`
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- user_id (INT, FOREIGN KEY)
- schedule_id (INT, FOREIGN KEY)
- reservation_no (VARCHAR, UNIQUE)
- total_price (INT)
- status (ENUM: 'pending', 'paid', 'completed', 'cancelled')
- created_at (TIMESTAMP)

### Tabel: `passengers`
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- booking_id (INT, FOREIGN KEY)
- full_name (VARCHAR)
- id_number (VARCHAR)
- seat_number (VARCHAR)

---

## Troubleshooting

### Jika API tidak terpanggil:
1. Cek Logcat di Android Studio
2. Filter: `package:mine tag:okhttp`
3. Lihat error message
4. Baca `TEST_API.md` untuk solusi

### Jika data tidak muncul:
1. Test API di browser dulu
2. Pastikan database terisi data
3. Cek format response JSON sesuai dengan model di Retrofit.kt

---

## Next Steps

1. ✅ Test koneksi API
2. ✅ Isi database dengan sample data
3. ✅ Test login/register
4. ✅ Test booking flow
5. ✅ Test payment flow

---

## Kontak & Support

Jika ada masalah:
1. Baca `API_CONNECTION_GUIDE.md`
2. Baca `TEST_API.md`
3. Cek Logcat untuk error details
4. Pastikan semua checklist sudah ✅

---

**Status:** ✅ Siap untuk testing
**Versi:** 1.0.0
**Last Updated:** 20 Mei 2026
