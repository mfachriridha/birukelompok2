<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();
$input = input();
$action = $input['action'] ?? '';

if ($action === 'register') {
    $name = trim($input['name'] ?? '');
    $nim = trim($input['nim'] ?? '');
    $email = trim($input['email'] ?? '');
    $password = (string)($input['password'] ?? '');

    if ($name === '' || $nim === '' || $email === '' || $password === '') {
        response(false, 'Nama, NIM, email, dan password wajib diisi', null, 422);
    }

    $check = $conn->prepare('SELECT id FROM users WHERE email = ? LIMIT 1');
    $check->bind_param('s', $email);
    $check->execute();
    if ($check->get_result()->num_rows > 0) {
        response(false, 'Email sudah terdaftar', null, 409);
    }

    $hash = password_hash($password, PASSWORD_DEFAULT);
    $role = 'pengguna';
    $stmt = $conn->prepare('INSERT INTO users (name, nim, email, password, role) VALUES (?, ?, ?, ?, ?)');
    $stmt->bind_param('sssss', $name, $nim, $email, $hash, $role);
    if (!$stmt->execute()) {
        response(false, 'Register gagal: ' . $stmt->error, null, 500);
    }

    $userId = $stmt->insert_id;
    $token = jwt_encode([
        'user_id' => $userId,
        'role' => $role,
        'exp' => time() + JWT_EXPIRY,
    ]);

    response(true, 'Register berhasil', [
        'token' => $token,
        'user' => user_row([
            'id' => $userId,
            'name' => $name,
            'nim' => $nim,
            'email' => $email,
            'role' => $role,
            'photo' => null,
        ]),
    ]);
}

if ($action === 'login') {
    $email = trim($input['email'] ?? '');
    $password = (string)($input['password'] ?? '');

    $stmt = $conn->prepare('SELECT * FROM users WHERE email = ? LIMIT 1');
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $row = $stmt->get_result()->fetch_assoc();

    if (!$row || !password_verify($password, $row['password'])) {
        response(false, 'Email atau password salah', null, 401);
    }

    $token = jwt_encode([
        'user_id' => (int)$row['id'],
        'role' => $row['role'],
        'exp' => time() + JWT_EXPIRY,
    ]);

    response(true, 'Login berhasil', [
        'token' => $token,
        'user' => user_row($row),
    ]);
}

response(false, 'Action auth tidak dikenal', null, 400);
?>
