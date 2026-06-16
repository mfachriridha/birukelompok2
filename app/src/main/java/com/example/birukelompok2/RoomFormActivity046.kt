package com.example.birukelompok2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson

class RoomFormActivity046 : AppCompatActivity() {
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private var roomId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_form046)
        session = SessionManager046(this)
        roomId = intent.getIntExtra("room_id", 0)
        if (roomId > 0) {
            findViewById<EditText>(R.id.et_nama).setText(intent.getStringExtra("room_nama"))
            findViewById<EditText>(R.id.et_lokasi).setText(intent.getStringExtra("room_lokasi"))
            findViewById<EditText>(R.id.et_kapasitas).setText(intent.getIntExtra("room_kapasitas", 0).toString())
            findViewById<EditText>(R.id.et_fasilitas).setText(intent.getStringExtra("room_fasilitas"))
            findViewById<EditText>(R.id.et_deskripsi).setText(intent.getStringExtra("room_deskripsi"))
        }
        findViewById<Button>(R.id.btn_save).setOnClickListener { saveRoom() }
    }

    private fun saveRoom() {
        val nama = findViewById<EditText>(R.id.et_nama).text.toString().trim()
        val lokasi = findViewById<EditText>(R.id.et_lokasi).text.toString().trim()
        val kapasitas = findViewById<EditText>(R.id.et_kapasitas).text.toString().trim()
        val fasilitas = findViewById<EditText>(R.id.et_fasilitas).text.toString().trim()
        val deskripsi = findViewById<EditText>(R.id.et_deskripsi).text.toString().trim()
        if (nama.isEmpty() || lokasi.isEmpty() || kapasitas.isEmpty()) {
            Toast.makeText(this, "Nama, lokasi, dan kapasitas harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val token = session.getToken() ?: return
        val params = mutableMapOf(
            "api_token" to token,
            "nama" to nama, "lokasi" to lokasi, "kapasitas" to kapasitas,
            "fasilitas" to fasilitas, "deskripsi" to deskripsi
        )
        if (roomId > 0) params["id"] = roomId.toString()
        val url = VolleyClient046.getBaseUrl() + "save_room046.php"
        val req = object : StringRequest(POST, url,
            { resp ->
                try {
                    val r = gson.fromJson(resp, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (r.success) {
                        Toast.makeText(this, "Ruangan disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, r.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) { Toast.makeText(this, "Parse: ${e.message}", Toast.LENGTH_SHORT).show() }
            },
            { err -> Toast.makeText(this, "Network: ${err.message}", Toast.LENGTH_SHORT).show() }
        ) { override fun getParams() = params }
        VolleyClient046.getRequestQueue().add(req)
    }
}
