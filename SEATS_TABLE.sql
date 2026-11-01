-- ============================================
-- SEATS TABLE FOR WHOOSH APPLICATION
-- ============================================

-- 1. CREATE SEATS TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS `seats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seat_number` varchar(10) NOT NULL COMMENT 'Nomor kursi: 1A, 1B, 1C, dll',
  `wagon_number` varchar(5) NOT NULL COMMENT 'Nomor gerbong: 01, 02, 03, dll',
  `wagon_class` enum('First Class','Kelas Bisnis','Kelas Ekonomi Premium') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_seat` (`seat_number`, `wagon_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. CREATE SEAT BOOKINGS TABLE (untuk track kursi yang sudah dipesan)
-- ============================================

CREATE TABLE IF NOT EXISTS `seat_bookings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seat_id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL COMMENT 'NULL jika belum ada booking',
  `is_available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1=tersedia, 0=sudah dipesan',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_seat_schedule` (`seat_id`, `schedule_id`),
  FOREIGN KEY (`seat_id`) REFERENCES `seats`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`schedule_id`) REFERENCES `schedules`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. INSERT SAMPLE SEATS DATA
-- ============================================

-- First Class Seats (Gerbong 01 & 02)
-- Layout: 2-1 (A,C - F) x 5 rows = 15 seats per wagon

-- Gerbong 01 - First Class
INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) VALUES
('1A', '01', 'First Class'),
('1C', '01', 'First Class'),
('1F', '01', 'First Class'),
('2A', '01', 'First Class'),
('2C', '01', 'First Class'),
('2F', '01', 'First Class'),
('3A', '01', 'First Class'),
('3C', '01', 'First Class'),
('3F', '01', 'First Class'),
('4A', '01', 'First Class'),
('4C', '01', 'First Class'),
('4F', '01', 'First Class'),
('5A', '01', 'First Class'),
('5C', '01', 'First Class'),
('5F', '01', 'First Class');

-- Gerbong 02 - First Class
INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) VALUES
('1A', '02', 'First Class'),
('1C', '02', 'First Class'),
('1F', '02', 'First Class'),
('2A', '02', 'First Class'),
('2C', '02', 'First Class'),
('2F', '02', 'First Class'),
('3A', '02', 'First Class'),
('3C', '02', 'First Class'),
('3F', '02', 'First Class'),
('4A', '02', 'First Class'),
('4C', '02', 'First Class'),
('4F', '02', 'First Class'),
('5A', '02', 'First Class'),
('5C', '02', 'First Class'),
('5F', '02', 'First Class');

-- Economy Seats (Gerbong 03, 04, 05, 06)
-- Layout: 3-2 (A,B,C - D,F) x 10 rows = 50 seats per wagon

-- Gerbong 03 - Economy
INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) VALUES
('1A', '03', 'Kelas Ekonomi Premium'),
('1B', '03', 'Kelas Ekonomi Premium'),
('1C', '03', 'Kelas Ekonomi Premium'),
('1D', '03', 'Kelas Ekonomi Premium'),
('1F', '03', 'Kelas Ekonomi Premium'),
('2A', '03', 'Kelas Ekonomi Premium'),
('2B', '03', 'Kelas Ekonomi Premium'),
('2C', '03', 'Kelas Ekonomi Premium'),
('2D', '03', 'Kelas Ekonomi Premium'),
('2F', '03', 'Kelas Ekonomi Premium'),
('3A', '03', 'Kelas Ekonomi Premium'),
('3B', '03', 'Kelas Ekonomi Premium'),
('3C', '03', 'Kelas Ekonomi Premium'),
('3D', '03', 'Kelas Ekonomi Premium'),
('3F', '03', 'Kelas Ekonomi Premium'),
('4A', '03', 'Kelas Ekonomi Premium'),
('4B', '03', 'Kelas Ekonomi Premium'),
('4C', '03', 'Kelas Ekonomi Premium'),
('4D', '03', 'Kelas Ekonomi Premium'),
('4F', '03', 'Kelas Ekonomi Premium'),
('5A', '03', 'Kelas Ekonomi Premium'),
('5B', '03', 'Kelas Ekonomi Premium'),
('5C', '03', 'Kelas Ekonomi Premium'),
('5D', '03', 'Kelas Ekonomi Premium'),
('5F', '03', 'Kelas Ekonomi Premium'),
('6A', '03', 'Kelas Ekonomi Premium'),
('6B', '03', 'Kelas Ekonomi Premium'),
('6C', '03', 'Kelas Ekonomi Premium'),
('6D', '03', 'Kelas Ekonomi Premium'),
('6F', '03', 'Kelas Ekonomi Premium'),
('7A', '03', 'Kelas Ekonomi Premium'),
('7B', '03', 'Kelas Ekonomi Premium'),
('7C', '03', 'Kelas Ekonomi Premium'),
('7D', '03', 'Kelas Ekonomi Premium'),
('7F', '03', 'Kelas Ekonomi Premium'),
('8A', '03', 'Kelas Ekonomi Premium'),
('8B', '03', 'Kelas Ekonomi Premium'),
('8C', '03', 'Kelas Ekonomi Premium'),
('8D', '03', 'Kelas Ekonomi Premium'),
('8F', '03', 'Kelas Ekonomi Premium'),
('9A', '03', 'Kelas Ekonomi Premium'),
('9B', '03', 'Kelas Ekonomi Premium'),
('9C', '03', 'Kelas Ekonomi Premium'),
('9D', '03', 'Kelas Ekonomi Premium'),
('9F', '03', 'Kelas Ekonomi Premium'),
('10A', '03', 'Kelas Ekonomi Premium'),
('10B', '03', 'Kelas Ekonomi Premium'),
('10C', '03', 'Kelas Ekonomi Premium'),
('10D', '03', 'Kelas Ekonomi Premium'),
('10F', '03', 'Kelas Ekonomi Premium');

-- Gerbong 04, 05, 06 - Copy dari gerbong 03, ganti wagon_number saja
-- (Untuk menghemat space, hanya contoh untuk gerbong 04)

INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) 
SELECT `seat_number`, '04', `wagon_class` FROM `seats` WHERE `wagon_number` = '03';

INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) 
SELECT `seat_number`, '05', `wagon_class` FROM `seats` WHERE `wagon_number` = '03';

INSERT INTO `seats` (`seat_number`, `wagon_number`, `wagon_class`) 
SELECT `seat_number`, '06', `wagon_class` FROM `seats` WHERE `wagon_number` = '03';

-- 4. INITIALIZE SEAT AVAILABILITY FOR ALL SCHEDULES
-- ============================================
-- Jalankan query ini untuk membuat semua kursi tersedia untuk semua jadwal

INSERT INTO `seat_bookings` (`seat_id`, `schedule_id`, `is_available`)
SELECT s.id, sch.id, 1
FROM `seats` s
CROSS JOIN `schedules` sch
WHERE NOT EXISTS (
    SELECT 1 FROM `seat_bookings` sb 
    WHERE sb.seat_id = s.id AND sb.schedule_id = sch.id
);

-- 5. VERIFICATION QUERIES
-- ============================================

-- Cek total seats
SELECT wagon_number, wagon_class, COUNT(*) as total_seats 
FROM seats 
GROUP BY wagon_number, wagon_class;

-- Cek seats untuk gerbong tertentu
SELECT * FROM seats WHERE wagon_number = '01' ORDER BY seat_number;

-- Cek seat availability untuk schedule tertentu
SELECT 
    s.seat_number,
    s.wagon_number,
    sb.is_available,
    sch.train_code,
    sch.date
FROM seat_bookings sb
JOIN seats s ON sb.seat_id = s.id
JOIN schedules sch ON sb.schedule_id = sch.id
WHERE sch.id = 1 AND s.wagon_number = '01'
ORDER BY s.seat_number;

-- 6. UPDATE passengers TABLE
-- ============================================
-- Ubah kolom seat_number menjadi seat_id

ALTER TABLE `passengers` 
CHANGE COLUMN `seat_number` `seat_id` INT(11) NOT NULL,
ADD FOREIGN KEY (`seat_id`) REFERENCES `seats`(`id`);

-- 7. STORED PROCEDURE untuk Book Seat
-- ============================================

DELIMITER $$

CREATE PROCEDURE `book_seat`(
    IN p_seat_id INT,
    IN p_schedule_id INT,
    IN p_booking_id INT
)
BEGIN
    -- Update seat_bookings
    UPDATE seat_bookings 
    SET is_available = 0, booking_id = p_booking_id
    WHERE seat_id = p_seat_id AND schedule_id = p_schedule_id;
    
    -- Return updated status
    SELECT 
        s.id as seat_id,
        s.seat_number,
        s.wagon_number,
        sb.is_available
    FROM seat_bookings sb
    JOIN seats s ON sb.seat_id = s.id
    WHERE sb.seat_id = p_seat_id AND sb.schedule_id = p_schedule_id;
END$$

DELIMITER ;

-- 8. STORED PROCEDURE untuk Release Seat (Cancel Booking)
-- ============================================

DELIMITER $$

CREATE PROCEDURE `release_seat`(
    IN p_booking_id INT
)
BEGIN
    -- Update seat_bookings
    UPDATE seat_bookings 
    SET is_available = 1, booking_id = NULL
    WHERE booking_id = p_booking_id;
    
    SELECT ROW_COUNT() as affected_rows;
END$$

DELIMITER ;

-- ============================================
-- NOTES
-- ============================================
-- 1. Setiap schedule memiliki seat availability sendiri
-- 2. Seat bisa dipesan untuk schedule yang berbeda
-- 3. Gunakan stored procedure untuk book/release seat
-- 4. seat_id adalah INT yang dikirim dari aplikasi Android
-- 5. seat_number (1A, 1B, dll) hanya untuk display
