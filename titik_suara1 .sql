-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 20, 2024 at 06:59 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `titik_suara1`
--

-- --------------------------------------------------------

--
-- Table structure for table `komentar_pengaduan`
--

CREATE TABLE `komentar_pengaduan` (
  `id_komentar` int(11) NOT NULL,
  `id_pengaduan` varchar(20) DEFAULT NULL,
  `id_pengguna` int(11) DEFAULT NULL,
  `komentar` text NOT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lampiran_pengaduan`
--

CREATE TABLE `lampiran_pengaduan` (
  `id_lampiran` int(11) NOT NULL,
  `id_pengaduan` varchar(20) DEFAULT NULL,
  `nama_file` varchar(255) NOT NULL,
  `file_data` mediumblob NOT NULL,
  `tipe_file` varchar(50) NOT NULL,
  `diunggah_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifikasi`
--

CREATE TABLE `notifikasi` (
  `id_notifikasi` int(11) NOT NULL,
  `id_pengguna` int(11) DEFAULT NULL,
  `id_pengaduan` varchar(20) DEFAULT NULL,
  `pesan` text NOT NULL,
  `dibaca` tinyint(1) DEFAULT 0,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pengaduan`
--

CREATE TABLE `pengaduan` (
  `id_pengaduan` varchar(20) NOT NULL,
  `deskripsi` text NOT NULL,
  `kategori` enum('Fasilitas','Peralatan','Kebersihan','Keamanan','Kenakalan Siswa','Lainnya') NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `id_status` int(11) DEFAULT NULL,
  `id_karyawan` int(11) DEFAULT NULL,
  `id_admin` int(11) DEFAULT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp(),
  `diperbarui_pada` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pengaduan`
--

INSERT INTO `pengaduan` (`id_pengaduan`, `deskripsi`, `kategori`, `image_path`, `id_status`, `id_karyawan`, `id_admin`, `dibuat_pada`, `diperbarui_pada`) VALUES
('PGD2024120001', 'terbobol', 'Keamanan', 'uploads/img_6763225b16acd1.56261592.jpg', 1, NULL, NULL, '2024-12-18 19:28:27', '2024-12-18 19:28:27'),
('PGD2024122000', 'kotor', 'Kebersihan', 'uploads/img_6763259e25be06.22657035.jpg', 1, NULL, NULL, '2024-12-18 19:42:22', '2024-12-18 19:42:22'),
('PGD2024122200', 'kotoryy4eeey65', 'Peralatan', 'uploads/img_67632637538676.90022953.jpg', 1, NULL, NULL, '2024-12-18 19:44:55', '2024-12-18 19:44:55');

--
-- Triggers `pengaduan`
--
DELIMITER $$
CREATE TRIGGER `after_pengaduan_status_update` AFTER UPDATE ON `pengaduan` FOR EACH ROW BEGIN
    IF OLD.id_status != NEW.id_status THEN
        INSERT INTO riwayat_pengaduan (
            id_pengaduan,
            status_lama,
            status_baru,
            diubah_oleh,
            catatan
        ) VALUES (
            NEW.id_pengaduan,
            OLD.id_status,
            NEW.id_status,
            NEW.id_admin,
            CONCAT('Status diubah dari ', 
                (SELECT nama_status FROM status_pengaduan WHERE id_status = OLD.id_status),
                ' ke ',
                (SELECT nama_status FROM status_pengaduan WHERE id_status = NEW.id_status)
            )
        );
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `before_pengaduan_insert` BEFORE INSERT ON `pengaduan` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(id_pengaduan, 9)), 0) + 1 
                   FROM pengaduan);
    SET NEW.id_pengaduan = CONCAT('PGD', DATE_FORMAT(NOW(), '%Y%m'), 
                                 LPAD(next_id, 4, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` int(11) NOT NULL,
  `nama_lengkap` varchar(50) NOT NULL,
  `kata_sandi` varchar(8) NOT NULL,
  `jabatan` varchar(50) NOT NULL,
  `alamat` varchar(225) NOT NULL,
  `no_telp` varchar(15) NOT NULL,
  `peran` enum('admin','karyawan') NOT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp(),
  `diperbarui_pada` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `riwayat_pengaduan`
--

CREATE TABLE `riwayat_pengaduan` (
  `id_riwayat` int(11) NOT NULL,
  `id_pengaduan` varchar(20) DEFAULT NULL,
  `status_lama` int(11) DEFAULT NULL,
  `status_baru` int(11) DEFAULT NULL,
  `diubah_oleh` int(11) DEFAULT NULL,
  `catatan` text DEFAULT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `status_pengaduan`
--

CREATE TABLE `status_pengaduan` (
  `id_status` int(11) NOT NULL,
  `nama_status` varchar(50) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `warna` varchar(7) DEFAULT '#000000',
  `urutan` int(11) NOT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `status_pengaduan`
--

INSERT INTO `status_pengaduan` (`id_status`, `nama_status`, `keterangan`, `warna`, `urutan`, `dibuat_pada`) VALUES
(1, 'Diajukan', 'Pengaduan baru yang telah dikirimkan', '#3498DB', 1, '2024-12-03 04:06:14'),
(2, 'Diproses', 'Pengaduan sedang dalam penanganan', '#F1C40F', 2, '2024-12-03 04:06:14'),
(3, 'Ditolak', 'Pengaduan ditolak karena tidak memenuhi kriteria', '#E74C3C', 3, '2024-12-03 04:06:14'),
(4, 'Ditinjau', 'Pengaduan sedang dalam peninjauan ulang', '#9B59B6', 4, '2024-12-03 04:06:14'),
(5, 'Selesai', 'Pengaduan telah selesai ditangani', '#2ECC71', 5, '2024-12-03 04:06:14');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `komentar_pengaduan`
--
ALTER TABLE `komentar_pengaduan`
  ADD PRIMARY KEY (`id_komentar`),
  ADD KEY `id_pengaduan` (`id_pengaduan`),
  ADD KEY `id_pengguna` (`id_pengguna`);

--
-- Indexes for table `lampiran_pengaduan`
--
ALTER TABLE `lampiran_pengaduan`
  ADD PRIMARY KEY (`id_lampiran`),
  ADD KEY `id_pengaduan` (`id_pengaduan`);

--
-- Indexes for table `notifikasi`
--
ALTER TABLE `notifikasi`
  ADD PRIMARY KEY (`id_notifikasi`),
  ADD KEY `id_pengguna` (`id_pengguna`),
  ADD KEY `id_pengaduan` (`id_pengaduan`);

--
-- Indexes for table `pengaduan`
--
ALTER TABLE `pengaduan`
  ADD PRIMARY KEY (`id_pengaduan`),
  ADD KEY `id_status` (`id_status`),
  ADD KEY `id_karyawan` (`id_karyawan`),
  ADD KEY `id_admin` (`id_admin`);

--
-- Indexes for table `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`),
  ADD UNIQUE KEY `nama_lengkap` (`nama_lengkap`),
  ADD UNIQUE KEY `no_telp` (`no_telp`);

--
-- Indexes for table `riwayat_pengaduan`
--
ALTER TABLE `riwayat_pengaduan`
  ADD PRIMARY KEY (`id_riwayat`),
  ADD KEY `id_pengaduan` (`id_pengaduan`),
  ADD KEY `status_lama` (`status_lama`),
  ADD KEY `status_baru` (`status_baru`),
  ADD KEY `diubah_oleh` (`diubah_oleh`);

--
-- Indexes for table `status_pengaduan`
--
ALTER TABLE `status_pengaduan`
  ADD PRIMARY KEY (`id_status`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `komentar_pengaduan`
--
ALTER TABLE `komentar_pengaduan`
  MODIFY `id_komentar` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lampiran_pengaduan`
--
ALTER TABLE `lampiran_pengaduan`
  MODIFY `id_lampiran` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifikasi`
--
ALTER TABLE `notifikasi`
  MODIFY `id_notifikasi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id_pengguna` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `riwayat_pengaduan`
--
ALTER TABLE `riwayat_pengaduan`
  MODIFY `id_riwayat` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `status_pengaduan`
--
ALTER TABLE `status_pengaduan`
  MODIFY `id_status` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `komentar_pengaduan`
--
ALTER TABLE `komentar_pengaduan`
  ADD CONSTRAINT `komentar_pengaduan_ibfk_1` FOREIGN KEY (`id_pengaduan`) REFERENCES `pengaduan` (`id_pengaduan`),
  ADD CONSTRAINT `komentar_pengaduan_ibfk_2` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`);

--
-- Constraints for table `lampiran_pengaduan`
--
ALTER TABLE `lampiran_pengaduan`
  ADD CONSTRAINT `lampiran_pengaduan_ibfk_1` FOREIGN KEY (`id_pengaduan`) REFERENCES `pengaduan` (`id_pengaduan`);

--
-- Constraints for table `notifikasi`
--
ALTER TABLE `notifikasi`
  ADD CONSTRAINT `notifikasi_ibfk_1` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`),
  ADD CONSTRAINT `notifikasi_ibfk_2` FOREIGN KEY (`id_pengaduan`) REFERENCES `pengaduan` (`id_pengaduan`);

--
-- Constraints for table `pengaduan`
--
ALTER TABLE `pengaduan`
  ADD CONSTRAINT `pengaduan_ibfk_1` FOREIGN KEY (`id_status`) REFERENCES `status_pengaduan` (`id_status`),
  ADD CONSTRAINT `pengaduan_ibfk_2` FOREIGN KEY (`id_karyawan`) REFERENCES `pengguna` (`id_pengguna`),
  ADD CONSTRAINT `pengaduan_ibfk_3` FOREIGN KEY (`id_admin`) REFERENCES `pengguna` (`id_pengguna`);

--
-- Constraints for table `riwayat_pengaduan`
--
ALTER TABLE `riwayat_pengaduan`
  ADD CONSTRAINT `riwayat_pengaduan_ibfk_1` FOREIGN KEY (`id_pengaduan`) REFERENCES `pengaduan` (`id_pengaduan`),
  ADD CONSTRAINT `riwayat_pengaduan_ibfk_2` FOREIGN KEY (`status_lama`) REFERENCES `status_pengaduan` (`id_status`),
  ADD CONSTRAINT `riwayat_pengaduan_ibfk_3` FOREIGN KEY (`status_baru`) REFERENCES `status_pengaduan` (`id_status`),
  ADD CONSTRAINT `riwayat_pengaduan_ibfk_4` FOREIGN KEY (`diubah_oleh`) REFERENCES `pengguna` (`id_pengguna`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
