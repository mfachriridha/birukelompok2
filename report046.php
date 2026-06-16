<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();
$tokenUser = require_auth();
$isAdmin = $tokenUser['role'] === 'admin';

if ($isAdmin) {
    $stmt = $conn->prepare('SELECT * FROM bookings ORDER BY date DESC, start_time DESC, id DESC');
} else {
    $userId = (int)($tokenUser['user_id'] ?? 0);
    $stmt = $conn->prepare('SELECT * FROM bookings WHERE user_id = ? ORDER BY date DESC, start_time DESC, id DESC');
    $stmt->bind_param('i', $userId);
}
$stmt->execute();
$result = $stmt->get_result();
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = booking_row($row);
}
response(true, 'Data laporan berhasil diambil', $data);
?>
