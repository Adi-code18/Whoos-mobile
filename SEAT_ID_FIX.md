# Fix Seat ID - Kirim seat_id (Int) bukan seat_number (String)

## Masalah
Error SQL: `Data truncated for column 'seat_id' at row 1`

**Penyebab:**
- Aplikasi mengirim `seat_number` (String) seperti "1A" 
- Database expect `seat_id` (Int) seperti 1, 2, 3, dll

## Solusi yang Diterapkan

### 1. **Ubah Model Data**

#### File: `Retrofit.kt`
```kotlin
// SEBELUM
data class PassengerRequest(
    val full_name: String,
    val id_number: String,
    val seat_number: String  // ❌ String
)

// SESUDAH
data class PassengerRequest(
    val full_name: String,
    val id_number: String,
    val seat_id: Int  // ✅ Int
)
```

#### File: `bookingsdata.kt`
```kotlin
data class BookingData(
    ...
    val selectedSeat   : String = "",      // Tetap untuk display "1A"
    val selectedSeatId : Int    = 0,       // ✅ Tambah untuk kirim ke API
    ...
)
```

#### File: `pesengger.kt`
```kotlin
data class Passenger(
    ...
    val seatNumber   : String = "",      // "1A" untuk display
    val seatId       : Int    = 0        // ✅ seat_id dari database
)
```

### 2. **Tambah API Endpoint untuk Get Seats**

#### File: `Retrofit.kt`
```kotlin
data class SeatResponse(
    val id: Int,              // seat_id dari database
    val seat_number: String,  // "1A", "1B", dll
    val wagon_number: String, // "01", "02", dll
    val is_available: Boolean // true/false
)

interface ApiService {
    ...
    @FormUrlEncoded
    @POST("get_seats.php")
    suspend fun getSeats(
        @Field("schedule_id") scheduleId: Int,
        @Field("wagon_number") wagonNumber: String
    ): List<SeatResponse>
}
```

### 3. **Update ViewModel**

#### File: `bookingsviewmodels.kt`

**Tambah fungsi fetch seats:**
```kotlin
var availableSeats = mutableStateListOf<SeatResponse>()
    private set

fun fetchSeats(scheduleId: Int, wagonNumber: String) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getSeats(scheduleId, wagonNumber)
            withContext(Dispatchers.Main) {
                availableSeats.clear()
                availableSeats.addAll(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

**Update selectSeat:**
```kotlin
fun selectSeat(car: String, seat: String, seatId: Int) {
    booking = booking.copy(
        selectedCar = car, 
        selectedSeat = seat,      // "1A" untuk display
        selectedSeatId = seatId   // 1 untuk kirim ke API
    )
    
    // Update passenger dengan seat info
    if (booking.passengers.isNotEmpty()) {
        val updatedPassengers = booking.passengers.mapIndexed { index, p ->
            if (index == 0) {
                p.copy(seatNumber = seat, seatId = seatId)
            } else {
                p
            }
        }
        booking = booking.copy(passengers = updatedPassengers)
    }
}
```

**Update completePayment:**
```kotlin
fun completePayment(onSuccess: () -> Unit, onError: (String) -> Unit) {
    ...
    val passengerRequests = booking.passengers.map { p ->
        PassengerRequest(
            full_name = p.name,
            id_number = p.idNumber,
            seat_id = p.seatId  // ✅ Kirim seat_id (Int)
        )
    }
    ...
}
```

### 4. **Update UI - Pilih Kursi**

#### File: `pilihkursi.kt`

**Fetch seats dari API:**
```kotlin
var selectedSeatId by remember { mutableIntStateOf(0) }

LaunchedEffect(selectedCar, b.scheduleId) {
    if (b.scheduleId > 0) {
        vm.fetchSeats(b.scheduleId, selectedCar)
    }
}

val availableSeatsMap = vm.availableSeats.associate { it.seat_number to it }
```

**Update seat selection:**
```kotlin
val seatData = availableSeatsMap[seat]
val isBooked = seatData?.is_available == false

Box(
    Modifier
        .clickable(enabled = !isBooked) { 
            if (isSelected) {
                selectedSeat = ""
                selectedSeatId = 0
            } else {
                selectedSeat = seat
                selectedSeatId = seatData?.id ?: 0  // ✅ Ambil seat_id
            }
        }
) {
    // Display seat
}
```

**Update save button:**
```kotlin
Button(
    onClick = {
        if (selectedSeat.isNotEmpty() && selectedSeatId > 0) {
            vm.selectSeat(selectedCar, selectedSeat, selectedSeatId)
            nav.popBackStack()
        }
    },
    enabled = selectedSeat.isNotEmpty() && selectedSeatId > 0
) { 
    Text("Simpan Kursi ($selectedSeat)") 
}
```

---

## Database Setup

### 1. **Create Tables**

Jalankan SQL dari file: `SEATS_TABLE.sql`

```sql
-- Table: seats
CREATE TABLE `seats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seat_number` varchar(10) NOT NULL,
  `wagon_number` varchar(5) NOT NULL,
  `wagon_class` enum('First Class','Kelas Bisnis','Kelas Ekonomi Premium'),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_seat` (`seat_number`, `wagon_number`)
);

-- Table: seat_bookings
CREATE TABLE `seat_bookings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seat_id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_seat_schedule` (`seat_id`, `schedule_id`),
  FOREIGN KEY (`seat_id`) REFERENCES `seats`(`id`),
  FOREIGN KEY (`schedule_id`) REFERENCES `schedules`(`id`)
);
```

### 2. **Insert Sample Data**

```sql
-- Insert seats untuk gerbong 01 (First Class)
INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) VALUES
('1A', '01', 'First Class'),
('1C', '01', 'First Class'),
('1F', '01', 'First Class'),
...

-- Initialize seat availability
INSERT INTO `seat_bookings` (`seat_id`, `schedule_id`, `is_available`)
SELECT s.id, sch.id, 1
FROM `seats` s
CROSS JOIN `schedules` sch;
```

### 3. **Update passengers Table**

```sql
ALTER TABLE `passengers` 
CHANGE COLUMN `seat_number` `seat_id` INT(11) NOT NULL,
ADD FOREIGN KEY (`seat_id`) REFERENCES `seats`(`id`);
```

---

## API Setup

### 1. **Copy File PHP**

Copy file `get_seats.php` ke folder: `C:\laragon\www\apiwhoosmobile\`

### 2. **Update Database Config**

Edit `get_seats.php`:
```php
$host = 'localhost';
$dbname = 'whoosh_db';  // Ganti dengan nama database Anda
$username = 'root';
$password = '';
```

### 3. **Test API**

**URL:** `http://localhost/apiwhoosmobile/get_seats.php`

**Method:** POST

**Parameters:**
- schedule_id: 1
- wagon_number: 01

**Expected Response:**
```json
[
  {
    "id": 1,
    "seat_number": "1A",
    "wagon_number": "01",
    "is_available": true
  },
  {
    "id": 2,
    "seat_number": "1C",
    "wagon_number": "01",
    "is_available": true
  },
  ...
]
```

---

## Testing Flow

### 1. **Setup Database**
```
✅ Jalankan SEATS_TABLE.sql
✅ Cek table seats ada data
✅ Cek table seat_bookings ada data
✅ Update table passengers
```

### 2. **Setup API**
```
✅ Copy get_seats.php ke apiwhoosmobile
✅ Update database config
✅ Test di browser/Postman
```

### 3. **Rebuild Aplikasi**
```
✅ Android Studio → Build → Rebuild Project
✅ Run aplikasi
```

### 4. **Test di Aplikasi**
```
✅ Login
✅ Search jadwal
✅ Pilih jadwal
✅ Pilih kelas
✅ Pilih kursi → Harus fetch dari API
✅ Simpan kursi → selectedSeatId harus > 0
✅ Isi data penumpang
✅ Bayar → Harus kirim seat_id (Int)
```

---

## Verification

### Cek di Logcat:
```
--> POST http://10.0.2.2/apiwhoosmobile/get_seats.php
schedule_id=1&wagon_number=01
<-- 200 OK
[{"id":1,"seat_number":"1A","wagon_number":"01","is_available":true}, ...]
```

### Cek Request Body saat Booking:
```json
{
  "user_id": 1,
  "schedule_id": 1,
  "total_price": 300000,
  "passengers": [
    {
      "full_name": "John Doe",
      "id_number": "1234567890",
      "seat_id": 1  // ✅ Int, bukan "1A"
    }
  ]
}
```

---

## Troubleshooting

### ❌ Error: "Data truncated for column 'seat_id'"
**Penyebab:** Masih kirim String "1A"
**Solusi:** 
- Cek `PassengerRequest` sudah `seat_id: Int`
- Cek `completePayment` kirim `p.seatId` bukan `p.seatNumber`

### ❌ Seats tidak muncul / semua tersedia
**Penyebab:** API tidak dipanggil atau database kosong
**Solusi:**
- Cek Logcat untuk HTTP request
- Cek database table `seats` dan `seat_bookings`
- Test API di browser

### ❌ selectedSeatId = 0
**Penyebab:** Seat tidak ada di API response
**Solusi:**
- Cek `availableSeatsMap` ada data
- Cek `seatData?.id` tidak null

---

## Summary

### Perubahan:
1. ✅ `PassengerRequest.seat_number` → `seat_id` (Int)
2. ✅ `BookingData` tambah `selectedSeatId`
3. ✅ `Passenger` tambah `seatId`
4. ✅ Tambah `SeatResponse` model
5. ✅ Tambah `getSeats()` API endpoint
6. ✅ Tambah `fetchSeats()` di ViewModel
7. ✅ Update `selectSeat()` terima `seatId`
8. ✅ Update `completePayment()` kirim `seat_id`
9. ✅ Update `pilihkursi.kt` fetch & display seats dari API

### Database:
1. ✅ Create table `seats`
2. ✅ Create table `seat_bookings`
3. ✅ Insert sample seats data
4. ✅ Update table `passengers`

### API:
1. ✅ Create `get_seats.php`

**Status:** ✅ Siap untuk testing
