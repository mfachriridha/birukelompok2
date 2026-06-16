<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();
$tokenUser = require_auth();
$tokenUserId = (int)($tokenUser['user_id'] ?? 0);

$id = (int)($_POST['id'] ?? 0);
if ($id <= 0 || $id !== $tokenUserId) {
    response(false, 'ID user tidak valid', null, 422);
}

$name = trim($_POST['name'] ?? '');
$nim = trim($_POST['nim'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = (string)($_POST['password'] ?? '');

if ($id <= 0 || $name === '' || $email === '') {
    response(false, 'Data profil wajib diisi', null, 422);
}

$check = $conn->prepare('SELECT id FROM users WHERE email = ? AND id <> ? LIMIT 1');
$check->bind_param('si', $email, $id);
$check->execute();
if ($check->get_result()->num_rows > 0) {
    response(false, 'Email sudah dipakai akun lain', null, 409);
}

$photoPath = null;
if (isset($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
    $tmp = $_FILES['photo']['tmp_name'];
    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime = $finfo->file($tmp);
    $allowed = [
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'image/webp' => 'webp',
    ];
    if (!isset($allowed[$mime])) {
        response(false, 'Foto harus JPG, PNG, atau WEBP', null, 422);
    }
    $profileDir = __DIR__ . '/uploads/profiles';
    if (!is_dir($profileDir)) {
        mkdir($profileDir, 0777, true);
    }
    $fileName = 'profile_' . $id . '_' . time() . '.' . $allowed[$mime];
    $relative = 'uploads/profiles/' . $fileName;
    if (!move_uploaded_file($tmp, __DIR__ . '/' . $relative)) {
        response(false, 'Upload foto gagal', null, 500);
    }
    $photoPath = $relative;
}

if ($password !== '' && $photoPath !== null) {
    $hash = password_hash($password, PASSWORD_DEFAULT);
    $stmt = $conn->prepare('UPDATE users SET name=?, nim=?, email=?, password=?, photo=? WHERE id=?');
    $stmt->bind_param('sssssi', $name, $nim, $email, $hash, $photoPath, $id);
} elseif ($password !== '') {
    $hash = password_hash($password, PASSWORD_DEFAULT);
    $stmt = $conn->prepare('UPDATE users SET name=?, nim=?, email=?, password=? WHERE id=?');
    $stmt->bind_param('ssssi', $name, $nim, $email, $hash, $id);
} elseif ($photoPath !== null) {
    $stmt = $conn->prepare('UPDATE users SET name=?, nim=?, email=?, photo=? WHERE id=?');
    $stmt->bind_param('ssssi', $name, $nim, $email, $photoPath, $id);
} else {
    $stmt = $conn->prepare('UPDATE users SET name=?, nim=?, email=? WHERE id=?');
    $stmt->bind_param('sssi', $name, $nim, $email, $id);
}

if (!$stmt->execute()) {
    response(false, 'Update profil gagal: ' . $stmt->error, null, 500);
}

$stmtRead = $conn->prepare('SELECT * FROM users WHERE id = ? LIMIT 1');
$stmtRead->bind_param('i', $id);
$stmtRead->execute();
$row = $stmtRead->get_result()->fetch_assoc();
response(true, 'Profil berhasil diperbarui', user_row($row));
?>
