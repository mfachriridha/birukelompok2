package com.example.birukelompok2

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.example.birukelompok2.utils.loadImageFromUrl
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.ActivityRoomForm046Binding
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class RoomFormActivity046 : AppCompatActivity() {
    private lateinit var binding: ActivityRoomForm046Binding
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private var roomId: Int? = null
    private var selectedPhotoBase64: String? = null
    private var existingFotoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomForm046Binding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager046(this)

        roomId = intent.getIntExtra("room_id", 0).takeIf { it > 0 }
        if (roomId != null) {
            binding.etNama.setText(intent.getStringExtra("room_nama"))
            binding.etLokasi.setText(intent.getStringExtra("room_lokasi"))
            binding.etKapasitas.setText(intent.getIntExtra("room_kapasitas", 0).toString())
            binding.etFasilitas.setText(intent.getStringExtra("room_fasilitas"))
            binding.etDeskripsi.setText(intent.getStringExtra("room_deskripsi"))
            existingFotoUrl = intent.getStringExtra("room_foto_url")

            if (!existingFotoUrl.isNullOrEmpty()) {
                val imageUrl = VolleyClient046.getBaseUrl() + existingFotoUrl
                loadImageFromUrl(imageUrl, binding.ivPhoto)
                binding.ivPhoto.visibility = View.VISIBLE
                binding.tvAddPhoto.visibility = View.GONE
            }
        }

        binding.photoContainer.setOnClickListener { pickPhoto() }
        binding.btnSave.setOnClickListener { saveRoom() }
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, PHOTO_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val stream = ByteArrayOutputStream()
                bitmap?.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                selectedPhotoBase64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)

                binding.ivPhoto.setImageBitmap(bitmap)
                binding.ivPhoto.visibility = View.VISIBLE
                binding.tvAddPhoto.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRoom() {
        val nama = binding.etNama.text.toString().trim()
        val lokasi = binding.etLokasi.text.toString().trim()
        val kapasitas = binding.etKapasitas.text.toString().trim()
        val fasilitas = binding.etFasilitas.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()

        if (nama.isEmpty() || lokasi.isEmpty() || kapasitas.isEmpty()) {
            Toast.makeText(this, "Nama, lokasi, dan kapasitas harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val token = session.getToken() ?: return
        val params = mutableMapOf(
            "api_token" to token,
            "nama" to nama,
            "lokasi" to lokasi,
            "kapasitas" to kapasitas,
            "fasilitas" to fasilitas,
            "deskripsi" to deskripsi
        )
        if (roomId != null) params["id"] = roomId.toString()
        if (selectedPhotoBase64 != null) params["foto_base64"] = selectedPhotoBase64!!

        val url = VolleyClient046.getBaseUrl() + "save_room046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success) {
                        Toast.makeText(this, "Ruangan disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, apiResp.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    companion object {
        private const val PHOTO_REQUEST_CODE = 1002
    }
}
