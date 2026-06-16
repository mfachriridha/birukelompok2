package com.example.birukelompok2.util

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.User
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReport046 {
    fun write(output: OutputStream, user: User, bookings: List<Booking>) {
        val document = PdfDocument()
        val normalPaint = Paint().apply {
            textSize = 12f
            isAntiAlias = true
        }
        val titlePaint = Paint(normalPaint).apply {
            textSize = 18f
            isFakeBoldText = true
        }
        val boldPaint = Paint(normalPaint).apply {
            isFakeBoldText = true
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var y = 48f

        fun line(text: String, paint: Paint = normalPaint) {
            if (y > 800f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 48f
            }
            canvas.drawText(text.take(96), 32f, y, paint)
            y += if (paint == titlePaint) 28f else 20f
        }

        val locale = Locale.Builder().setLanguage("id").setRegion("ID").build()
        line("LAPORAN BOOKING RUANGAN BIRU", titlePaint)
        line("")
        line("Dicetak oleh: ${user.name} (${user.role})")
        line("Tanggal cetak: ${SimpleDateFormat("dd-MM-yyyy HH:mm", locale).format(Date())}")
        line("Total data: ${bookings.size}")
        line("")

        bookings.forEachIndexed { index, booking ->
            line("${index + 1}. ${booking.roomName} - ${booking.status.uppercase()}", boldPaint)
            line("   Peminjam: ${booking.userName} (${booking.userNim})")
            line("   Tanggal: ${booking.date}, ${booking.startTime}-${booking.endTime}")
            line("   Keperluan: ${booking.purpose}")
            if (booking.adminNote.isNotBlank()) {
                line("   Catatan admin: ${booking.adminNote}")
            }
            line("")
        }

        document.finishPage(page)
        document.writeTo(output)
        document.close()
    }
}
