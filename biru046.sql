-- Active: 1778210369420@@127.0.0.1@3306@biru046
-- Biru (Booking Ruangan) - Database Schema
-- Jalankan di phpMyAdmin atau MySQL CLI

CREATE DATABASE IF NOT EXISTS biru046 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biru046;

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    nim VARCHAR(32) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'pengguna') NOT NULL DEFAULT 'pengguna',
    photo VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(160) NOT NULL,
    capacity INT NOT NULL DEFAULT 0,
    facilities TEXT NULL,
    description TEXT NULL,
    photo VARCHAR(255) NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'tersedia',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    room_id INT NULL,
    user_name VARCHAR(120) NOT NULL,
    user_nim VARCHAR(32) NOT NULL,
    room_name VARCHAR(120) NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    purpose TEXT NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'cancelled') NOT NULL DEFAULT 'pending',
    admin_note TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_booking_room_date (room_id, date, start_time, end_time),
    INDEX idx_booking_user (user_id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Admin default: Muhammad Fachri Ridha
-- Password: admin123
INSERT INTO users (name, nim, email, password, role) VALUES
('Muhammad Fachri Ridha', '1201226046', 'admin@biru.local', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin');

-- Contoh data ruangan
INSERT INTO rooms (name, location, capacity, facilities, description, status) VALUES
('Ruang 101', 'Gedung A Lantai 1', 35, 'Proyektor, AC, Papan tulis', 'Ruang kelas standar untuk perkuliahan.', 'tersedia'),
('Lab Komputer', 'Gedung B Lantai 2', 30, 'PC, Proyektor, AC, Internet', 'Laboratorium komputer untuk praktikum.', 'tersedia');
