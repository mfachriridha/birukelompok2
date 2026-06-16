package com.example.birukelompok2.ui.rooms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*
import java.util.Calendar

@Composable
fun RoomFormScreen046(
    initial: Room?,
    onCancel: () -> Unit,
    onSave: (Room, Uri?) -> Unit
) {
    val context = LocalContext.current
    var name by remember(initial?.id) { mutableStateOf(initial?.name.orEmpty()) }
    var location by remember(initial?.id) { mutableStateOf(initial?.location.orEmpty()) }
    var capacity by remember(initial?.id) { mutableStateOf(initial?.capacity?.takeIf { it > 0 }?.toString().orEmpty()) }
    var facilities by remember(initial?.id) { mutableStateOf(initial?.facilities.orEmpty()) }
    var description by remember(initial?.id) { mutableStateOf(initial?.description.orEmpty()) }
    var status by remember(initial?.id) { mutableStateOf(initial?.status ?: "tersedia") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        photoUri = uri
    }

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
                text = if (initial?.id == 0 || initial?.id == null) "Tambah Ruangan" else "Edit Ruangan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCancel) {
                Text("Batal", color = BiruTextSecondary)
            }
        }

        BiruCard {
            if (photoUri != null || initial?.photo?.isNotBlank() == true) {
                BiruRemoteImage(
                    url = photoUri?.toString() ?: initial?.photo.orEmpty(),
                    description = "Foto ruangan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            OutlinedButton(
                onClick = { picker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeButton
            ) {
                Text("Pilih Foto Ruangan")
            }

            BiruTextField(name, { name = it }, "Nama ruangan")
            BiruTextField(location, { location = it }, "Lokasi")
            BiruTextField(capacity, { capacity = it.filter(Char::isDigit) }, "Kapasitas (orang)")

            OutlinedTextField(
                value = facilities,
                onValueChange = { facilities = it },
                label = { Text("Fasilitas") },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BiruPrimary,
                    unfocusedBorderColor = BiruBorder
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = ShapeTextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BiruPrimary,
                    unfocusedBorderColor = BiruBorder
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusChip("tersedia")
                Switch(
                    checked = status == "tersedia",
                    onCheckedChange = { status = if (it) "tersedia" else "maintenance" },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BiruSurface,
                        checkedTrackColor = BiruSuccess,
                        uncheckedThumbColor = BiruSurface,
                        uncheckedTrackColor = BiruError
                    )
                )
                StatusChip("maintenance")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BiruButton(
                    text = "Simpan",
                    onClick = {
                        onSave(
                            Room(
                                id = initial?.id ?: 0,
                                name = name,
                                location = location,
                                capacity = capacity.toIntOrNull() ?: 0,
                                facilities = facilities,
                                description = description,
                                status = status,
                                photo = initial?.photo ?: ""
                            ),
                            photoUri
                        )
                    },
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
