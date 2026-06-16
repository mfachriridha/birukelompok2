package com.example.birukelompok2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.google.gson.Gson

class RegisterActivity046 : AppCompatActivity() {
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register046)
        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val name = findViewById<EditText>(R.id.et_name).text.toString().trim()
            val nim = findViewById<EditText>(R.id.et_nim).text.toString().trim()
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val pwd = findViewById<EditText>(R.id.et_password).text.toString().trim()
            if (name.isEmpty() || nim.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val url = VolleyClient046.getBaseUrl() + "register046.php"
            val req = object : StringRequest(POST, url,
                { resp ->
                    try {
                        val r = gson.fromJson(resp, com.example.birukelompok2.models.ApiResponse::class.java)
                        if (r.success) {
                            Toast.makeText(this, "Pendaftaran berhasil, silakan login", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, r.message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Parse: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                { err -> Toast.makeText(this, "Network: ${err.message}", Toast.LENGTH_SHORT).show() }
            ) {
                override fun getParams() = hashMapOf("name" to name, "nim" to nim, "email" to email, "password" to pwd)
            }
            VolleyClient046.getRequestQueue().add(req)
        }
    }
}
