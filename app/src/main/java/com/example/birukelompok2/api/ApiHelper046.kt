package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.util.HashMap

object ApiHelper046 {

    fun init(context: Context) {
        VolleyClient046.init(context)
    }

    private fun baseUrl() = VolleyClient046.getBaseUrl()

    private fun post(
        endpoint: String,
        params: Map<String, String>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = baseUrl() + endpoint
        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response -> onSuccess(response) },
            Response.ErrorListener { error ->
                val msg = error.message ?: "Unknown error"
                onError(msg)
            }
        ) {
            override fun getParams(): Map<String, String> = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun get(
        endpoint: String,
        params: Map<String, String>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = baseUrl() + endpoint
        val request = object : StringRequest(Method.GET, url,
            Response.Listener { response -> onSuccess(response) },
            Response.ErrorListener { error ->
                val msg = error.message ?: "Unknown error"
                onError(msg)
            }
        ) {
            override fun getParams(): Map<String, String> = params
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    fun login(email: String, password: String, onResult: (Boolean, String, String?) -> Unit) {
        post("login046.php", mapOf("email" to email, "password" to password)) { response ->
            onResult(true, response, null)
        } { error ->
            onResult(false, "", error)
        }
    }

    fun register(name: String, nim: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        post("register046.php", mapOf(
            "name" to name, "nim" to nim, "email" to email, "password" to password
        )) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun logout(token: String, onResult: (Boolean, String) -> Unit) {
        post("logout046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun getRooms(token: String, onResult: (Boolean, String) -> Unit) {
        post("rooms046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun saveRoom(token: String, params: Map<String, String>, onResult: (Boolean, String) -> Unit) {
        val p = params.toMutableMap()
        p["api_token"] = token
        post("save_room046.php", p) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun deleteRoom(token: String, roomId: Int, onResult: (Boolean, String) -> Unit) {
        post("delete_room046.php", mapOf("api_token" to token, "id" to roomId.toString())) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun getBookings(token: String, onResult: (Boolean, String) -> Unit) {
        post("bookings046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
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
        )) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun updateBooking(token: String, bookingId: Int, status: String, adminNote: String, onResult: (Boolean, String) -> Unit) {
        post("update_booking046.php", mapOf(
            "api_token" to token,
            "id" to bookingId.toString(),
            "status" to status,
            "admin_note" to adminNote
        )) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun getUsers(token: String, onResult: (Boolean, String) -> Unit) {
        post("users046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun deleteUser(token: String, userId: Int, onResult: (Boolean, String) -> Unit) {
        post("delete_user046.php", mapOf("api_token" to token, "id" to userId.toString())) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun getProfile(token: String, onResult: (Boolean, String) -> Unit) {
        post("profile046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun updateProfile(token: String, params: Map<String, String>, onResult: (Boolean, String) -> Unit) {
        val p = params.toMutableMap()
        p["api_token"] = token
        post("update_profile046.php", p) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }

    fun getReport(token: String, onResult: (Boolean, String) -> Unit) {
        post("report046.php", mapOf("api_token" to token)) { response ->
            onResult(true, response)
        } { error ->
            onResult(false, error)
        }
    }
}
