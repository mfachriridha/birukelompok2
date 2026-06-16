package com.example.birukelompok2

import android.content.Intent
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

class LoginActivity046 : AppCompatActivity() {
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login046)
        VolleyClient046.init(this)
        val session = SessionManager046(this)
        if (session.isLoggedIn()) {
            startActivity(Intent(this, RoomsActivity046::class.java))
            finish()
            return
        }
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val pwd = findViewById<EditText>(R.id.et_password).text.toString().trim()
            if (email.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val url = VolleyClient046.getBaseUrl() + "login046.php"
            val req = object : StringRequest(POST, url,
                { resp ->
                    try {
                        val r = gson.fromJson(resp, com.example.birukelompok2.models.ApiResponse::class.java)
                        if (r.success && r.data != null && r.data.isJsonObject) {
                            val u = Gson().fromJson(r.data, com.example.birukelompok2.models.User::class.java)
                            session.saveSession(u.apiToken, u)
                            startActivity(Intent(this, RoomsActivity046::class.java))
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
                override fun getParams() = hashMapOf("email" to email, "password" to pwd)
            }
            VolleyClient046.getRequestQueue().add(req)
        }
    }
}
