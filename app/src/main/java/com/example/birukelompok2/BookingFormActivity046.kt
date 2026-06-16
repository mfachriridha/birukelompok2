package com.example.birukelompok2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.example.birukelompok2.utils.loadImageFromUrl
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.ActivityBookingForm046Binding
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.models.Room
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import java.util.Calendar

class BookingFormActivity046 : AppCompatActivity() {
    private lateinit var binding: ActivityBookingForm046Binding
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private var selectedRoom: Room? = null
    private var selectedDate: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingForm046Binding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager046(this)

        val roomId = intent.getIntExtra("room_id", 0)
        val roomName = intent.getStringExtra("room_name")
        val roomLokasi = intent.getStringExtra("room_lokasi")
        val roomFotoUrl = intent.getStringExtra("room_foto_url")

        if (roomId > 0) {
            selectedRoom = Room(id = roomId, nama = roomName ?: "", lokasi = roomLokasi ?: "", fotoUrl = roomFotoUrl ?: "")
            binding.roomInfoCard.visibility = View.VISIBLE
            binding.tvRoomName.text = roomName
            binding.tvRoomLokasi.text = roomLokasi

            if (!roomFotoUrl.isNullOrEmpty()) {
                val imageUrl = VolleyClient046.getBaseUrl() + roomFotoUrl
                loadImageFromUrl(imageUrl, binding.ivRoomPhoto)
            }
        }

        binding.btnDate.setOnClickListener {
            DatePickerDialog(this,
                { _, y, m, d ->
                    selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    binding.btnDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnStartTime.setOnClickListener {
            TimePickerDialog(this,
                { _, h, m ->
                    selectedStartTime = String.format("%02d:%02d", h, m)
                    binding.btnStartTime.text = selectedStartTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnEndTime.setOnClickListener {
            TimePickerDialog(this,
                { _, h, m ->
                    selectedEndTime = String.format("%02d:%02d", h, m)
                    binding.btnEndTime.text = selectedEndTime
                },
                calendar.get(Calendar.HOUR_OF_DAY) + 1,
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSubmit.setOnClickListener { submitBooking() }
    }

    private fun submitBooking() {
        if (selectedRoom == null) {
            // Pick from all rooms
            pickRoom()
            return
        }

        val roomId = selectedRoom!!.id
        val tanggal = selectedDate
        val jamMulai = selectedStartTime
        val jamSelesai = selectedEndTime
        val keperluan = binding.etKeperluan.text.toString().trim()

        if (tanggal == null || jamMulai == null || jamSelesai == null || keperluan.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "create_booking046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success) {
                        Toast.makeText(this, "Booking berhasil diajukan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, apiResp.message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = hashMapOf(
                "api_token" to token,
                "room_id" to roomId.toString(),
                "tanggal" to tanggal,
                "jam_mulai" to jamMulai,
                "jam_selesai" to jamSelesai,
                "keperluan" to keperluan
            )
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun pickRoom() {
        Toast.makeText(this, "Pilih ruangan dari tab Ruangan", Toast.LENGTH_SHORT).show()
        finish()
    }
}
