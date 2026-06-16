<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();

$role = $user['role'];
$userId = (int)$user['id'];

$where = ($role === 'admin')
    ? "1=1"
    : "b.user_id = $userId";

$sql = "SELECT b.*, r.nama AS room_name, u.name AS user_name
        FROM bookings b
        JOIN rooms r ON b.room_id = r.id
        JOIN users u ON b.user_id = u.id
        WHERE $where
        ORDER BY b.created_at DESC";

$result = $conn->query($sql);
$bookings = [];
while ($row = $result->fetch_assoc()) {
    $row['room_id'] = (int)$row['room_id'];
    $row['user_id'] = (int)$row['user_id'];
    $bookings[] = $row;
}

response(true, 'Bookings retrieved', ['bookings' => $bookings]);
