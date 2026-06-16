# BiruKelompok2

Aplikasi Booking Ruangan Kampus berbasis Android + PHP.

## Struktur Repository

- **Branch `main`** — Aplikasi Android (Kotlin + XML + Activities/Fragments + Volley)
- **Branch `PHP`** — Backend API (PHP + MySQL, 1 file = 1 endpoint)

---

## Cara Clone

Clone masing-masing branch langsung ke folder tujuan. Tidak perlu clone seluruh repo.

### 1. Clone Android (Branch main)

```bash
git clone -b main --single-branch https://github.com/mfachriridha/birukelompok2.git
```

Buka folder hasil clone di **Android Studio**. Gradle akan sync otomatis.

### 2. Clone Backend PHP (Branch PHP)

Arahkan ke folder localhost sesuai lingkungan:

| Lingkungan | Perintah |
|------------|----------|
| **WAMP**   | `cd C:\wamp64\www` |
| **XAMPP**  | `cd C:\xampp\htdocs` |

```bash
git clone -b PHP --single-branch https://github.com/mfachriridha/birukelompok2.git
```

### 3. Import Database

1. Buka **phpMyAdmin** (http://localhost/phpmyadmin)
2. Klik **New** → buat database `biru046`
3. Pilih database `biru046` → tab **Import**
4. Pilih file `biru046.sql` → **Go**

### 4. Konfigurasi

- Database: `config046.php` (host: `localhost`, user: `root`, password: `""`)
- Default admin: `admin@biru.local` / `password`
- URL backend otomatis terdeteksi via `VolleyClient046.discoverBaseUrl()`

---

## Fitur

| Fitur | Role |
|-------|------|
| Auth (Login/Register) | Semua |
| Lihat Ruangan | Semua |
| Booking Ruangan | User |
| Approve/Reject Booking | Admin |
| Kelola User | Admin |
| Kelola Ruangan (CRUD) | Admin |
| Laporan + Grafik (Pie/Bar) | Semua |
| Export PDF | Semua |

## Tech Stack

- **Android**: Kotlin, XML Layouts, Activities + Fragments, ViewBinding, Volley, MPAndroidChart, PdfDocument
- **Backend**: PHP 8+, MySQL, bcrypt, Random Token Auth, Base64 Photo
