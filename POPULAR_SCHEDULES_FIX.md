# Fix Popular Schedules - Menghilangkan Data Dummy

## Masalah
Popular Schedules di Dashboard masih menggunakan data dummy (hardcoded), tidak mengambil dari database.

## Solusi yang Diterapkan

### 1. **Perhitungan Durasi Otomatis**
File: `dashboardscreens.kt`

**Sebelum:**
```kotlin
val popularSchedules = vm.popularSchedules.map { s ->
    ScheduleCardItem(
        trainCode = s.train_code,
        from      = s.from_station,
        to        = s.to_station,
        depTime   = s.departure_time,
        arrTime   = s.arrival_time,
        duration  = "46m", // ❌ HARDCODED!
        price     = WhooshUtils.formatRupiah(s.price)
    )
}
```

**Sesudah:**
```kotlin
val popularSchedules = vm.popularSchedules.map { s ->
    // ✅ Hitung durasi dari departure_time dan arrival_time
    val duration = try {
        val depParts = s.departure_time.split(":")
        val arrParts = s.arrival_time.split(":")
        val depMinutes = depParts[0].toInt() * 60 + depParts[1].toInt()
        val arrMinutes = arrParts[0].toInt() * 60 + arrParts[1].toInt()
        val diffMinutes = if (arrMinutes >= depMinutes) {
            arrMinutes - depMinutes
        } else {
            (24 * 60 - depMinutes) + arrMinutes
        }
        "${diffMinutes}m"
    } catch (e: Exception) {
        "N/A"
    }
    
    ScheduleCardItem(
        trainCode = s.train_code,
        from      = s.from_station,
        to        = s.to_station,
        depTime   = s.departure_time,
        arrTime   = s.arrival_time,
        duration  = duration, // ✅ CALCULATED!
        price     = WhooshUtils.formatRupiah(s.price)
    )
}
```

### 2. **Auto-fill Tanggal saat Klik Book**
File: `dashboardscreens.kt`

**Sebelum:**
```kotlin
SchedulePopularCard(item = s) {
    vm.updateForm(stationKey(s.from), stationKey(s.to), dateRaw, quantity)
    // ❌ dateRaw bisa kosong!
    nav.navigate(Screen.SearchResult.route)
}
```

**Sesudah:**
```kotlin
SchedulePopularCard(item = s) {
    // ✅ Jika user belum pilih tanggal, gunakan tanggal hari ini
    val selectedDate = if (dateRaw.isEmpty()) {
        val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
        today
    } else {
        dateRaw
    }
    vm.updateForm(stationKey(s.from), stationKey(s.to), selectedDate, quantity)
    nav.navigate(Screen.SearchResult.route)
}
```

### 3. **Fetch Data dari API**
File: `bookingsviewmodels.kt`

Data popular schedules diambil dari API saat aplikasi dibuka:

```kotlin
fun fetchPopularSchedules() {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            // ✅ Mengambil jadwal dari database untuk hari ini
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date())
            val response = RetrofitClient.instance.getSchedules("Halim", "Tegalluar", today)
            withContext(Dispatchers.Main) {
                popularSchedules.clear()
                popularSchedules.addAll(response.take(5)) // Ambil 5 jadwal pertama
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                popularSchedules.clear()
            }
        }
    }
}
```

---

## Cara Kerja Sekarang

### Flow Popular Schedules:

1. **Aplikasi Dibuka**
   - `BookingViewModel` init
   - Panggil `fetchPopularSchedules()`
   - API call: `GET /get_schedules.php?from_station=Halim&to_station=Tegalluar&date=2026-05-20`

2. **API Response**
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
     },
     ...
   ]
   ```

3. **Data Ditampilkan**
   - Train Code: G1001 (dari database)
   - From: Halim (dari database)
   - To: Tegalluar (dari database)
   - Departure: 06:40 (dari database)
   - Arrival: 07:26 (dari database)
   - Duration: 46m (dihitung otomatis: 07:26 - 06:40 = 46 menit)
   - Price: Rp.300.000 (dari database)

4. **User Klik "Book"**
   - Jika user belum pilih tanggal → gunakan tanggal hari ini
   - Navigate ke SearchResult dengan parameter dari database

---

## Checklist Verifikasi

### ✅ Database Harus Terisi Data

1. **Buka phpMyAdmin**: `http://localhost/phpmyadmin`

2. **Cek Tabel `schedules`**:
   ```sql
   SELECT * FROM schedules 
   WHERE from_station = 'Halim' 
   AND to_station = 'Tegalluar' 
   AND date = CURDATE()
   ORDER BY departure_time;
   ```

3. **Jika Kosong, Insert Sample Data**:
   - Buka file: `SAMPLE_DATA.sql`
   - Copy semua SQL
   - Paste di phpMyAdmin
   - Execute

### ✅ API Endpoint Harus Berfungsi

1. **Test di Browser**:
   ```
   http://localhost/apiwhoosmobile/get_schedules.php
   ```
   
2. **Test dengan Postman**:
   - Method: POST
   - URL: `http://localhost/apiwhoosmobile/get_schedules.php`
   - Body (form-data):
     - from_station: Halim
     - to_station: Tegalluar
     - date: 2026-05-20

3. **Expected Response**:
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

### ✅ Aplikasi Harus Terhubung ke API

1. **Cek Retrofit.kt**:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2/apiwhoosmobile/"
   ```

2. **Rebuild Aplikasi**:
   - Android Studio → Build → Rebuild Project

3. **Jalankan Aplikasi**:
   - Run di Emulator atau Device

4. **Cek Logcat**:
   - Filter: `okhttp`
   - Harus muncul HTTP request ke API

---

## Troubleshooting

### ❌ Popular Schedules Kosong

**Penyebab:**
1. Database tidak ada data untuk tanggal hari ini
2. API tidak berfungsi
3. Koneksi ke API gagal

**Solusi:**
1. Insert sample data (lihat `SAMPLE_DATA.sql`)
2. Test API di browser
3. Cek Logcat untuk error message

### ❌ Duration Salah

**Penyebab:**
- Format time di database salah

**Solusi:**
- Pastikan format time di database: `HH:MM:SS` (contoh: `06:40:00`)
- Atau `HH:MM` (contoh: `06:40`)

### ❌ Klik "Book" Error

**Penyebab:**
- Tanggal tidak valid
- Station name tidak match

**Solusi:**
- Cek `stationKey()` function
- Pastikan station name di database sama dengan yang di aplikasi

---

## Testing

### Test Case 1: Popular Schedules Muncul
1. Buka aplikasi
2. Dashboard harus muncul
3. Scroll ke bawah
4. Section "Popular Schedules" harus muncul
5. Harus ada 5 card jadwal (jika database ada data)

### Test Case 2: Data dari Database
1. Cek train code di card
2. Harus sesuai dengan data di database
3. Cek harga
4. Harus sesuai dengan data di database

### Test Case 3: Duration Dihitung Otomatis
1. Lihat duration di card
2. Hitung manual: arrival_time - departure_time
3. Harus sama

### Test Case 4: Klik Book
1. Klik tombol "Book" di popular schedule
2. Harus navigate ke SearchResult
3. Harus muncul list jadwal
4. Jadwal harus sesuai dengan yang diklik

---

## Summary

✅ **Data dummy dihilangkan**
✅ **Duration dihitung otomatis dari database**
✅ **Tanggal auto-fill jika kosong**
✅ **Semua data dari API/database**

**Status:** ✅ Siap untuk testing
**File SQL:** `SAMPLE_DATA.sql` (untuk insert sample data)
