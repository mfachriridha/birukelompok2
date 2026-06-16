package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

object ApiHelper046 {

    fun init(context: Context) {
        VolleyClient046.init(context)
    }

    private fun baseUrl() = VolleyClient046.getBaseUrl()

    private fun post(
        endpoint: String,
        params: Map<String, String>,
        callback: (Boolean, String) -> Unit
    ) {
        val url = baseUrl() + endpoint
        val request = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response -> callback(true, response) },
            Response.ErrorListener { error ->
                callback(false, error.message ?: "Unknown error")
            }
        ) {
            override fun getParams(): Map<String, String> = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun get(
        endpoint: String,
        params: Map<String, String>,
        callback: (Boolean, String) -> Unit
    ) {
        val url = baseUrl() + endpoint
        val request = object : StringRequest(Request.Method.GET, url,
            Response.Listener { response -> callback(true, response) },
            Response.ErrorListener { error ->
                callback(false, error.message ?: "Unknown error")
            }
        ) {
            override fun getParams(): Map<String, String> = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    fun login(email: String, password: String, onResult: (Boolean, String, String?) -> Unit) {
        post("login046.php", mapOf("email" to email, "password" to password)) { success, response ->
            if (success) onResult(true, response, null)
            else onResult(false, "", response)
        }
    }

    fun register(name: String, nim: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        post("register046.php", mapOf(
            "name" to name, "nim" to nim, "email" to email, "password" to password
        )) { success, response ->
            if (success) onResult(true, response)
            else onResult(false, response)
        }
    }

    fun logout(token: String, onResult: (Boolean, String) -> Unit) {
        post("logout046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun getRooms(token: String, onResult: (Boolean, String) -> Unit) {
        post("rooms046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun saveRoom(token: String, params: Map<String, String>, onResult: (Boolean, String) -> Unit) {
        val p = params.toMutableMap()
        p["api_token"] = token
        post("save_room046.php", p) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun deleteRoom(token: String, roomId: Int, onResult: (Boolean, String) -> Unit) {
        post("delete_room046.php", mapOf("api_token" to token, "id" to roomId.toString())) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun getBookings(token: String, onResult: (Boolean, String) -> Unit) {
        post("bookings046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun createBooking(token: String, roomId: Int, tanggal: String, jamMulai: String, jamSelesai: String, keperluan: String, onResult: (Boolean, String) -> Unit) {
        post("create_booking046.php", mapOf(
            "api_token" to token,
            "room_id" to roomId.toString(),
            "tanggal" to tanggal,
            "jam_mulai" to jamMulai,
            "jam_selesai" to jamSelesai,
            "keperluan" to keperluan
        )) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun updateBooking(token: String, bookingId: Int, status: String, adminNote: String, onResult: (Boolean, String) -> Unit) {
        post("update_booking046.php", mapOf(
            "api_token" to token,
            "id" to bookingId.toString(),
            "status" to status,
            "admin_note" to adminNote
        )) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun getUsers(token: String, onResult: (Boolean, String) -> Unit) {
        post("users046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun deleteUser(token: String, userId: Int, onResult: (Boolean, String) -> Unit) {
        post("delete_user046.php", mapOf("api_token" to token, "id" to userId.toString())) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun getProfile(token: String, onResult: (Boolean, String) -> Unit) {
        post("profile046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun updateProfile(token: String, params: Map<String, String>, onResult: (Boolean, String) -> Unit) {
        val p = params.toMutableMap()
        p["api_token"] = token
        post("update_profile046.php", p) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }

    fun getReport(token: String, onResult: (Boolean, String) -> Unit) {
        post("report046.php", mapOf("api_token" to token)) { success, response ->
            if (success) onResult(true, response) else onResult(false, response)
        }
    }
}
