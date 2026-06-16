package com.example.birukelompok2.ui.report

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*
import com.example.birukelompok2.util.PdfReport046
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportScreen046(
    user: User,
    onMessage: (String) -> Unit,
    viewModel: ReportViewModel046 = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val savePdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openOutputStream(uri)?.use {
                            PdfReport046.write(it, user, state.bookings)
                        }
                        true
                    } catch (_: Exception) {
                        false
                    }
                }
                onMessage(if (ok) "PDF berhasil disimpan" else "Gagal menyimpan PDF")
            }
        }
    }

    LaunchedEffect(user.id) {
        viewModel.setUser(user)
        viewModel.loadReport()
    }

    LaunchedEffect(state.message) {
        if (state.message.isNotBlank()) {
            onMessage(state.message)
            viewModel.clearMessage()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BiruCard {
                Text(
                    "Laporan Booking",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Total data: ${state.bookings.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BiruTextSecondary
                )
                Text(
                    "Refresh data lalu simpan sebagai PDF.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BiruTextTertiary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BiruButton(
                        text = "Refresh",
                        onClick = { viewModel.loadReport() },
                        modifier = Modifier.weight(1f)
                    )
                    BiruOutlineButton(
                        text = "Simpan PDF",
                        onClick = {
                            val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            savePdf.launch("laporan_biru_$stamp.pdf")
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.bookings.isNotEmpty()
                    )
                }
            }
        }

        if (state.isLoading && state.bookings.isEmpty()) {
            item { LoadingShimmer() }
            item { LoadingShimmer() }
        }

        if (state.bookings.isEmpty() && !state.isLoading) {
            item { EmptyState("Belum ada data laporan.") }
        }

        items(state.bookings) { booking ->
            BiruCard {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        booking.roomName,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = booking.status)
                }
                Text(
                    "Peminjam: ${booking.userName} (${booking.userNim})",
                    style = MaterialTheme.typography.bodySmall,
                    color = BiruTextSecondary
                )
                Text(
                    "${booking.date}, ${booking.startTime}-${booking.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BiruTextSecondary
                )
                Text(
                    booking.purpose,
                    style = MaterialTheme.typography.bodySmall,
                    color = BiruTextSecondary
                )
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
