package com.example.birukelompok2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.models.Room
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import java.util.Calendar

class BookingFormActivity046 : AppCompatActivity() {
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private var selectedRoom: Room? = null
    private var selectedDate: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_form046)
        session = SessionManager046(this)
        val roomId = intent.getIntExtra("room_id", 0)
        val roomName = intent.getStringExtra("room_name")
        val roomLokasi = intent.getStringExtra("room_lokasi")
        if (roomId > 0) {
            selectedRoom = Room(id = roomId, nama = roomName ?: "", lokasi = roomLokasi ?: "")
            findViewById<TextView>(R.id.tv_room_name).text = roomName
            findViewById<TextView>(R.id.tv_room_lokasi).text = roomLokasi
        } else {
            Toast.makeText(this, "Pilih ruangan dari daftar ruangan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        findViewById<Button>(R.id.btn_date).setOnClickListener {
            DatePickerDialog(this,
                { _, y, m, d ->
                    selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    findViewById<Button>(R.id.btn_date).text = selectedDate
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        findViewById<Button>(R.id.btn_start_time).setOnClickListener {
            TimePickerDialog(this,
                { _, h, m ->
                    selectedStartTime = String.format("%02d:%02d", h, m)
                    findViewById<Button>(R.id.btn_start_time).text = selectedStartTime
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            ).show()
        }
        findViewById<Button>(R.id.btn_end_time).setOnClickListener {
            TimePickerDialog(this,
                { _, h, m ->
                    selectedEndTime = String.format("%02d:%02d", h, m)
                    findViewById<Button>(R.id.btn_end_time).text = selectedEndTime
                },
                calendar.get(Calendar.HOUR_OF_DAY) + 1, calendar.get(Calendar.MINUTE), true
            ).show()
        }
        findViewById<Button>(R.id.btn_submit).setOnClickListener { submitBooking() }
    }

    private fun submitBooking() {
        val roomId = selectedRoom?.id ?: run {
            Toast.makeText(this, "Ruangan tidak dipilih", Toast.LENGTH_SHORT).show(); return
        }
        val tanggal = selectedDate
        val jamMulai = selectedStartTime
        val jamSelesai = selectedEndTime
        val keperluan = findViewById<EditText>(R.id.et_keperluan).text.toString().trim()
        if (tanggal == null || jamMulai == null || jamSelesai == null || keperluan.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "create_booking046.php"
        val req = object : StringRequest(POST, url,
            { resp ->
                try {
                    val r = gson.fromJson(resp, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (r.success) {
                        Toast.makeText(this, "Booking berhasil diajukan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, r.message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) { Toast.makeText(this, "Parse: ${e.message}", Toast.LENGTH_SHORT).show() }
            },
            { err -> Toast.makeText(this, "Network: ${err.message}", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams() = hashMapOf(
                "api_token" to token, "room_id" to roomId.toString(),
                "tanggal" to tanggal, "jam_mulai" to jamMulai,
                "jam_selesai" to jamSelesai, "keperluan" to keperluan
            )
        }
        VolleyClient046.getRequestQueue().add(req)
    }
}
