<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();

function room_row(array $row): array
{
    return [
        'id' => (int)$row['id'],
        'name' => $row['name'] ?? '',
        'location' => $row['location'] ?? '',
        'capacity' => (int)($row['capacity'] ?? 0),
        'facilities' => $row['facilities'] ?? '',
        'description' => $row['description'] ?? '',
        'status' => $row['status'] ?? 'tersedia',
        'photo' => photo_url($row['photo'] ?? ''),
    ];
}

function upload_room_photo(): ?string
{
    if (!isset($_FILES['photo']) || $_FILES['photo']['error'] !== UPLOAD_ERR_OK) {
        return null;
    }

    $tmp = $_FILES['photo']['tmp_name'];
    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime = $finfo->file($tmp);
    $allowed = [
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'image/webp' => 'webp',
    ];

    if (!isset($allowed[$mime])) {
        response(false, 'Foto ruangan harus JPG, PNG, atau WEBP', null, 422);
    }

    $dir = __DIR__ . '/uploads/rooms';
    if (!is_dir($dir)) {
        mkdir($dir, 0777, true);
    }

    $fileName = 'room_' . time() . '_' . random_int(1000, 9999) . '.' . $allowed[$mime];
    $relative = 'uploads/rooms/' . $fileName;
    if (!move_uploaded_file($tmp, __DIR__ . '/' . $relative)) {
        response(false, 'Upload foto ruangan gagal', null, 500);
    }
    return $relative;
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    require_auth();
    $result = $conn->query('SELECT * FROM rooms ORDER BY name ASC');
    $data = [];
    while ($row = $result->fetch_assoc()) {
        $data[] = room_row($row);
    }
    response(true, 'Data ruangan berhasil diambil', $data);
}

$tokenUser = require_auth();
$isAdmin = $tokenUser['role'] === 'admin';

$isMultipart = str_contains($_SERVER['CONTENT_TYPE'] ?? '', 'multipart/form-data');
$input = $isMultipart ? $_POST : input();
$action = $input['action'] ?? '';

if ($action === 'create' || $action === 'update') {
    if (!$isAdmin) response(false, 'Hanya admin yang bisa mengelola ruangan', null, 403);

    $id = (int)($input['id'] ?? 0);
    $name = trim($input['name'] ?? '');
    $location = trim($input['location'] ?? '');
    $capacity = (int)($input['capacity'] ?? 0);
    $facilities = trim($input['facilities'] ?? '');
    $description = trim($input['description'] ?? '');
    $status = trim($input['status'] ?? 'tersedia');
    $photoPath = upload_room_photo();

    if ($name === '' || $location === '' || $capacity <= 0) {
        response(false, 'Nama, lokasi, dan kapasitas ruangan wajib valid', null, 422);
    }

    if ($action === 'create') {
        $stmt = $conn->prepare('INSERT INTO rooms (name, location, capacity, facilities, description, photo, status) VALUES (?, ?, ?, ?, ?, ?, ?)');
        $stmt->bind_param('ssissss', $name, $location, $capacity, $facilities, $description, $photoPath, $status);
    } elseif ($photoPath !== null) {
        $stmt = $conn->prepare('UPDATE rooms SET name=?, location=?, capacity=?, facilities=?, description=?, photo=?, status=? WHERE id=?');
        $stmt->bind_param('ssissssi', $name, $location, $capacity, $facilities, $description, $photoPath, $status, $id);
    } else {
        $stmt = $conn->prepare('UPDATE rooms SET name=?, location=?, capacity=?, facilities=?, description=?, status=? WHERE id=?');
        $stmt->bind_param('ssisssi', $name, $location, $capacity, $facilities, $description, $status, $id);
    }

    if (!$stmt->execute()) {
        response(false, 'Simpan ruangan gagal: ' . $stmt->error, null, 500);
    }
    response(true, 'Ruangan berhasil disimpan');
}

if ($action === 'delete') {
    if (!$isAdmin) response(false, 'Hanya admin yang bisa menghapus ruangan', null, 403);

    $id = (int)($input['id'] ?? 0);
    $stmt = $conn->prepare('DELETE FROM rooms WHERE id = ?');
    $stmt->bind_param('i', $id);
    if (!$stmt->execute()) {
        response(false, 'Hapus ruangan gagal: ' . $stmt->error, null, 500);
    }
    response(true, 'Ruangan berhasil dihapus');
}

response(false, 'Action ruangan tidak dikenal', null, 400);
?>
