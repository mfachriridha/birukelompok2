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
if ($id <= 0) {
    response(false, 'Invalid room ID', null);
}

// Delete related bookings first
$conn->query("DELETE FROM bookings WHERE room_id = $id");

$stmt = $conn->prepare("DELETE FROM rooms WHERE id = ?");
$stmt->bind_param('i', $id);
$stmt->execute();
$stmt->close();

response(true, 'Room deleted', null);
