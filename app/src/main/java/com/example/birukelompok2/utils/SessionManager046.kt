package com.example.birukelompok2.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.birukelompok2.models.User
import com.google.gson.Gson

class SessionManager046(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "biru_session"
        private const val KEY_TOKEN = "api_token"
        private const val KEY_USER = "user_data"
        private const val KEY_LOGGED_IN = "is_logged_in"
        private const val KEY_SERVER_IP = "server_ip"
    }

    fun saveSession(token: String, user: User) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER, gson.toJson(user))
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (_: Exception) {
            null
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getRole(): String = getUser()?.role ?: "user"

    fun isAdmin(): Boolean = getRole() == "admin"

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun saveServerIp(ip: String) {
        prefs.edit().putString(KEY_SERVER_IP, ip).apply()
    }

    fun getServerIp(): String? = prefs.getString(KEY_SERVER_IP, null)
}
