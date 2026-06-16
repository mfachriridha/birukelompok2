package com.example.birukelompok2.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.data.repository.UserRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val message: String = "",
    val updatedUser: User? = null
)

class ProfileViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository046()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun updateProfile(
        user: User, name: String, nim: String, email: String,
        password: String, photoUri: Uri?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.updateProfile(
                getApplication(), user, name,
                if (user.role == "admin") user.nim else nim,
                email, password, photoUri
            )
            result.fold(
                onSuccess = { updated ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = "Profil berhasil diperbarui",
                        updatedUser = updated
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal update profil"
                    )
                }
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = "")
    }

    fun clearUpdatedUser() {
        _state.value = _state.value.copy(updatedUser = null)
    }
}
