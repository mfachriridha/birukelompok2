<?php
require_once 'config046.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    response(false, 'Method not allowed', null);
}

$name = trim($_POST['name'] ?? '');
$nim = trim($_POST['nim'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

if (empty($name) || empty($nim) || empty($email) || empty($password)) {
    response(false, 'All fields are required', null);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    response(false, 'Invalid email format', null);
}

$stmt = $conn->prepare("SELECT id FROM users WHERE email = ? LIMIT 1");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    response(false, 'Email already registered', null);
}
$stmt->close();

$hashedPassword = password_hash($password, PASSWORD_BCRYPT);
$stmt = $conn->prepare("INSERT INTO users (name, nim, email, password) VALUES (?, ?, ?, ?)");
$stmt->bind_param('ssss', $name, $nim, $email, $hashedPassword);
$stmt->execute();
$stmt->close();

response(true, 'Registration successful', null);
