package com.example.birukelompok2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.ActivityLogin046Binding
import com.example.birukelompok2.models.User
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson

class LoginActivity046 : AppCompatActivity() {
    private lateinit var binding: ActivityLogin046Binding
    private lateinit var session: SessionManager046
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin046Binding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager046(this)
        VolleyClient046.init(this)

        // Pre-fill saved IP
        val savedIp = session.getServerIp()
        if (savedIp != null) {
            binding.etServerIp.setText(savedIp)
            VolleyClient046.setServerIp(savedIp)
        }

        if (session.isLoggedIn() && savedIp != null) {
            navigateToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            val ip = binding.etServerIp.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (ip.isEmpty()) {
                Toast.makeText(this, "IP Server harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            session.saveServerIp(ip)
            VolleyClient046.setServerIp(ip)
            login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity046::class.java))
        }
    }

    private fun login(email: String, password: String) {
        val url = VolleyClient046.getBaseUrl() + "login046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val user = gson.fromJson(apiResp.data, User::class.java)
                        session.saveSession(user.apiToken, user)
                        navigateToMain()
                    } else {
                        Toast.makeText(this, apiResp.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Network error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = hashMapOf("email" to email, "password" to password)
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity046::class.java))
        finish()
    }
}
