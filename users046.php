<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();
if ($user['role'] !== 'admin') {
    response(false, 'Admin only', null);
}

$result = $conn->query("SELECT id, name, nim, email, role, foto_url, created_at FROM users ORDER BY created_at DESC");
$users = [];
while ($row = $result->fetch_assoc()) {
    $row['id'] = (int)$row['id'];
    $users[] = $row;
}

response(true, 'Users retrieved', ['users' => $users]);
