package com.example.birukelompok2.ui.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.data.repository.UserRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsersState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val message: String = ""
)

class UsersViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository046()

    private val _state = MutableStateFlow(UsersState())
    val state: StateFlow<UsersState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.getUsers()
            result.fold(
                onSuccess = { users ->
                    _state.value = _state.value.copy(
                        users = users.filter { it.role == "pengguna" },
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal memuat pengguna"
                    )
                }
            )
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.deleteUser(id)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg
                    )
                    loadUsers()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal menghapus"
                    )
                }
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = "")
    }
}
