<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();

$roomId = (int)($_POST['room_id'] ?? 0);
$tanggal = trim($_POST['tanggal'] ?? '');
$jamMulai = trim($_POST['jam_mulai'] ?? '');
$jamSelesai = trim($_POST['jam_selesai'] ?? '');
$keperluan = trim($_POST['keperluan'] ?? '');

if ($roomId <= 0 || empty($tanggal) || empty($jamMulai) || empty($jamSelesai) || empty($keperluan)) {
    response(false, 'All fields are required', null);
}

// Check room exists
$stmt = $conn->prepare("SELECT id FROM rooms WHERE id = ?");
$stmt->bind_param('i', $roomId);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows === 0) {
    response(false, 'Room not found', null);
}
$stmt->close();

// Check slot conflict
$stmt = $conn->prepare("SELECT id FROM bookings
    WHERE room_id = ? AND tanggal = ?
    AND status != 'rejected'
    AND (
        (jam_mulai < ? AND jam_selesai > ?)
        OR (jam_mulai < ? AND jam_selesai > ?)
        OR (jam_mulai >= ? AND jam_selesai <= ?)
    )
    LIMIT 1");
$stmt->bind_param('isssssss', $roomId, $tanggal, $jamSelesai, $jamMulai, $jamMulai, $jamSelesai, $jamMulai, $jamSelesai);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    response(false, 'Room is already booked at that time slot', null);
}
$stmt->close();

$stmt = $conn->prepare("INSERT INTO bookings (room_id, user_id, tanggal, jam_mulai, jam_selesai, keperluan) VALUES (?, ?, ?, ?, ?, ?)");
$stmt->bind_param('iissss', $roomId, $user['id'], $tanggal, $jamMulai, $jamSelesai, $keperluan);
$stmt->execute();
$bookingId = $stmt->insert_id;
$stmt->close();

$result = $conn->query("SELECT b.*, r.nama AS room_name, u.name AS user_name
    FROM bookings b
    JOIN rooms r ON b.room_id = r.id
    JOIN users u ON b.user_id = u.id
    WHERE b.id = $bookingId");
$booking = $result->fetch_assoc();
$booking['room_id'] = (int)$booking['room_id'];
$booking['user_id'] = (int)$booking['user_id'];

response(true, 'Booking created', $booking);
