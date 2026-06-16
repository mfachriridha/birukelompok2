package com.example.birukelompok2.ui.rooms

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.repository.RoomRepository046
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoomsState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val message: String = "",
    val isFormVisible: Boolean = false,
    val editingRoom: Room? = null,
    val bookingRoom: Room? = null
)

class RoomsViewModel046(application: Application) : AndroidViewModel(application) {

    private val repo = RoomRepository046()

    private val _state = MutableStateFlow(RoomsState())
    val state: StateFlow<RoomsState> = _state.asStateFlow()

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.getRooms()
            result.fold(
                onSuccess = { rooms ->
                    _state.value = _state.value.copy(
                        rooms = rooms,
                        isLoading = false,
                        message = if (rooms.isEmpty()) "Belum ada ruangan" else ""
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal memuat ruangan"
                    )
                }
            )
        }
    }

    fun showAddForm() {
        _state.value = _state.value.copy(
            isFormVisible = true,
            editingRoom = null
        )
    }

    fun showEditForm(room: Room) {
        _state.value = _state.value.copy(
            isFormVisible = true,
            editingRoom = room
        )
    }

    fun hideForm() {
        _state.value = _state.value.copy(
            isFormVisible = false,
            editingRoom = null
        )
    }

    fun showBookingForm(room: Room) {
        _state.value = _state.value.copy(bookingRoom = room)
    }

    fun hideBookingForm() {
        _state.value = _state.value.copy(bookingRoom = null)
    }

    fun saveRoom(room: Room, photoUri: Uri?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.saveRoom(getApplication(), room, photoUri)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg,
                        isFormVisible = false,
                        editingRoom = null
                    )
                    loadRooms()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = error.message ?: "Gagal menyimpan"
                    )
                }
            )
        }
    }

    fun deleteRoom(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = "")
            val result = repo.deleteRoom(id)
            result.fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = msg
                    )
                    loadRooms()
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
