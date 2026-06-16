<?php
require_once __DIR__ . '/database046.php';

define('JWT_SECRET', 'biru_kelompok2_secret_key_046_2026');
define('JWT_EXPIRY', 86400); // 24 jam

function jwt_encode(array $payload): string
{
    $header = base64url_encode(json_encode(['alg' => 'HS256', 'typ' => 'JWT']));
    $payload = base64url_encode(json_encode($payload));
    $signature = base64url_encode(
        hash_hmac('sha256', "$header.$payload", JWT_SECRET, true)
    );
    return "$header.$payload.$signature";
}

function jwt_decode(string $token): ?array
{
    $parts = explode('.', $token);
    if (count($parts) !== 3) return null;

    [$header, $payload, $signature] = $parts;

    $expected = base64url_encode(
        hash_hmac('sha256', "$header.$payload", JWT_SECRET, true)
    );

    if (!hash_equals($expected, $signature)) return null;

    $data = json_decode(base64url_decode($payload), true);
    if (!$data || !isset($data['exp']) || $data['exp'] < time()) return null;

    return $data;
}

function base64url_encode(string $data): string
{
    return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
}

function base64url_decode(string $data): string
{
    return base64_decode(strtr($data, '-_', '+/'));
}

function get_token_user(): ?array
{
    $auth = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if (empty($auth) && function_exists('apache_request_headers')) {
        $headers = apache_request_headers();
        $auth = $headers['Authorization'] ?? $headers['authorization'] ?? '';
    }
    if (empty($auth)) return null;

    if (str_starts_with($auth, 'Bearer ')) {
        $token = substr($auth, 7);
        return jwt_decode($token);
    }
    return null;
}

function require_auth(): array
{
    $user = get_token_user();
    if (!$user) {
        response(false, 'Unauthorized: token tidak valid atau expired', null, 401);
    }
    return $user;
}

function require_admin(): array
{
    $user = require_auth();
    if (($user['role'] ?? '') !== 'admin') {
        response(false, 'Forbidden: hanya admin yang bisa mengakses', null, 403);
    }
    return $user;
}
?>
