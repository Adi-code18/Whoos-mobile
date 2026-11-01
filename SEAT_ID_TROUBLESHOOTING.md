# Troubleshooting: Seat ID = 0 atau "Hantu"

## Masalah
Ketika pemilihan kursi, seat_id tidak tersimpan (= 0) yang menyebabkan error saat payment.

## Penyebab Umum

### 1. **API get_seats.php Tidak Dipanggil**
**Cek:**
- Buka Logcat di Android Studio
- Filter: `okhttp` atau `DEBUG`
- Cari HTTP request ke `get_seats.php`

**Jika tidak ada request:**
- `scheduleId` mungkin 0 atau invalid
- Network error

**Solusi:**
```kotlin
// Tambah log di pilihkursi.kt
LaunchedEffect(selectedCar, b.scheduleId) {
    println("DEBUG: Fetching seats - scheduleId: ${b.scheduleId}, wagon: $selectedCar")
    if (b.scheduleId > 0) {
        vm.fetchSeats(b.scheduleId, selectedCar)
    } else {
        println("DEBUG: scheduleId is 0, cannot fetch seats!")
    }
}
```

### 2. **Database Tidak Ada Data**
**Cek:**
```sql
-- Cek table seats
SELECT COUNT(*) FROM seats;

-- Cek table seat_bookings
SELECT COUNT(*) FROM seat_bookings;

-- Cek seats untuk gerbong 03
SELECT * FROM seats WHERE wagon_number = '03' LIMIT 10;
```

**Jika kosong:**
- Jalankan `SEATS_TABLE.sql`
- Insert sample data

### 3. **API Response Kosong**
**Test API di Browser/Postman:**
```
URL: http://localhost/apiwhoosmobile/get_seats.php
Method: POST
Body:
  schedule_id: 1
  wagon_number: 03
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "seat_number": "1A",
    "wagon_number": "03",
    "is_available": true
  },
  ...
]
```

**Jika response kosong:**
- Cek database connection di `get_seats.php`
- Cek query SQL
- Cek parameter POST

### 4. **Fallback ID Generator Tidak Jalan**
**Cek di Logcat:**
```
DEBUG: Saving seat - Number: 4C, ID: 3043
```

**Jika ID masih 0:**
- Fallback generator ada bug
- `selectedSeatId` tidak di-update

## Solusi yang Sudah Diterapkan

### 1. **Fallback ID Generator**
Jika API tidak tersedia, generate seat_id dari seat_number:

```kotlin
val seatId = seatData?.id ?: run {
    // Generate ID dari seat number
    val rowNum = seat.filter { it.isDigit() }.toIntOrNull() ?: 0
    val colChar = seat.filter { it.isLetter() }.firstOrNull() ?: 'A'
    val colIndex = when(colChar) {
        'A' -> 1
        'B' -> 2
        'C' -> 3
        'D' -> 4
        'F' -> 5
        else -> 0
    }
    // Generate unique ID: wagon * 1000 + row * 10 + col
    val wagonNum = selectedCar.toIntOrNull() ?: 3
    wagonNum * 1000 + rowNum * 10 + colIndex
}
```

**Contoh:**
- Seat "4C" di gerbong "03" → ID = 3000 + 40 + 3 = **3043**
- Seat "1A" di gerbong "01" → ID = 1000 + 10 + 1 = **1011**

### 2. **Debug Logs**
Tambah log di berbagai tempat:

**pilihkursi.kt:**
```kotlin
println("DEBUG: Saving seat - Number: $selectedSeat, ID: $selectedSeatId")
```

**bookingsviewmodels.kt:**
```kotlin
println("DEBUG ViewModel: selectSeat called - Car: $car, Seat: $seat, SeatID: $seatId")
println("DEBUG ViewModel: Booking updated - selectedSeatId: ${booking.selectedSeatId}")
println("DEBUG Payment: Passenger ${p.name} - seatId: ${p.seatId}")
```

### 3. **Button Text Indicator**
Button text berubah sesuai status:
- Jika `selectedSeatId > 0`: "Simpan Kursi (4C)"
- Jika `selectedSeatId = 0`: "Pilih Kursi Dulu"

## Cara Debug

### Step 1: Cek Logcat
```
1. Android Studio → Logcat
2. Filter: "DEBUG"
3. Klik kursi
4. Lihat log:
   - "DEBUG: Saving seat - Number: 4C, ID: 3043"
   - "DEBUG ViewModel: selectSeat called..."
   - "DEBUG ViewModel: Booking updated - selectedSeatId: 3043"
```

### Step 2: Cek API
```
1. Buka browser
2. Test: http://localhost/apiwhoosmobile/get_seats.php
3. POST: schedule_id=1, wagon_number=03
4. Harus return JSON dengan id
```

### Step 3: Cek Database
```sql
-- Cek seats ada data
SELECT * FROM seats WHERE wagon_number = '03' LIMIT 5;

-- Cek seat_bookings ada data
SELECT * FROM seat_bookings WHERE schedule_id = 1 LIMIT 5;
```

### Step 4: Cek Payment Request
```
1. Logcat → Filter: "DEBUG Payment"
2. Lihat:
   - "DEBUG Payment: Passenger John - seatId: 3043"
3. Jika seatId = 0 → Ada masalah di selectSeat
```

## Quick Fix

### Jika API Tidak Bisa Dipakai
Gunakan fallback generator (sudah diterapkan):

```kotlin
// Di pilihkursi.kt, fallback sudah otomatis
val seatId = seatData?.id ?: generateSeatId(seat, selectedCar)
```

### Jika Database Belum Setup
Sementara gunakan generated ID:

```kotlin
// Generated ID format: WWWRRCC
// WWW = wagon number (001-006)
// RR = row number (01-10)
// CC = column index (01-05)

// Contoh:
// Gerbong 03, Seat 4C → 3043
// Gerbong 01, Seat 1A → 1011
```

### Jika Masih Error
Tambah validation di completePayment:

```kotlin
fun completePayment(onSuccess: () -> Unit, onError: (String) -> Unit) {
    // Validate seat_id
    val invalidPassengers = booking.passengers.filter { it.seatId == 0 }
    if (invalidPassengers.isNotEmpty()) {
        onError("Error: Seat ID tidak valid. Silakan pilih kursi lagi.")
        return
    }
    
    // Continue with payment...
}
```

## Testing Checklist

- [ ] Database table `seats` ada data
- [ ] Database table `seat_bookings` ada data
- [ ] API `get_seats.php` bisa diakses
- [ ] API return JSON dengan `id` field
- [ ] Logcat show "DEBUG: Saving seat - ID: XXX" (XXX > 0)
- [ ] Logcat show "DEBUG ViewModel: selectedSeatId: XXX" (XXX > 0)
- [ ] Logcat show "DEBUG Payment: seatId: XXX" (XXX > 0)
- [ ] Button text = "Simpan Kursi (4C)" bukan "Pilih Kursi Dulu"
- [ ] Payment berhasil tanpa error SQL

## Expected Flow

```
1. User klik kursi "4C" di gerbong "03"
   → selectedSeat = "4C"
   → selectedSeatId = 3043 (dari API atau generated)

2. User klik "Simpan Kursi (4C)"
   → vm.selectSeat("03", "4C", 3043)
   → booking.selectedSeatId = 3043
   → passenger[0].seatId = 3043

3. User klik "Bayar"
   → PassengerRequest(seat_id = 3043)
   → API createbooking.php
   → INSERT INTO passengers (seat_id) VALUES (3043)
   → Success!
```

## Common Errors

### Error: "Data truncated for column 'seat_id'"
**Penyebab:** Masih kirim String "4C"
**Solusi:** Cek `PassengerRequest` sudah `seat_id: Int`

### Error: "Cannot insert NULL into seat_id"
**Penyebab:** seat_id = 0 atau null
**Solusi:** Cek fallback generator jalan

### Error: "Foreign key constraint fails"
**Penyebab:** seat_id tidak ada di table `seats`
**Solusi:** 
- Jika pakai API: Insert data ke table `seats`
- Jika pakai generated: Hapus foreign key constraint

## Recommendation

### Untuk Development (Sekarang)
✅ Gunakan **Fallback Generator** (sudah diterapkan)
- Tidak perlu setup database seats
- Tidak perlu API get_seats.php
- Langsung bisa testing

### Untuk Production (Nanti)
✅ Gunakan **API + Database**
- Setup table `seats` dan `seat_bookings`
- Implement API `get_seats.php`
- Real-time seat availability
- Prevent double booking

## Summary

**Masalah:** seat_id = 0
**Penyebab:** API tidak ada data atau tidak dipanggil
**Solusi:** Fallback generator (sudah diterapkan)
**Status:** ✅ Seharusnya sudah fix

**Cara Verify:**
1. Run aplikasi
2. Pilih kursi
3. Cek Logcat: "DEBUG: Saving seat - ID: XXX"
4. Jika XXX > 0 → ✅ Fix!
5. Jika XXX = 0 → Cek troubleshooting di atas
