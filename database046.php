<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

function db(): mysqli
{
    $host = 'localhost';
    $user = 'root';
    $pass = '';
    $name = 'biru046';

    $conn = new mysqli($host, $user, $pass, $name);
    if ($conn->connect_error) {
        response(false, 'Koneksi database gagal: ' . $conn->connect_error, null, 500);
    }
    $conn->set_charset('utf8mb4');
    return $conn;
}

function input(): array
{
    $raw = file_get_contents('php://input');
    $data = json_decode($raw, true);
    return is_array($data) ? $data : [];
}

function response(bool $success, string $message, $data = null, int $code = 200): void
{
    http_response_code($code);
    $payload = [
        'success' => $success,
        'message' => $message,
    ];
    if ($data !== null) {
        $payload['data'] = $data;
    }
    echo json_encode($payload, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
    exit;
}

function base_url(): string
{
    $scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
    $host = $_SERVER['HTTP_HOST'] ?? 'localhost';
    $dir = str_replace('\\', '/', dirname($_SERVER['SCRIPT_NAME'] ?? '/'));
    $dir = rtrim($dir, '/');
    return $scheme . '://' . $host . ($dir === '' ? '' : $dir);
}

function photo_url(?string $photo): string
{
    if (!$photo) return '';
    if (str_starts_with($photo, 'http://') || str_starts_with($photo, 'https://')) return $photo;
    return base_url() . '/' . ltrim($photo, '/');
}

function user_row(array $row): array
{
    return [
        'id' => (int)$row['id'],
        'name' => $row['name'] ?? '',
        'nim' => $row['nim'] ?? '',
        'email' => $row['email'] ?? '',
        'role' => $row['role'] ?? 'pengguna',
        'photo' => photo_url($row['photo'] ?? ''),
    ];
}

function booking_row(array $row): array
{
    return [
        'id' => (int)$row['id'],
        'user_id' => (int)($row['user_id'] ?? 0),
        'room_id' => (int)($row['room_id'] ?? 0),
        'user_name' => $row['user_name'] ?? '',
        'user_nim' => $row['user_nim'] ?? '',
        'room_name' => $row['room_name'] ?? '',
        'date' => $row['date'] ?? '',
        'start_time' => substr((string)($row['start_time'] ?? ''), 0, 5),
        'end_time' => substr((string)($row['end_time'] ?? ''), 0, 5),
        'purpose' => $row['purpose'] ?? '',
        'status' => $row['status'] ?? 'pending',
        'admin_note' => $row['admin_note'] ?? '',
    ];
}
?>
