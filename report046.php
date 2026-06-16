<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();

// Status distribution
$statusResult = $conn->query("
    SELECT status, COUNT(*) as count
    FROM bookings
    GROUP BY status
");
$statusData = [];
$totalBookings = 0;
while ($row = $statusResult->fetch_assoc()) {
    $row['count'] = (int)$row['count'];
    $statusData[] = $row;
    $totalBookings += $row['count'];
}

// Per-room counts
$roomResult = $conn->query("
    SELECT r.id, r.nama, COUNT(b.id) as count
    FROM rooms r
    LEFT JOIN bookings b ON r.id = b.room_id
    GROUP BY r.id, r.nama
    ORDER BY count DESC
");
$roomData = [];
while ($row = $roomResult->fetch_assoc()) {
    $row['count'] = (int)$row['count'];
    $roomData[] = $row;
}

// All bookings for detail
$bookingsResult = $conn->query("
    SELECT b.*, r.nama AS room_name, u.name AS user_name
    FROM bookings b
    JOIN rooms r ON b.room_id = r.id
    JOIN users u ON b.user_id = u.id
    ORDER BY b.created_at DESC
");
$bookings = [];
while ($row = $bookingsResult->fetch_assoc()) {
    $row['room_id'] = (int)$row['room_id'];
    $row['user_id'] = (int)$row['user_id'];
    $bookings[] = $row;
}

$data = [
    'total_bookings' => $totalBookings,
    'status_distribution' => $statusData,
    'per_room' => $roomData,
    'bookings' => $bookings
];

response(true, 'Report data retrieved', $data);
