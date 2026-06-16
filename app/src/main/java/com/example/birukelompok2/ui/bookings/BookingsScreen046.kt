package com.example.birukelompok2.ui.bookings

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
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*

@Composable
fun BookingsScreen046(
    user: User,
    onMessage: (String) -> Unit,
    onBookingRoom: Room?,
    onBookingDismissed: () -> Unit,
    viewModel: BookingsViewModel046 = viewModel()
) {
    LaunchedEffect(user.id) {
        viewModel.setUser(user)
        viewModel.loadBookings()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message.isNotBlank()) {
            onMessage(state.message)
            viewModel.clearMessage()
        }
    }

    if (onBookingRoom != null) {
        BookingFormScreen046(
            user = user,
            room = onBookingRoom,
            onCancel = onBookingDismissed,
            onSave = { date, start, end, purpose ->
                viewModel.createBooking(onBookingRoom, date, start, end, purpose)
            }
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
                Text(
                    "Booking",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                BiruOutlineButton(text = "Refresh", onClick = { viewModel.loadBookings() })
            }
        }

        if (state.isLoading && state.bookings.isEmpty()) {
            item { LoadingShimmer() }
            item { LoadingShimmer() }
        }

        if (state.bookings.isEmpty() && !state.isLoading) {
            item { EmptyState("Belum ada booking.") }
        }

        items(state.bookings) { booking ->
            BookingCard046(
                role = user.role,
                booking = booking,
                onApprove = { viewModel.updateStatus(booking.id, "approved") },
                onReject = { note -> viewModel.updateStatus(booking.id, "rejected", note) }
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun BookingCard046(
    role: String,
    booking: Booking,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var showNoteInput by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    BiruCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                booking.roomName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            StatusChip(status = booking.status)
        }

        Text(
            "Peminjam: ${booking.userName}${if (booking.userNim.isNotBlank()) " (${booking.userNim})" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = BiruTextSecondary
        )
        Text(
            "${booking.date}, ${booking.startTime}-${booking.endTime}",
            style = MaterialTheme.typography.bodySmall,
            color = BiruTextSecondary
        )
        Text(
            "Keperluan: ${booking.purpose}",
            style = MaterialTheme.typography.bodySmall,
            color = BiruTextSecondary
        )

        if (booking.adminNote.isNotBlank()) {
            Text(
                "Catatan admin: ${booking.adminNote}",
                style = MaterialTheme.typography.bodySmall,
                color = BiruWarning
            )
        }

        if (role == "admin" && booking.status == "pending") {
            HorizontalDivider(color = BiruBorder)

            if (showNoteInput) {
                BiruTextField(note, { note = it }, "Catatan (opsional)")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BiruButton(
                    text = "Setujui",
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                )
                BiruOutlineButton(
                    text = if (showNoteInput) "Tolak" else "Tolak + catatan",
                    onClick = {
                        if (!showNoteInput) {
                            showNoteInput = true
                        } else {
                            onReject(note)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
