package com.example.birukelompok2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.ActivityRegister046Binding
import com.google.gson.Gson

class RegisterActivity046 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister046Binding
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister046Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val nim = binding.etNim.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || nim.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(name, nim, email, password)
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun register(name: String, nim: String, email: String, password: String) {
        val url = VolleyClient046.getBaseUrl() + "register046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (apiResp.success) {
                        Toast.makeText(this, "Pendaftaran berhasil, silakan login", Toast.LENGTH_LONG).show()
                        finish()
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
            override fun getParams() = hashMapOf("name" to name, "nim" to nim, "email" to email, "password" to password)
        }
        VolleyClient046.getRequestQueue().add(request)
    }
}
