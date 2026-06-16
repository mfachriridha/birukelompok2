# BiruKelompok2

Aplikasi Booking Ruangan Kampus berbasis Android + PHP.

## Struktur Repository

- **Branch `main`** — Aplikasi Android (Kotlin + Jetpack Compose)
- **Branch `PHP`** — Backend API (PHP + MySQL)

---

## Cara Clone

Clone masing-masing branch langsung ke folder tujuan. Tidak perlu clone seluruh repo.

### 1. Clone Android (Branch main)

Buka terminal, arahkan ke folder project Android Studio, lalu clone:

```bash
git clone -b main --single-branch https://github.com/mfachriridha/birukelompok2.git
```

Hasilnya folder `birukelompok2` langsung bisa dibuka di **Android Studio**. Gradle akan sync otomatis.

> Opsional: rename folder jika ingin nama berbeda.

### 2. Clone Backend PHP (Branch PHP)

Buka terminal, arahkan ke folder localhost sesuai lingkungan, lalu clone:

| Lingkungan | Perintah |
|------------|----------|
| **WAMP**   | `cd C:\wamp64\www` |
| **XAMPP**  | `cd C:\xampp\htdocs` |
| **Laragon**| `cd C:\laragon\www` |
| **PHP built-in** | Di mana saja, lalu `php -S localhost:8080` |

```bash
git clone -b PHP --single-branch https://github.com/mfachriridha/birukelompok2.git
```

### 3. Import Database

1. Buka **phpMyAdmin** (http://localhost/phpmyadmin)
2. Klik **New** → buat database dengan nama `biru046`
3. Pilih database `biru046` → tab **Import**
4. Klik **Choose File** → pilih file `biru046.sql` dari folder hasil clone
5. Klik **Go**

### 4. Konfigurasi

- Database sudah dikonfigurasi di `database046.php` (host: `localhost`, user: `root`, password: `""`)
- Default admin: `admin@biru.local` / `password`
- URL backend otomatis terdeteksi oleh Android via `discoverBaseUrl`

---

## Tech Stack

- **Android**: Kotlin, Jetpack Compose, Retrofit, Coil, JWT
- **Backend**: PHP 8+, MySQL, JWT (HMAC-SHA256)
