<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$DB_HOST = 'localhost';
$DB_USER = 'root';
$DB_PASS = '';
$DB_NAME = 'biru046';

$conn = new mysqli($DB_HOST, $DB_USER, $DB_PASS, $DB_NAME);
$conn->set_charset('utf8');

if ($conn->connect_error) {
    response(false, 'Database connection failed: ' . $conn->connect_error, null);
    exit;
}

function response($success, $message, $data = null) {
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

function getToken() {
    return $_POST['api_token'] ?? $_GET['api_token'] ?? '';
}

function requireAuth() {
    global $conn;
    $token = getToken();
    if (empty($token)) {
        response(false, 'Unauthorized: no token', null);
    }
    $stmt = $conn->prepare("SELECT * FROM users WHERE api_token = ? LIMIT 1");
    $stmt->bind_param('s', $token);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc();
    $stmt->close();
    if (!$user) {
        response(false, 'Unauthorized: invalid token', null);
    }
    return $user;
}

function generateToken($length = 64) {
    return bin2hex(random_bytes($length / 2));
}
