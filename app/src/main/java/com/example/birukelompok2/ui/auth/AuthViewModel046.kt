package com.example.birukelompok2.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.data.repository.AuthRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val message: String = "",
    val messageType: MessageType = MessageType.INFO,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val connectionStatus: String = ""
)

enum class MessageType { SUCCESS, ERROR, INFO }

class AuthViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository046()
    private val prefs = application.getSharedPreferences("biru_user", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkSavedSession()
    }

    private fun checkSavedSession() {
        val json = prefs.getString("user_data", null)
        if (json != null) {
            try {
                val gson = com.google.gson.Gson()
                val user = gson.fromJson(json, User::class.java)
                _state.value = _state.value.copy(isLoggedIn = true, user = user)
            } catch (_: Exception) {
                prefs.edit().clear().apply()
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(
                message = "Email dan password wajib diisi",
                messageType = MessageType.ERROR
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.login(email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    saveUser(user)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        message = "Login berhasil! Selamat datang, ${user.name}",
                        messageType = MessageType.SUCCESS
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Login gagal",
                        messageType = MessageType.ERROR
                    )
                }
            )
        }
    }

    fun register(name: String, nim: String, email: String, password: String) {
        if (name.isBlank() || nim.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(
                message = "Semua field wajib diisi",
                messageType = MessageType.ERROR
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.register(name.trim(), nim.trim(), email.trim(), password)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg,
                        messageType = MessageType.SUCCESS
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Register gagal",
                        messageType = MessageType.ERROR
                    )
                }
            )
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _state.value = AuthState(
            message = "Berhasil keluar",
            messageType = MessageType.INFO
        )
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = "")
    }

    fun setConnectionStatus(status: String) {
        _state.value = _state.value.copy(connectionStatus = status)
    }

    private fun saveUser(user: User) {
        try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(user)
            prefs.edit().putString("user_data", json).apply()
        } catch (_: Exception) {}
    }

    fun updateUser(user: User) {
        saveUser(user)
        _state.value = _state.value.copy(user = user)
    }
}
