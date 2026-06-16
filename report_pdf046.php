<?php
require_once 'config046.php';

$result = $conn->query("
    SELECT b.*, r.nama AS room_name, u.name AS user_name
    FROM bookings b
    JOIN rooms r ON b.room_id = r.id
    JOIN users u ON b.user_id = u.id
    ORDER BY b.created_at DESC
");
$bookings = [];
while ($row = $result->fetch_assoc()) {
    $bookings[] = $row;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biru - Laporan PDF</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.31/jspdf.plugin.autotable.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; font-size: 12px; }
        th { background: #2563EB; color: white; }
        button { padding: 10px 20px; background: #2563EB; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
        button:hover { background: #7C3AED; }
    </style>
</head>
<body>
    <h1>Biru - Laporan Booking Ruangan</h1>
    <p>Total bookings: <?= count($bookings) ?></p>
    <button onclick="generatePDF()">Download PDF</button>
    <table id="bookingTable">
        <thead>
            <tr>
                <th>No</th>
                <th>Ruangan</th>
                <th>Peminjam</th>
                <th>Tanggal</th>
                <th>Jam</th>
                <th>Keperluan</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <?php $no = 1; foreach ($bookings as $b): ?>
            <tr>
                <td><?= $no++ ?></td>
                <td><?= htmlspecialchars($b['room_name']) ?></td>
                <td><?= htmlspecialchars($b['user_name']) ?></td>
                <td><?= $b['tanggal'] ?></td>
                <td><?= $b['jam_mulai'] ?> - <?= $b['jam_selesai'] ?></td>
                <td><?= htmlspecialchars($b['keperluan']) ?></td>
                <td><?= $b['status'] ?></td>
            </tr>
            <?php endforeach; ?>
        </tbody>
    </table>

    <script>
    function generatePDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF('l', 'mm', 'a4');
        doc.setFontSize(16);
        doc.text('Biru - Laporan Booking Ruangan', 14, 15);
        doc.setFontSize(10);
        doc.text('Generated: ' + new Date().toLocaleDateString('id-ID'), 14, 22);
        doc.autoTable({
            startY: 28,
            html: '#bookingTable',
            theme: 'grid',
            styles: { fontSize: 8 },
            headStyles: { fillColor: [37, 99, 235] }
        });
        doc.save('biru_laporan.pdf');
    }
    </script>
</body>
</html>
