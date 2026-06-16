<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();
if ($user['role'] !== 'admin') {
    response(false, 'Admin only', null);
}

$id = (int)($_POST['id'] ?? 0);
$status = trim($_POST['status'] ?? '');
$adminNote = trim($_POST['admin_note'] ?? '');

if ($id <= 0 || !in_array($status, ['approved', 'rejected'])) {
    response(false, 'Invalid parameters', null);
}

$stmt = $conn->prepare("UPDATE bookings SET status=?, admin_note=? WHERE id=?");
$stmt->bind_param('ssi', $status, $adminNote, $id);
$stmt->execute();
$stmt->close();

$result = $conn->query("SELECT b.*, r.nama AS room_name, u.name AS user_name
    FROM bookings b
    JOIN rooms r ON b.room_id = r.id
    JOIN users u ON b.user_id = u.id
    WHERE b.id = $id");
$booking = $result->fetch_assoc();
$booking['room_id'] = (int)$booking['room_id'];
$booking['user_id'] = (int)$booking['user_id'];

response(true, 'Booking updated', $booking);
