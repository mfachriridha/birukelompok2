package com.example.birukelompok2.ui.report

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.data.repository.BookingRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val message: String = ""
)

class ReportViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = BookingRepository046()
    private var currentUser: User? = null

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    fun setUser(user: User) {
        currentUser = user
    }

    fun loadReport() {
        val user = currentUser ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.getBookings(user)
            result.fold(
                onSuccess = { bookings ->
                    _state.value = _state.value.copy(
                        bookings = bookings,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal memuat laporan"
                    )
                }
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = "")
    }
}
