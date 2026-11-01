# Testing API Connection

## 1. Test di Browser (Wajib dilakukan pertama!)

### Test Get Stations
```
http://localhost/apiwhoosmobile/get_station.php
```
**Expected Response:**
```json
[
  {
    "id": 1,
    "station_name": "Halim"
  },
  {
    "id": 2,
    "station_name": "Karawang"
  }
]
```

### Test Get Schedules (dengan Postman atau Browser Extension)
**URL:** `http://localhost/apiwhoosmobile/get_schedules.php`
**Method:** POST
**Body (form-data):**
- from_station: Halim
- to_station: Tegalluar
- date: 2026-05-20

**Expected Response:**
```json
[
  {
    "id": 1,
    "train_code": "G1001",
    "from_station": "Halim",
    "to_station": "Tegalluar",
    "departure_time": "06:40",
    "arrival_time": "07:26",
    "price": 300000
  }
]
```

## 2. Cek IP Address Komputer

### Windows:
1. Buka Command Prompt (CMD)
2. Ketik: `ipconfig`
3. Cari "IPv4 Address" pada adapter WiFi atau Ethernet
4. Contoh output:
```
Wireless LAN adapter Wi-Fi:
   IPv4 Address. . . . . . . . . . . : 192.168.1.100
```

### Gunakan IP ini untuk device fisik!

## 3. Konfigurasi IP di Aplikasi

### Untuk Emulator:
File: `app/src/main/java/com/example/whoos_aplication/remote/Retrofit.kt`
```kotlin
private const val BASE_URL = "http://10.0.2.2/apiwhoosmobile/"
```

### Untuk Device Fisik (HP):
```kotlin
private const val BASE_URL = "http://192.168.1.100/apiwhoosmobile/"
```
*Ganti 192.168.1.100 dengan IP komputer Anda*

## 4. Checklist Troubleshooting

- [ ] Laragon sudah running (Apache hijau)
- [ ] MySQL sudah running (hijau)
- [ ] Test API di browser berhasil
- [ ] IP address sudah benar di Retrofit.kt
- [ ] Device/Emulator dan komputer di jaringan WiFi yang sama (untuk device fisik)
- [ ] Windows Firewall tidak memblokir Apache
- [ ] AndroidManifest.xml sudah ada permission INTERNET
- [ ] network_security_config.xml sudah dibuat
- [ ] Rebuild aplikasi setelah ganti IP

## 5. Cara Melihat Log Error

1. Buka Android Studio
2. Buka tab "Logcat" (biasanya di bawah)
3. Filter dengan: `package:mine tag:okhttp`
4. Jalankan aplikasi
5. Lihat log HTTP request/response

## 6. Common Errors

### "Unable to resolve host"
- **Penyebab:** IP address salah atau tidak bisa diakses
- **Solusi:** Cek IP address, pastikan Laragon running

### "Connection refused"
- **Penyebab:** Apache tidak running atau port 80 digunakan aplikasi lain
- **Solusi:** Start Apache di Laragon, cek port 80

### "Timeout"
- **Penyebab:** Koneksi terlalu lambat atau API tidak merespon
- **Solusi:** Cek API di browser, pastikan database terisi data

### "Empty response" atau "No data"
- **Penyebab:** Database kosong atau query salah
- **Solusi:** Cek database, pastikan ada data di tabel stations dan schedules

## 7. Insert Sample Data (Jika Database Kosong)

Jalankan SQL ini di phpMyAdmin:

```sql
-- Insert Stations
INSERT INTO stations (station_name) VALUES 
('Halim'),
('Karawang'),
('Padalarang'),
('Tegalluar');

-- Insert Schedules
INSERT INTO schedules (train_code, from_station, to_station, departure_time, arrival_time, price, date) VALUES
('G1001', 'Halim', 'Tegalluar', '06:40', '07:26', 300000, '2026-05-20'),
('G1002', 'Tegalluar', 'Halim', '06:40', '07:26', 300000, '2026-05-20'),
('G1003', 'Halim', 'Tegalluar', '07:05', '07:51', 300000, '2026-05-20'),
('G1004', 'Tegalluar', 'Halim', '07:05', '07:51', 300000, '2026-05-20'),
('G1005', 'Halim', 'Tegalluar', '08:00', '08:46', 300000, '2026-05-20');
```
