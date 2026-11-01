-- ============================================
-- WAGON CLASSES TABLE FOR WHOOSH APPLICATION
-- ============================================

-- 1. CREATE WAGON_CLASSES TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS `wagon_classes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(50) NOT NULL COMMENT 'Nama kelas: Ekonomi, Eksekutif, Busines',
  `price_multiplier` decimal(3,2) NOT NULL COMMENT 'Multiplier harga: 1.00, 1.50, 1.70',
  `description` text DEFAULT NULL COMMENT 'Deskripsi kelas',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_class_name` (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. INSERT DATA KELAS
-- ============================================

INSERT INTO `wagon_classes` (`class_name`, `price_multiplier`, `description`) VALUES
('Ekonomi', 1.00, 'Kelas ekonomi dengan fasilitas standar'),
('Eksekutif', 1.50, 'Kelas eksekutif dengan fasilitas lebih baik (50% lebih mahal)'),
('Busines', 1.70, 'Kelas bisnis dengan fasilitas premium (70% lebih mahal)');

-- 3. VERIFICATION QUERY
-- ============================================

SELECT * FROM wagon_classes ORDER BY price_multiplier ASC;

-- Expected result:
-- id | class_name | price_multiplier | description
-- 1  | Ekonomi    | 1.00            | Kelas ekonomi dengan fasilitas standar
-- 2  | Eksekutif  | 1.50            | Kelas eksekutif dengan fasilitas lebih baik (50% lebih mahal)
-- 3  | Busines    | 1.70            | Kelas bisnis dengan fasilitas premium (70% lebih mahal)

-- 4. UPDATE SEATS TABLE (Optional - jika ingin link seats ke wagon_classes)
-- ============================================

-- Ubah enum wagon_class menjadi varchar dan tambah foreign key
ALTER TABLE `seats` 
MODIFY COLUMN `wagon_class` VARCHAR(50) NOT NULL;

-- Update data yang sudah ada
UPDATE `seats` SET `wagon_class` = 'Ekonomi' WHERE `wagon_class` = 'Kelas Ekonomi Premium';
UPDATE `seats` SET `wagon_class` = 'Eksekutif' WHERE `wagon_class` = 'First Class';
UPDATE `seats` SET `wagon_class` = 'Busines' WHERE `wagon_class` = 'Kelas Bisnis';

-- Tambah foreign key (optional)
-- ALTER TABLE `seats` 
-- ADD CONSTRAINT `fk_seats_wagon_class` 
-- FOREIGN KEY (`wagon_class`) REFERENCES `wagon_classes`(`class_name`);

-- 5. NOTES
-- ============================================
-- 1. Multiplier 1.00 = harga dasar (Ekonomi)
-- 2. Multiplier 1.50 = 50% lebih mahal dari harga dasar (Eksekutif)
-- 3. Multiplier 1.70 = 70% lebih mahal dari harga dasar (Busines)
-- 4. Harga dasar diambil dari schedules.price (harga ekonomi)
-- 5. Aplikasi Android akan mengalikan harga dasar dengan multiplier
