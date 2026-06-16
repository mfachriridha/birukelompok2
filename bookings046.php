<?php
require_once __DIR__ . '/database046.php';
require_once __DIR__ . '/middleware046.php';

$conn = db();

function has_conflict(mysqli $conn, int $roomId, string $date, string $start, string $end, int $ignoreId = 0, array $statuses = ['pending', 'approved']): bool
{
    $placeholders = implode(',', array_fill(0, count($statuses), '?'));
    $types = 'isiss' . str_repeat('s', count($statuses));
    $sql = "SELECT id FROM bookings WHERE room_id = ? AND date = ? AND id <> ? AND NOT (end_time <= ? OR start_time >= ?) AND status IN ($placeholders) LIMIT 1";
    $stmt = $conn->prepare($sql);
    $params = array_merge([$roomId, $date, $ignoreId, $start, $end], $statuses);
    $stmt->bind_param($types, ...$params);
    $stmt->execute();
    return $stmt->get_result()->num_rows > 0;
}

$tokenUser = require_auth();
$isAdmin = $tokenUser['role'] === 'admin';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if ($isAdmin) {
        $stmt = $conn->prepare('SELECT * FROM bookings ORDER BY date DESC, start_time DESC, id DESC');
    } else {
        $userId = (int)($tokenUser['user_id'] ?? 0);
        $stmt = $conn->prepare('SELECT * FROM bookings WHERE user_id = ? ORDER BY date DESC, start_time DESC, id DESC');
        $stmt->bind_param('i', $userId);
    }
    $stmt->execute();
    $result = $stmt->get_result();
    $data = [];
    while ($row = $result->fetch_assoc()) {
        $data[] = booking_row($row);
    }
    response(true, 'Data booking berhasil diambil', $data);
}

$input = input();
$action = $input['action'] ?? '';

if ($action === 'create') {
    $userId = (int)($tokenUser['user_id'] ?? 0);
    $roomId = (int)($input['room_id'] ?? 0);
    $userName = trim($input['user_name'] ?? '');
    $userNim = trim($input['user_nim'] ?? '');
    $roomName = trim($input['room_name'] ?? '');
    $date = trim($input['date'] ?? '');
    $start = trim($input['start_time'] ?? '');
    $end = trim($input['end_time'] ?? '');
    $purpose = trim($input['purpose'] ?? '');
    $status = 'pending';

    if ($userId <= 0 || $roomId <= 0 || $date === '' || $start === '' || $end === '' || $purpose === '') {
        response(false, 'Data booking belum lengkap', null, 422);
    }
    if ($end <= $start) {
        response(false, 'Jam selesai harus lebih besar dari jam mulai', null, 422);
    }
    if (has_conflict($conn, $roomId, $date, $start, $end)) {
        response(false, 'Jadwal ruangan bentrok dengan booking lain', null, 409);
    }

    $stmt = $conn->prepare('INSERT INTO bookings (user_id, room_id, user_name, user_nim, room_name, date, start_time, end_time, purpose, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)');
    $stmt->bind_param('iissssssss', $userId, $roomId, $userName, $userNim, $roomName, $date, $start, $end, $purpose, $status);
    if (!$stmt->execute()) {
        response(false, 'Booking gagal: ' . $stmt->error, null, 500);
    }
    response(true, 'Booking berhasil diajukan dan menunggu approval admin');
}

if ($action === 'status') {
    if (!$isAdmin) response(false, 'Hanya admin yang bisa update status booking', null, 403);

    $id = (int)($input['id'] ?? 0);
    $status = trim($input['status'] ?? '');
    $note = trim($input['admin_note'] ?? '');
    if (!in_array($status, ['approved', 'rejected', 'cancelled'], true)) {
        response(false, 'Status booking tidak valid', null, 422);
    }

    if ($status === 'approved') {
        $stmtRead = $conn->prepare('SELECT room_id, date, start_time, end_time FROM bookings WHERE id = ? LIMIT 1');
        $stmtRead->bind_param('i', $id);
        $stmtRead->execute();
        $booking = $stmtRead->get_result()->fetch_assoc();
        if (!$booking) {
            response(false, 'Booking tidak ditemukan', null, 404);
        }
        if (has_conflict($conn, (int)$booking['room_id'], $booking['date'], $booking['start_time'], $booking['end_time'], $id, ['approved'])) {
            response(false, 'Tidak bisa approve karena bentrok dengan booking approved lain', null, 409);
        }
    }

    $stmt = $conn->prepare('UPDATE bookings SET status = ?, admin_note = ? WHERE id = ?');
    $stmt->bind_param('ssi', $status, $note, $id);
    if (!$stmt->execute()) {
        response(false, 'Update status gagal: ' . $stmt->error, null, 500);
    }
    response(true, 'Status booking berhasil diperbarui');
}

response(false, 'Action booking tidak dikenal', null, 400);
?>
