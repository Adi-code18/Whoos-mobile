# Panduan Koneksi API Whoosh

## Konfigurasi IP Address

File konfigurasi: `app/src/main/java/com/example/whoos_aplication/remote/Retrofit.kt`

### Untuk Android Emulator:
```kotlin
private const val BASE_URL = "http://10.0.2.2/apiwhoosmobile/"
```
**Catatan:** `10.0.2.2` adalah IP khusus yang mengarah ke `localhost` komputer host dari Android Emulator.

### Untuk Device Fisik (HP):
1. Cek IP komputer Anda:
   - Buka Command Prompt (CMD)
   - Ketik: `ipconfig`
   - Cari "IPv4 Address" pada adapter yang aktif (biasanya WiFi atau Ethernet)
   - Contoh: `192.168.1.100`

2. Ubah BASE_URL menjadi:
```kotlin
private const val BASE_URL = "http://192.168.1.100/apiwhoosmobile/"
```

3. **Pastikan HP dan Komputer terhubung ke jaringan WiFi yang sama!**

### Untuk Testing dengan IP Lain:
Jika Anda menggunakan IP `10.10.45.1`, ubah menjadi:
```kotlin
private const val BASE_URL = "http://10.10.45.1/apiwhoosmobile/"
```

## Cara Mengganti IP Address

1. Buka file: `app/src/main/java/com/example/whoos_aplication/remote/Retrofit.kt`
2. Cari baris: `private const val BASE_URL = "http://10.0.2.2/apiwhoosmobile/"`
3. Ganti IP sesuai kebutuhan
4. Rebuild aplikasi

## Troubleshooting

### API Tidak Terpanggil / Timeout
1. **Cek Laragon sudah running**
   - Buka Laragon
   - Pastikan Apache dan MySQL sudah start (hijau)

2. **Test API di Browser**
   - Buka browser
   - Akses: `http://localhost/apiwhoosmobile/get_station.php`
   - Jika berhasil, akan muncul data JSON

3. **Cek Firewall**
   - Pastikan Windows Firewall tidak memblokir koneksi
   - Izinkan Apache untuk akses network

4. **Cek Network Security Config**
   - File sudah dibuat di: `app/src/main/res/xml/network_security_config.xml`
   - AndroidManifest.xml sudah dikonfigurasi

### Error "Unable to resolve host"
- Pastikan IP address benar
- Pastikan device/emulator terhubung ke internet
- Untuk device fisik, pastikan di jaringan WiFi yang sama dengan komputer

### Error "Connection Refused"
- Pastikan Laragon sudah running
- Pastikan port 80 tidak digunakan aplikasi lain
- Test dengan browser terlebih dahulu

## Endpoint API yang Tersedia

1. **GET** `/get_station.php` - Mendapatkan daftar stasiun
2. **POST** `/get_schedules.php` - Mendapatkan jadwal kereta
   - Parameters: `from_station`, `to_station`, `date`
3. **POST** `/login.php` - Login user
   - Parameters: `email`, `password`
4. **POST** `/register.php` - Register user baru
   - Parameters: `full_name`, `email`, `phone`, `password`
5. **POST** `/createbooking.php` - Membuat booking baru
   - Body: JSON dengan `user_id`, `schedule_id`, `total_price`, `passengers`
6. **POST** `/my_bookings.php` - Mendapatkan riwayat booking
   - Parameters: `email`

## Tips

- Gunakan Logcat untuk melihat log HTTP request/response
- HttpLoggingInterceptor sudah diaktifkan untuk debugging
- Timeout sudah diset 30 detik untuk koneksi yang lambat
