<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

requireAuth();

$result = $conn->query("SELECT * FROM rooms ORDER BY created_at DESC");
$rooms = [];
while ($row = $result->fetch_assoc()) {
    $row['kapasitas'] = (int)$row['kapasitas'];
    $rooms[] = $row;
}

response(true, 'Rooms retrieved', ['rooms' => $rooms]);
