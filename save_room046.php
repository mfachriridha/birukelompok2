<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();
if ($user['role'] !== 'admin') {
    response(false, 'Admin only', null);
}

$id = $_POST['id'] ?? null;
$nama = trim($_POST['nama'] ?? '');
$lokasi = trim($_POST['lokasi'] ?? '');
$kapasitas = (int)($_POST['kapasitas'] ?? 0);
$fasilitas = trim($_POST['fasilitas'] ?? '');
$deskripsi = trim($_POST['deskripsi'] ?? '');
$fotoBase64 = $_POST['foto_base64'] ?? '';

if (empty($nama) || empty($lokasi) || $kapasitas <= 0) {
    response(false, 'Nama, lokasi, and kapasitas are required', null);
}

$fotoUrl = null;
if (!empty($fotoBase64)) {
    $fotoDir = 'uploads/rooms/';
    if (!is_dir($fotoDir)) mkdir($fotoDir, 0777, true);
    $fotoName = uniqid('room_') . '.jpg';
    $fotoPath = $fotoDir . $fotoName;
    $imageData = base64_decode($fotoBase64);
    if ($imageData !== false && file_put_contents($fotoPath, $imageData)) {
        $fotoUrl = $fotoPath;
    }
}

if ($id) {
    if ($fotoUrl) {
        $stmt = $conn->prepare("UPDATE rooms SET nama=?, lokasi=?, kapasitas=?, fasilitas=?, deskripsi=?, foto_url=? WHERE id=?");
        $stmt->bind_param('ssisssi', $nama, $lokasi, $kapasitas, $fasilitas, $deskripsi, $fotoUrl, $id);
    } else {
        $stmt = $conn->prepare("UPDATE rooms SET nama=?, lokasi=?, kapasitas=?, fasilitas=?, deskripsi=? WHERE id=?");
        $stmt->bind_param('ssissi', $nama, $lokasi, $kapasitas, $fasilitas, $deskripsi, $id);
    }
} else {
    $stmt = $conn->prepare("INSERT INTO rooms (nama, lokasi, kapasitas, fasilitas, deskripsi, foto_url) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param('ssisss', $nama, $lokasi, $kapasitas, $fasilitas, $deskripsi, $fotoUrl);
}

$stmt->execute();
$roomId = $id ? (int)$id : $stmt->insert_id;
$stmt->close();

$result = $conn->query("SELECT * FROM rooms WHERE id = $roomId");
$room = $result->fetch_assoc();
$room['kapasitas'] = (int)$room['kapasitas'];

response(true, 'Room saved', $room);
