package com.example.birukelompok2.ui.bookings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.data.repository.BookingRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingsState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val message: String = "",
    val bookingRoom: Room? = null,
    val isFormVisible: Boolean = false
)

class BookingsViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = BookingRepository046()
    private var currentUser: User? = null

    private val _state = MutableStateFlow(BookingsState())
    val state: StateFlow<BookingsState> = _state.asStateFlow()

    fun setUser(user: User) {
        currentUser = user
    }

    fun loadBookings() {
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
                        message = error.message ?: "Gagal memuat booking"
                    )
                }
            )
        }
    }

    fun showBookingForm(room: Room) {
        _state.value = _state.value.copy(bookingRoom = room, isFormVisible = true)
    }

    fun hideBookingForm() {
        _state.value = _state.value.copy(bookingRoom = null, isFormVisible = false)
    }

    fun createBooking(
        room: Room, date: String, startTime: String, endTime: String, purpose: String
    ) {
        val user = currentUser ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.createBooking(user, room.id, room.name, date, startTime, endTime, purpose)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg,
                        isFormVisible = false,
                        bookingRoom = null
                    )
                    loadBookings()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal booking"
                    )
                }
            )
        }
    }

    fun updateStatus(id: Int, status: String, note: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.updateStatus(id, status, note)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg
                    )
                    loadBookings()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal update"
                    )
                }
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = "")
    }
}
