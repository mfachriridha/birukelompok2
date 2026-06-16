<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();
$tokenUser = require_admin();

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $result = $conn->query('SELECT * FROM users ORDER BY role ASC, name ASC');
    $data = [];
    while ($row = $result->fetch_assoc()) {
        $data[] = user_row($row);
    }
    response(true, 'Data pengguna berhasil diambil', $data);
}

$input = input();
if (($input['action'] ?? '') === 'delete') {
    $id = (int)($input['id'] ?? 0);
    $stmt = $conn->prepare("DELETE FROM users WHERE id = ? AND role = 'pengguna'");
    $stmt->bind_param('i', $id);
    if (!$stmt->execute()) {
        response(false, 'Hapus pengguna gagal: ' . $stmt->error, null, 500);
    }
    response(true, $stmt->affected_rows > 0 ? 'Pengguna berhasil dihapus' : 'Admin tidak bisa dihapus dari menu pengguna');
}

response(false, 'Action pengguna tidak dikenal', null, 400);
?>
