package com.example.birukelompok2.data.local

import android.content.Context
import androidx.core.content.edit

class TokenManager046(context: Context) {

    private val prefs = context.getSharedPreferences("biru_token", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString(TOKEN_KEY, token) }
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit { remove(TOKEN_KEY) }
    }

    fun hasToken(): Boolean = getToken() != null

    companion object {
        private const val TOKEN_KEY = "jwt_token"
    }
}
