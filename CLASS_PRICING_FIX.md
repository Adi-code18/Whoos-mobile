# Fix Nama Kelas dan Harga Tiket

## Masalah
1. **Nama kelas tidak konsisten** antara database dan aplikasi Android
2. **Harga tidak sesuai multiplier** yang sudah diatur di admin panel
3. **Data hardcoded** di aplikasi, tidak mengambil dari API

## Solusi yang Diimplementasikan

### 1. Database Setup
**File**: `WAGON_CLASSES_TABLE.sql`

Buat tabel `wagon_classes` dengan struktur:
```sql
CREATE TABLE wagon_classes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  class_name VARCHAR(50) UNIQUE,
  price_multiplier DECIMAL(3,2),
  description TEXT
);

INSERT INTO wagon_classes VALUES
(1, 'Ekonomi', 1.00, 'Kelas ekonomi dengan fasilitas standar'),
(2, 'Eksekutif', 1.50, 'Kelas eksekutif 50% lebih mahal'),
(3, 'Busines', 1.70, 'Kelas bisnis 70% lebih mahal');
```

### 2. API Endpoint
**File**: `get_classes.php`

Endpoint baru untuk ambil data kelas:
```
GET /apiwhoosmobile/get_classes.php
```

Response:
```json
[
  {
    "id": 1,
    "class_name": "Ekonomi",
    "price_multiplier": 1.0,
    "description": "Kelas ekonomi dengan fasilitas standar"
  },
  {
    "id": 2,
    "class_name": "Eksekutif",
    "price_multiplier": 1.5,
    "description": "Kelas eksekutif 50% lebih mahal"
  },
  {
    "id": 3,
    "class_name": "Busines",
    "price_multiplier": 1.7,
    "description": "Kelas bisnis 70% lebih mahal"
  }
]
```

### 3. Android App Changes

#### A. Model (Retrofit.kt)
Tambah model baru:
```kotlin
data class WagonClassResponse(
    val id: Int,
    val class_name: String,
    val price_multiplier: Float,
    val description: String
)
```

Tambah endpoint:
```kotlin
@GET("get_classes.php")
suspend fun getWagonClasses(): List<WagonClassResponse>
```

#### B. ViewModel (bookingsviewmodels.kt)
1. Tambah state untuk wagon classes:
```kotlin
val wagonClasses = mutableStateListOf<WagonClassResponse>()
```

2. Fetch data saat init:
```kotlin
init {
    fetchWagonClasses()
}
```

3. Tambah fungsi helper untuk hitung harga:
```kotlin
fun calculatePrice(className: String, basePrice: Int): Int {
    val wagonClass = wagonClasses.find { 
        it.class_name.equals(className, ignoreCase = true) 
    }
    return if (wagonClass != null) {
        (basePrice * wagonClass.price_multiplier).toInt()
    } else {
        // Fallback
        when {
            className.contains("Ekonomi") -> basePrice
            className.contains("Eksekutif") -> (basePrice * 1.5).toInt()
            className.contains("Busines") -> (basePrice * 1.7).toInt()
            else -> basePrice
        }
    }
}
```

#### C. UI Screens

**Search Result (searchresult.kt)**
- Gunakan `vm.wagonClasses` untuk list kelas
- Hitung harga dengan `s.price * wc.price_multiplier`
- Fallback ke hardcoded jika API gagal

**Pemesanan (pemesanan.kt)**
- Gunakan `vm.wagonClasses.map { it.class_name }` untuk list kelas
- Hitung harga dengan `vm.calculatePrice(selectedClass, basePrice)`
- Display harga yang sudah dikalikan multiplier

## Cara Penggunaan

### 1. Setup Database
```bash
# Buka phpMyAdmin
# Pilih database whoosh_db
# Jalankan query dari WAGON_CLASSES_TABLE.sql
```

### 2. Deploy API
```bash
# Copy get_classes.php ke folder API
cp get_classes.php C:\laragon\www\apiwhoosmobile\
```

### 3. Test API
```bash
# Buka browser atau Postman
http://10.0.2.2/apiwhoosmobile/get_classes.php

# Expected response: array of 3 classes
```

### 4. Run Android App
```bash
# Build dan run aplikasi
# Aplikasi akan otomatis fetch wagon classes saat startup
```

## Mapping Nama Kelas

| Database (Baru) | Aplikasi (Lama) | Multiplier |
|-----------------|-----------------|------------|
| Ekonomi | Kelas Ekonomi Premium | 1.0 (100%) |
| Eksekutif | First Class | 1.5 (150%) |
| Busines | Kelas Bisnis | 1.7 (170%) |

## Contoh Perhitungan Harga

Jika harga dasar (ekonomi) dari schedule = Rp 295.000:

| Kelas | Multiplier | Harga |
|-------|-----------|-------|
| Ekonomi | 1.0 | Rp 295.000 |
| Eksekutif | 1.5 | Rp 442.500 (50% lebih mahal) |
| Busines | 1.7 | Rp 501.500 (70% lebih mahal) |

## Testing Checklist

### Database
- [ ] Tabel `wagon_classes` sudah dibuat
- [ ] Ada 3 rows: Ekonomi (1.0), Eksekutif (1.5), Busines (1.7)
- [ ] Query `SELECT * FROM wagon_classes` berhasil

### API
- [ ] File `get_classes.php` sudah di-deploy
- [ ] API bisa diakses via browser
- [ ] Response JSON valid dengan 3 classes

### Android App
- [ ] Build berhasil tanpa error
- [ ] Logcat menampilkan: "Loaded 3 wagon classes from API"
- [ ] Search result menampilkan 3 kelas dengan nama yang benar
- [ ] Harga sesuai dengan multiplier (Eksekutif 50% lebih mahal, Busines 70%)
- [ ] Pemesanan screen menampilkan harga yang benar
- [ ] Ticket history menampilkan nama kelas yang benar

## Troubleshooting

### API tidak bisa diakses
```bash
# Cek apakah Laragon running
# Cek file get_classes.php ada di folder yang benar
# Cek BASE_URL di Retrofit.kt
```

### Aplikasi masih pakai nama lama
```bash
# Cek Logcat: "Failed to load wagon classes from API"
# Berarti API gagal, aplikasi pakai fallback
# Perbaiki koneksi API terlebih dahulu
```

### Harga tidak sesuai
```bash
# Cek multiplier di database
# Cek base price dari schedule
# Cek fungsi calculatePrice() di ViewModel
```

## Files Modified
1. ✅ `get_classes.php` - API endpoint baru
2. ✅ `Retrofit.kt` - Tambah model dan endpoint
3. ✅ `bookingsviewmodels.kt` - Fetch classes dan calculate price
4. ✅ `searchresult.kt` - Gunakan data dari API
5. ✅ `pemesanan.kt` - Gunakan data dari API
6. ✅ `WAGON_CLASSES_TABLE.sql` - Database schema

## Notes
- Aplikasi punya fallback jika API gagal
- Nama kelas sekarang konsisten dengan admin panel
- Harga otomatis dihitung berdasarkan multiplier
- Mudah untuk tambah kelas baru di database
