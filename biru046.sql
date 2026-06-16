-- ============================================
-- Biru (Booking Ruangan) - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS biru046
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE biru046;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    nim VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    foto_url VARCHAR(255) DEFAULT NULL,
    api_token VARCHAR(64) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    lokasi VARCHAR(100) NOT NULL,
    kapasitas INT NOT NULL,
    fasilitas TEXT,
    deskripsi TEXT,
    foto_url VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    user_id INT NOT NULL,
    tanggal DATE NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    keperluan TEXT NOT NULL,
    status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
    admin_note TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed admin user (password: password)
INSERT INTO users (name, nim, email, password, role) VALUES
('Admin', '0000000000', 'admin@biru.local', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin');

-- Seed some rooms
INSERT INTO rooms (nama, lokasi, kapasitas, fasilitas, deskripsi) VALUES
('Ruang A101', 'Gedung A Lantai 1', 30, 'AC, Proyektor, Whiteboard, Speaker', 'Ruangan kelas standar dengan kapasitas 30 orang, cocok untuk kuliah dan seminar kecil'),
('Ruang A102', 'Gedung A Lantai 1', 40, 'AC, Proyektor, Whiteboard, Sound System', 'Ruangan kelas besar dengan kapasitas 40 orang'),
('Ruang B201', 'Gedung B Lantai 2', 20, 'AC, TV 65 inch, Sofa, Karpet', 'Ruang meeting eksekutif dengan interior premium'),
('Laboratorium Komputer', 'Gedung C Lantai 1', 25, 'AC, 25 PC, Proyektor, Whiteboard', 'Laboratorium komputer dengan spesifikasi tinggi'),
('Aula Serbaguna', 'Gedung A Lantai 3', 100, 'AC, Proyektor, Sound System, Panggung, Lampu', 'Aula besar untuk acara besar seperti seminar, workshop, dan pentas seni');
