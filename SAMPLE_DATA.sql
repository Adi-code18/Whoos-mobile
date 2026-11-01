-- ============================================
-- SAMPLE DATA FOR WHOOSH APPLICATION
-- ============================================
-- Pastikan database sudah dibuat terlebih dahulu
-- Database name: whoosh_db (atau sesuai konfigurasi Anda)
-- ============================================

-- 1. CREATE TABLES (Jika belum ada)
-- ============================================

CREATE TABLE IF NOT EXISTS `stations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `station_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `schedules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `train_code` varchar(20) NOT NULL,
  `from_station` varchar(100) NOT NULL,
  `to_station` varchar(100) NOT NULL,
  `departure_time` time NOT NULL,
  `arrival_time` time NOT NULL,
  `price` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `phone` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `bookings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `reservation_no` varchar(50) NOT NULL UNIQUE,
  `total_price` int(11) NOT NULL,
  `status` enum('pending','paid','completed','cancelled') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`schedule_id`) REFERENCES `schedules`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `passengers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `booking_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `id_number` varchar(50) NOT NULL,
  `seat_number` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. INSERT SAMPLE DATA
-- ============================================

-- Clear existing data (optional - hapus comment jika ingin reset data)
-- TRUNCATE TABLE `passengers`;
-- TRUNCATE TABLE `bookings`;
-- TRUNCATE TABLE `schedules`;
-- TRUNCATE TABLE `users`;
-- TRUNCATE TABLE `stations`;

-- Insert Stations
INSERT INTO `stations` (`station_name`) VALUES 
('Halim'),
('Karawang'),
('Padalarang'),
('Tegalluar');

-- Insert Schedules untuk hari ini dan beberapa hari ke depan
-- Ganti tanggal sesuai kebutuhan testing Anda

-- Schedules untuk tanggal 2026-05-20
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G1001', 'Halim', 'Tegalluar', '06:40:00', '07:26:00', 300000, '2026-05-20'),
('G1002', 'Tegalluar', 'Halim', '06:40:00', '07:26:00', 300000, '2026-05-20'),
('G1003', 'Halim', 'Tegalluar', '07:05:00', '07:51:00', 300000, '2026-05-20'),
('G1004', 'Tegalluar', 'Halim', '07:05:00', '07:51:00', 300000, '2026-05-20'),
('G1005', 'Halim', 'Tegalluar', '08:00:00', '08:46:00', 300000, '2026-05-20'),
('G1006', 'Tegalluar', 'Halim', '08:00:00', '08:46:00', 300000, '2026-05-20'),
('G1007', 'Halim', 'Tegalluar', '09:00:00', '09:46:00', 300000, '2026-05-20'),
('G1008', 'Tegalluar', 'Halim', '09:00:00', '09:46:00', 300000, '2026-05-20'),
('G1009', 'Halim', 'Tegalluar', '10:00:00', '10:46:00', 300000, '2026-05-20'),
('G1010', 'Tegalluar', 'Halim', '10:00:00', '10:46:00', 300000, '2026-05-20');

-- Schedules untuk tanggal 2026-05-21
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G1001', 'Halim', 'Tegalluar', '06:40:00', '07:26:00', 300000, '2026-05-21'),
('G1002', 'Tegalluar', 'Halim', '06:40:00', '07:26:00', 300000, '2026-05-21'),
('G1003', 'Halim', 'Tegalluar', '07:05:00', '07:51:00', 300000, '2026-05-21'),
('G1004', 'Tegalluar', 'Halim', '07:05:00', '07:51:00', 300000, '2026-05-21'),
('G1005', 'Halim', 'Tegalluar', '08:00:00', '08:46:00', 300000, '2026-05-21'),
('G1006', 'Tegalluar', 'Halim', '08:00:00', '08:46:00', 300000, '2026-05-21'),
('G1007', 'Halim', 'Tegalluar', '09:00:00', '09:46:00', 300000, '2026-05-21'),
('G1008', 'Tegalluar', 'Halim', '09:00:00', '09:46:00', 300000, '2026-05-21'),
('G1009', 'Halim', 'Tegalluar', '10:00:00', '10:46:00', 300000, '2026-05-21'),
('G1010', 'Tegalluar', 'Halim', '10:00:00', '10:46:00', 300000, '2026-05-21');

-- Schedules untuk tanggal 2026-05-22
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G1001', 'Halim', 'Tegalluar', '06:40:00', '07:26:00', 300000, '2026-05-22'),
('G1002', 'Tegalluar', 'Halim', '06:40:00', '07:26:00', 300000, '2026-05-22'),
('G1003', 'Halim', 'Tegalluar', '07:05:00', '07:51:00', 300000, '2026-05-22'),
('G1004', 'Tegalluar', 'Halim', '07:05:00', '07:51:00', 300000, '2026-05-22'),
('G1005', 'Halim', 'Tegalluar', '08:00:00', '08:46:00', 300000, '2026-05-22'),
('G1006', 'Tegalluar', 'Halim', '08:00:00', '08:46:00', 300000, '2026-05-22'),
('G1007', 'Halim', 'Tegalluar', '09:00:00', '09:46:00', 300000, '2026-05-22'),
('G1008', 'Tegalluar', 'Halim', '09:00:00', '09:46:00', 300000, '2026-05-22'),
('G1009', 'Halim', 'Tegalluar', '10:00:00', '10:46:00', 300000, '2026-05-22'),
('G1010', 'Tegalluar', 'Halim', '10:00:00', '10:46:00', 300000, '2026-05-22');

-- Tambahkan rute lain (Halim - Karawang)
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G2001', 'Halim', 'Karawang', '06:30:00', '06:41:00', 150000, '2026-05-20'),
('G2002', 'Karawang', 'Halim', '07:00:00', '07:11:00', 150000, '2026-05-20'),
('G2003', 'Halim', 'Karawang', '08:30:00', '08:41:00', 150000, '2026-05-20'),
('G2004', 'Karawang', 'Halim', '09:00:00', '09:11:00', 150000, '2026-05-20');

-- Tambahkan rute lain (Karawang - Padalarang)
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G3001', 'Karawang', 'Padalarang', '07:15:00', '07:34:00', 200000, '2026-05-20'),
('G3002', 'Padalarang', 'Karawang', '08:00:00', '08:19:00', 200000, '2026-05-20'),
('G3003', 'Karawang', 'Padalarang', '09:15:00', '09:34:00', 200000, '2026-05-20'),
('G3004', 'Padalarang', 'Karawang', '10:00:00', '10:19:00', 200000, '2026-05-20');

-- Tambahkan rute lain (Padalarang - Tegalluar)
INSERT INTO `schedules` (`train_code`, `from_station`, `to_station`, `departure_time`, `arrival_time`, `price`, `date`) VALUES
('G4001', 'Padalarang', 'Tegalluar', '07:40:00', '07:57:00', 180000, '2026-05-20'),
('G4002', 'Tegalluar', 'Padalarang', '08:30:00', '08:47:00', 180000, '2026-05-20'),
('G4003', 'Padalarang', 'Tegalluar', '09:40:00', '09:57:00', 180000, '2026-05-20'),
('G4004', 'Tegalluar', 'Padalarang', '10:30:00', '10:47:00', 180000, '2026-05-20');

-- Insert Sample User (password: "password123" - hashed dengan MD5 atau bcrypt di PHP)
-- Untuk testing, gunakan password plain text dulu, nanti di PHP bisa di-hash
INSERT INTO `users` (`full_name`, `email`, `phone`, `password`) VALUES
('John Doe', 'john@example.com', '081234567890', MD5('password123')),
('Jane Smith', 'jane@example.com', '081234567891', MD5('password123'));

-- ============================================
-- 3. VERIFICATION QUERIES
-- ============================================
-- Jalankan query ini untuk memastikan data sudah masuk

-- Cek jumlah stations
SELECT COUNT(*) as total_stations FROM stations;

-- Cek jumlah schedules
SELECT COUNT(*) as total_schedules FROM schedules;

-- Cek schedules untuk hari ini
SELECT * FROM schedules WHERE date = CURDATE() ORDER BY departure_time;

-- Cek schedules Halim - Tegalluar untuk hari ini
SELECT * FROM schedules 
WHERE from_station = 'Halim' 
AND to_station = 'Tegalluar' 
AND date = CURDATE()
ORDER BY departure_time;

-- Cek users
SELECT id, full_name, email, phone FROM users;

-- ============================================
-- 4. NOTES
-- ============================================
-- 1. Ganti tanggal di INSERT statements sesuai tanggal testing Anda
-- 2. Untuk production, gunakan password hashing yang lebih aman (bcrypt)
-- 3. Tambahkan index untuk performa yang lebih baik:
--    CREATE INDEX idx_schedules_date ON schedules(date);
--    CREATE INDEX idx_schedules_route ON schedules(from_station, to_station, date);
-- 4. Pastikan timezone database sesuai dengan timezone aplikasi
