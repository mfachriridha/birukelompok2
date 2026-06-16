<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$user = requireAuth();
$userId = (int)$user['id'];

$name = trim($_POST['name'] ?? '');
$nim = trim($_POST['nim'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';
$fotoBase64 = $_POST['foto_base64'] ?? '';

if (!empty($email) && $email !== $user['email']) {
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? AND id != ? LIMIT 1");
    $stmt->bind_param('si', $email, $userId);
    $stmt->execute();
    $stmt->store_result();
    if ($stmt->num_rows > 0) {
        response(false, 'Email already used by another account', null);
    }
    $stmt->close();
}

$fotoUrl = $user['foto_url'];
if (!empty($fotoBase64)) {
    $fotoDir = 'uploads/profiles/';
    if (!is_dir($fotoDir)) mkdir($fotoDir, 0777, true);
    $fotoName = uniqid('profile_') . '.jpg';
    $fotoPath = $fotoDir . $fotoName;
    $imageData = base64_decode($fotoBase64);
    if ($imageData !== false && file_put_contents($fotoPath, $imageData)) {
        $fotoUrl = $fotoPath;
    }
}

$setClauses = [];
$params = [];
$types = '';

if (!empty($name)) { $setClauses[] = "name=?"; $params[] = $name; $types .= 's'; }
if (!empty($nim)) { $setClauses[] = "nim=?"; $params[] = $nim; $types .= 's'; }
if (!empty($email)) { $setClauses[] = "email=?"; $params[] = $email; $types .= 's'; }
if (!empty($password)) { $setClauses[] = "password=?"; $params[] = password_hash($password, PASSWORD_BCRYPT); $types .= 's'; }
if (!empty($fotoBase64)) { $setClauses[] = "foto_url=?"; $params[] = $fotoUrl; $types .= 's'; }

if (empty($setClauses)) {
    response(false, 'No fields to update', null);
}

$sql = "UPDATE users SET " . implode(', ', $setClauses) . " WHERE id=?";
$params[] = $userId;
$types .= 'i';

$stmt = $conn->prepare($sql);
$stmt->bind_param($types, ...$params);
$stmt->execute();
$stmt->close();

$result = $conn->query("SELECT * FROM users WHERE id = $userId");
$updated = $result->fetch_assoc();
unset($updated['password']);

response(true, 'Profile updated', $updated);
