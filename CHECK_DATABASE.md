# Database Setup Checklist

## PENTING: Jalankan Query Ini di phpMyAdmin

Sebelum aplikasi bisa berjalan, database HARUS sudah disetup dengan benar.

### 1. Buka phpMyAdmin
- URL: http://localhost/phpmyadmin
- Database: `whoosh_db` (atau nama database kamu)

### 2. Jalankan Query Ini (Copy-Paste ke SQL Tab)

```sql
-- Cek apakah tabel seats sudah ada
SELECT COUNT(*) as total_seats FROM seats;

-- Jika error "Table doesn't exist", jalankan file SEATS_TABLE.sql
-- Jika berhasil, harusnya ada 230 seats (15+15+50+50+50+50)
```

### 3. Jika Tabel Belum Ada
1. Buka file `SEATS_TABLE.sql`
2. Copy SEMUA isinya
3. Paste ke SQL tab di phpMyAdmin
4. Klik "Go"

### 4. Verifikasi Data Seats
```sql
-- Cek seats per gerbong
SELECT wagon_number, COUNT(*) as total 
FROM seats 
GROUP BY wagon_number;

-- Harusnya hasilnya:
-- 01: 15 seats
-- 02: 15 seats
-- 03: 50 seats
-- 04: 50 seats
-- 05: 50 seats
-- 06: 50 seats
```

### 5. Cek Struktur Tabel passengers
```sql
DESCRIBE passengers;

-- Pastikan ada kolom seat_id (INT), bukan seat_number (VARCHAR)
-- Jika masih seat_number, jalankan:

ALTER TABLE passengers 
DROP FOREIGN KEY IF EXISTS passengers_ibfk_2;

ALTER TABLE passengers 
CHANGE COLUMN seat_number seat_id INT(11) NOT NULL;

ALTER TABLE passengers 
ADD CONSTRAINT passengers_ibfk_2 
FOREIGN KEY (seat_id) REFERENCES seats(id);
```

### 6. Initialize Seat Availability
```sql
-- Buat semua kursi tersedia untuk semua jadwal
INSERT INTO seat_bookings (seat_id, schedule_id, is_available)
SELECT s.id, sch.id, 1
FROM seats s
CROSS JOIN schedules sch
WHERE NOT EXISTS (
    SELECT 1 FROM seat_bookings sb 
    WHERE sb.seat_id = s.id AND sb.schedule_id = sch.id
);

-- Cek hasilnya
SELECT COUNT(*) as total_seat_bookings FROM seat_bookings;
-- Harusnya: (jumlah seats) x (jumlah schedules)
```

### 7. Test Query untuk Ambil Seats
```sql
-- Test query yang dipakai oleh get_seats.php
SELECT 
    s.id,
    s.seat_number,
    s.wagon_number,
    COALESCE(sb.is_available, 1) as is_available
FROM seats s
LEFT JOIN seat_bookings sb ON s.id = sb.seat_id AND sb.schedule_id = 1
WHERE s.wagon_number = '01'
ORDER BY s.seat_number;

-- Harusnya muncul 15 rows dengan ID asli dari database (1-15)
```

## Status Checklist

- [ ] Tabel `seats` sudah ada dan berisi 230 rows
- [ ] Tabel `seat_bookings` sudah ada
- [ ] Tabel `passengers` punya kolom `seat_id` (INT) dengan foreign key
- [ ] Seat availability sudah di-initialize untuk semua schedules
- [ ] Test query berhasil menampilkan seats dengan ID yang benar

## Jika Sudah Semua Checklist ✅

Lanjut ke langkah berikutnya: Update API dan Aplikasi Android
