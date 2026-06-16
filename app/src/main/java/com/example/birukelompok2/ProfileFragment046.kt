package com.example.birukelompok2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.FragmentProfile046Binding
import com.example.birukelompok2.models.User
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson

class ProfileFragment046 : Fragment() {
    private var _binding: FragmentProfile046Binding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private var selectedPhotoBase64: String? = null
    private var currentUser: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfile046Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager046(requireContext())

        loadProfile()

        binding.btnSave.setOnClickListener { updateProfile() }
        binding.btnLogout.setOnClickListener { logout() }
        binding.photoContainer.setOnClickListener { pickPhoto() }
    }

    private fun loadProfile() {
        currentUser = session.getUser()
        currentUser?.let { populateUi(it) }

        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "profile046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val user = gson.fromJson(apiResp.data, User::class.java)
                        currentUser = user
                        session.saveSession(user.apiToken, user)
                        populateUi(user)
                    }
                } catch (_: Exception) {}
            },
            { _ -> }
        ) {
            override fun getParams() = hashMapOf("api_token" to token)
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun populateUi(user: User) {
        binding.etName.setText(user.name)
        binding.etNim.setText(user.nim)
        binding.etEmail.setText(user.email)
        binding.tvInitials.text = user.name.firstOrNull()?.uppercase() ?: "?"

        if (user.fotoUrl.isNotEmpty()) {
            val imageUrl = VolleyClient046.getBaseUrl() + user.fotoUrl
            val request = ImageRequest(imageUrl,
                { bitmap ->
                    binding.ivPhoto.setImageBitmap(bitmap)
                    binding.ivPhoto.visibility = View.VISIBLE
                    binding.tvInitials.visibility = View.GONE
                }, 0, 0, ImageRequest.ScaleType.CENTER_CROP, null, { _ -> })
            VolleyClient046.getRequestQueue().add(request)
        }
    }

    private fun updateProfile() {
        val token = session.getToken() ?: return
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || nim.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Nama, NIM, dan Email harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val params = mutableMapOf("api_token" to token, "name" to name, "nim" to nim, "email" to email)
        if (password.isNotEmpty()) {
            params["password"] = password
        }
        if (selectedPhotoBase64 != null) {
            params["foto_base64"] = selectedPhotoBase64!!
        }

        val url = VolleyClient046.getBaseUrl() + "update_profile046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, com.example.birukelompok2.models.ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val user = gson.fromJson(apiResp.data, User::class.java)
                        session.saveSession(user.apiToken, user)
                        Toast.makeText(requireContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), apiResp.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, PHOTO_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val stream = java.io.ByteArrayOutputStream()
                bitmap?.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                selectedPhotoBase64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)

                binding.ivPhoto.setImageBitmap(bitmap)
                binding.ivPhoto.visibility = View.VISIBLE
                binding.tvInitials.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        val token = session.getToken() ?: ""
        val url = VolleyClient046.getBaseUrl() + "logout046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            {
                session.clearSession()
                startActivity(Intent(requireContext(), LoginActivity046::class.java))
                requireActivity().finish()
            },
            {
                session.clearSession()
                startActivity(Intent(requireContext(), LoginActivity046::class.java))
                requireActivity().finish()
            }
        ) {
            override fun getParams() = hashMapOf("api_token" to token)
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PHOTO_REQUEST_CODE = 1001
    }
}
