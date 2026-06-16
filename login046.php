<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    response(false, 'Email and password are required', null);
}

$stmt = $conn->prepare("SELECT * FROM users WHERE email = ? LIMIT 1");
$stmt->bind_param('s', $email);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();
$stmt->close();

if (!$user || !password_verify($password, $user['password'])) {
    response(false, 'Invalid email or password', null);
}

$token = generateToken();
$stmt = $conn->prepare("UPDATE users SET api_token = ? WHERE id = ?");
$stmt->bind_param('si', $token, $user['id']);
$stmt->execute();
$stmt->close();

$user['api_token'] = $token;
unset($user['password']);

response(true, 'Login successful', $user);
