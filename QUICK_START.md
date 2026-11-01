# 🚀 Quick Start Guide - Whoosh Application

## ⚡ Langkah Cepat (5 Menit)

### 1️⃣ Start Laragon
```
✅ Buka Laragon
✅ Klik "Start All"
✅ Pastikan Apache dan MySQL hijau
```

### 2️⃣ Test API di Browser
Buka browser, akses:
```
http://localhost/apiwhoosmobile/get_station.php
```

**Jika muncul JSON data stasiun = ✅ API OK!**

**Jika error 404 atau blank:**
- Cek folder `C:\laragon\www\apiwhoosmobile` ada atau tidak
- Pastikan file `get_station.php` ada di folder tersebut

### 3️⃣ Cek IP Address (Untuk Device Fisik)

**Jika pakai Emulator → Skip langkah ini!**

**Jika pakai HP:**
1. Buka CMD
2. Ketik: `ipconfig`
3. Cari "IPv4 Address" di WiFi adapter
4. Catat IP-nya (contoh: 192.168.1.100)

### 4️⃣ Set IP di Aplikasi

**File:** `app/src/main/java/com/example/whoos_aplication/remote/Retrofit.kt`

**Untuk Emulator (sudah benar):**
```kotlin
private const val BASE_URL = "http://10.0.2.2/apiwhoosmobile/"
```

**Untuk HP (ganti dengan IP Anda):**
```kotlin
private const val BASE_URL = "http://192.168.1.100/apiwhoosmobile/"
```

### 5️⃣ Rebuild & Run
```
✅ Android Studio → Build → Rebuild Project
✅ Run aplikasi
```

---

## 🔍 Cek Database Ada Data atau Tidak

### Buka phpMyAdmin:
```
http://localhost/phpmyadmin
```

### Cek Tabel:
1. **stations** → Harus ada data (Halim, Karawang, dll)
2. **schedules** → Harus ada data jadwal kereta
3. **users** → Boleh kosong (akan terisi saat register)

### Jika Kosong, Insert Sample Data:

```sql
-- Insert Stations
INSERT INTO stations (station_name) VALUES 
('Halim'),
('Karawang'),
('Padalarang'),
('Tegalluar');

-- Insert Schedules (ganti tanggal sesuai kebutuhan)
INSERT INTO schedules (train_code, from_station, to_station, departure_time, arrival_time, price, date) VALUES
('G1001', 'Halim', 'Tegalluar', '06:40:00', '07:26:00', 300000, '2026-05-20'),
('G1002', 'Tegalluar', 'Halim', '06:40:00', '07:26:00', 300000, '2026-05-20'),
('G1003', 'Halim', 'Tegalluar', '07:05:00', '07:51:00', 300000, '2026-05-20'),
('G1004', 'Tegalluar', 'Halim', '07:05:00', '07:51:00', 300000, '2026-05-20'),
('G1005', 'Halim', 'Tegalluar', '08:00:00', '08:46:00', 300000, '2026-05-20'),
('G1006', 'Halim', 'Tegalluar', '09:00:00', '09:46:00', 300000, '2026-05-20'),
('G1007', 'Tegalluar', 'Halim', '09:00:00', '09:46:00', 300000, '2026-05-20');
```

---

## 🐛 Troubleshooting Cepat

### ❌ "Unable to resolve host"
**Penyebab:** IP salah atau Laragon tidak running
**Solusi:**
1. Pastikan Laragon running
2. Test API di browser dulu
3. Cek IP di Retrofit.kt

### ❌ "Connection refused"
**Penyebab:** Apache tidak running
**Solusi:**
1. Start Apache di Laragon
2. Cek port 80 tidak dipakai aplikasi lain (Skype, IIS, dll)

### ❌ "Empty response" / Tidak ada data
**Penyebab:** Database kosong
**Solusi:**
1. Buka phpMyAdmin
2. Insert sample data (lihat SQL di atas)

### ❌ Aplikasi crash saat buka
**Penyebab:** Error di code
**Solusi:**
1. Buka Logcat di Android Studio
2. Cari error message
3. Screenshot dan cek error-nya

---

## 📱 Testing Flow

### 1. Register User Baru
```
✅ Buka aplikasi
✅ Klik "Daftar"
✅ Isi form register
✅ Klik "DAFTAR SEKARANG"
✅ Jika berhasil → akan ke Dashboard
```

### 2. Login
```
✅ Masukkan email & password
✅ Klik "LOGIN"
✅ Jika berhasil → Dashboard
```

### 3. Search Jadwal
```
✅ Pilih From: Halim
✅ Pilih To: Tegalluar
✅ Pilih tanggal (sesuai data di database)
✅ Klik "Search"
✅ Harus muncul list jadwal
```

### 4. Booking
```
✅ Pilih jadwal
✅ Pilih kelas
✅ Klik "Book"
✅ Isi data penumpang
✅ Pilih kursi
✅ Bayar
```

---

## 📊 Cek Log di Android Studio

### Cara Buka Logcat:
1. Android Studio → Tab "Logcat" (bawah)
2. Filter: `package:mine`
3. Atau filter: `okhttp` untuk lihat HTTP request

### Yang Harus Muncul:
```
--> GET http://10.0.2.2/apiwhoosmobile/get_station.php
<-- 200 OK (response time)
[{"id":1,"station_name":"Halim"}, ...]
```

---

## ✅ Checklist Akhir

Sebelum testing, pastikan:

- [ ] Laragon running (Apache + MySQL hijau)
- [ ] Database ada data (stations & schedules)
- [ ] Test API di browser berhasil
- [ ] IP di Retrofit.kt sudah benar
- [ ] Rebuild aplikasi
- [ ] Untuk HP: HP dan komputer di WiFi yang sama

---

## 📚 Dokumentasi Lengkap

Jika masih ada masalah, baca:
1. `API_CONNECTION_GUIDE.md` - Panduan koneksi API
2. `TEST_API.md` - Testing & troubleshooting
3. `CHANGES_SUMMARY.md` - Ringkasan perubahan

---

## 🎯 Expected Result

Setelah semua langkah di atas:
- ✅ Aplikasi bisa dibuka
- ✅ Dashboard muncul
- ✅ Popular schedules muncul (jika ada data)
- ✅ Bisa search jadwal
- ✅ Bisa booking tiket
- ✅ Bisa lihat riwayat booking

---

**Good Luck! 🚀**

Jika ada error, screenshot dan cek Logcat untuk detail error message.
