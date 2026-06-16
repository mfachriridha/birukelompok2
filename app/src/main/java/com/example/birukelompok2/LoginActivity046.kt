package com.example.birukelompok2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
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

        try {
            binding = ActivityLogin046Binding.inflate(layoutInflater)
            setContentView(binding.root)

            session = SessionManager046(this)
            VolleyClient046.init(this)

            if (session.isLoggedIn()) {
                navigateToMain()
                return
            }

            binding.btnLogin.setOnClickListener {
                try {
                    val email = binding.etEmail.text.toString().trim()
                    val password = binding.etPassword.text.toString().trim()
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    login(email, password)
                } catch (e: Exception) {
                    showError(e)
                }
            }

            binding.tvRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity046::class.java))
            }
        } catch (e: Exception) {
            showError(e)
        }
    }

    private fun showError(e: Exception) {
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 64, 32, 32)
        }
        val title = TextView(this).apply {
            text = "CRASH ERROR:"
            textSize = 20f
            setTextColor(Color.RED)
        }
        val message = TextView(this).apply {
            text = e.toString() + "\n\n" + e.stackTraceToString()
            textSize = 14f
            setTextColor(Color.BLACK)
            setTextIsSelectable(true)
        }
        layout.addView(title)
        layout.addView(message)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun login(email: String, password: String) {
        val url = VolleyClient046.getBaseUrl() + "login046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null && apiResp.data.isJsonObject) {
                        val user = gson.fromJson(apiResp.data, User::class.java)
                        session.saveSession(user.apiToken, user)
                        navigateToMain()
                    } else {
                        Toast.makeText(this, apiResp.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    showError(e)
                }
            },
            { error ->
                showError(Exception("Network error: ${error.message}", error))
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
