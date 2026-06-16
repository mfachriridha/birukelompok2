package com.example.birukelompok2.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*

@Composable
fun RoomsScreen046(
    user: User,
    onMessage: (String) -> Unit,
    onBookingRequested: (Room) -> Unit,
    viewModel: RoomsViewModel046 = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message.isNotBlank()) {
            onMessage(state.message)
            viewModel.clearMessage()
        }
    }

    if (state.isFormVisible) {
        RoomFormScreen046(
            initial = state.editingRoom,
            onCancel = { viewModel.hideForm() },
            onSave = { room, photo -> viewModel.saveRoom(room, photo) }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Ruangan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${state.rooms.size} ruang terdaftar",
                        style = MaterialTheme.typography.bodySmall,
                        color = BiruTextSecondary
                    )
                }
                if (user.role == "admin") {
                    BiruButton(
                        text = "+ Tambah",
                        onClick = { viewModel.showAddForm() }
                    )
                }
            }
        }

        if (state.isLoading && state.rooms.isEmpty()) {
            item { LoadingShimmer() }
            item { LoadingShimmer() }
        }

        if (state.rooms.isEmpty() && !state.isLoading) {
            item { EmptyState("Belum ada ruangan.") }
        }

        items(state.rooms) { room ->
            RoomCard046(
                room = room,
                isAdmin = user.role == "admin",
                onEdit = { viewModel.showEditForm(room) },
                onDelete = { viewModel.deleteRoom(room.id) },
                onBook = { onBookingRequested(room) }
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun RoomCard046(
    room: Room,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBook: () -> Unit
) {
    BiruCard {
        if (room.photo.isNotBlank()) {
            BiruRemoteImage(
                url = room.photo,
                description = room.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    room.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${room.location} · ${room.capacity} orang",
                    style = MaterialTheme.typography.bodySmall,
                    color = BiruTextSecondary
                )
            }
            StatusChip(status = room.status)
        }

        if (room.facilities.isNotBlank()) {
            Text(
                room.facilities,
                style = MaterialTheme.typography.bodySmall,
                color = BiruTextSecondary
            )
        }

        if (isAdmin) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BiruButton(
                    text = "Edit",
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                )
                BiruOutlineButton(
                    text = "Hapus",
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            BiruButton(
                text = "Pinjam",
                onClick = onBook,
                modifier = Modifier.fillMaxWidth(),
                enabled = room.status == "tersedia"
            )
        }
    }
}
