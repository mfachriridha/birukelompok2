package com.example.birukelompok2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomsActivity046 : AppCompatActivity() {
    private val gson = Gson()
    private lateinit var session: SessionManager046
    private val rooms = mutableListOf<com.example.birukelompok2.models.Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms046)
        session = SessionManager046(this)
        if (!session.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity046::class.java))
            finish()
            return
        }
        val btnAdd = findViewById<Button>(R.id.btn_add_room)
        if (session.isAdmin()) {
            btnAdd.visibility = android.view.View.VISIBLE
            btnAdd.setOnClickListener {
                startActivity(Intent(this, RoomFormActivity046::class.java))
            }
        } else {
            btnAdd.visibility = android.view.View.GONE
        }
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            session.clearSession()
            startActivity(Intent(this, LoginActivity046::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    private fun loadRooms() {
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "rooms046.php"
        val req = object : StringRequest(POST, url,
            { resp ->
                try {
                    val r = gson.fromJson(resp, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (r.success && r.data != null && r.data.isJsonObject) {
                        val arr = r.data.asJsonObject.getAsJsonArray("rooms")
                        val list: List<com.example.birukelompok2.models.Room> = gson.fromJson(
                            arr, object : TypeToken<List<com.example.birukelompok2.models.Room>>() {}.type
                        )
                        rooms.clear()
                        rooms.addAll(list)
                        val lv = findViewById<ListView>(R.id.lv_rooms)
                        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                            list.map { "${it.nama} — ${it.lokasi} (kap ${it.kapasitas})" })
                        lv.setOnItemClickListener { _, _, position, _ ->
                            val room = rooms[position]
                            val intent = Intent(this, BookingFormActivity046::class.java)
                            intent.putExtra("room_id", room.id)
                            intent.putExtra("room_name", room.nama)
                            intent.putExtra("room_lokasi", room.lokasi)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, r.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) { Toast.makeText(this, "Parse: ${e.message}", Toast.LENGTH_SHORT).show() }
            },
            { err -> Toast.makeText(this, "Network: ${err.message}", Toast.LENGTH_SHORT).show() }
        ) { override fun getParams() = hashMapOf("api_token" to token) }
        VolleyClient046.getRequestQueue().add(req)
    }
}
