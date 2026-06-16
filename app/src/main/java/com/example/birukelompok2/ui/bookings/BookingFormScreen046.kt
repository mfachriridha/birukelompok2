package com.example.birukelompok2.ui.bookings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*
import java.util.Calendar

@Composable
fun BookingFormScreen046(
    user: User,
    room: Room,
    onCancel: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ajukan Peminjaman",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCancel) {
                Text("Batal", color = BiruTextSecondary)
            }
        }

        BiruCard {
            if (room.photo.isNotBlank()) {
                BiruRemoteImage(
                    url = room.photo,
                    description = "Foto ${room.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }

            Text(
                text = room.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${room.location} · Kapasitas ${room.capacity} orang",
                style = MaterialTheme.typography.bodyMedium,
                color = BiruTextSecondary
            )

            HorizontalDivider(color = BiruBorder)

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            date = "%04d-%02d-%02d".format(y, m + 1, d)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeButton
            ) {
                Text(
                    if (date.isBlank()) "Pilih tanggal" else "Tanggal: $date",
                    color = if (date.isBlank()) BiruTextTertiary else BiruTextPrimary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, h, m -> startTime = "%02d:%02d".format(h, m) },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = ShapeButton
                ) {
                    Text(
                        if (startTime.isBlank()) "Jam mulai" else startTime,
                        color = if (startTime.isBlank()) BiruTextTertiary else BiruTextPrimary
                    )
                }

                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, h, m -> endTime = "%02d:%02d".format(h, m) },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = ShapeButton
                ) {
                    Text(
                        if (endTime.isBlank()) "Jam selesai" else endTime,
                        color = if (endTime.isBlank()) BiruTextTertiary else BiruTextPrimary
                    )
                }
            }

            BiruTextField(purpose, { purpose = it }, "Keperluan")

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BiruButton(
                    text = "Ajukan",
                    onClick = { onSave(date, startTime, endTime, purpose) },
                    modifier = Modifier.weight(1f)
                )
                BiruOutlineButton(
                    text = "Batal",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
