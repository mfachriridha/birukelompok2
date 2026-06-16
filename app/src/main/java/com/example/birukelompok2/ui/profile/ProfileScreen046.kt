package com.example.birukelompok2.ui.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birukelompok2.data.model.User
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*

@Composable
fun ProfileScreen046(
    user: User,
    onUserUpdated: (User) -> Unit,
    onMessage: (String) -> Unit,
    viewModel: ProfileViewModel046 = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var name by remember(user.id) { mutableStateOf(user.name) }
    var nim by remember(user.id) { mutableStateOf(user.nim) }
    var email by remember(user.id) { mutableStateOf(user.email) }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        photoUri = uri
    }

    LaunchedEffect(state.message) {
        if (state.message.isNotBlank()) {
            onMessage(state.message)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.updatedUser) {
        state.updatedUser?.let {
            photoUri = null
            onUserUpdated(it)
            viewModel.clearUpdatedUser()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Profil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        BiruCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BiruAvatar(
                    photoUrl = photoUri?.toString() ?: user.photo,
                    name = user.name,
                    size = 72.dp
                )
                Column {
                    Text(
                        user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        user.role.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = BiruPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    BiruOutlineButton(
                        text = "Ganti Foto",
                        onClick = { picker.launch("image/*") }
                    )
                }
            }
        }

        BiruCard {
            BiruTextField(name, { name = it }, "Nama lengkap")
            if (user.role == "pengguna") {
                BiruTextField(nim, { nim = it }, "NIM")
            }
            BiruTextField(email, { email = it }, "Email")

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password baru (kosongkan jika tidak ganti)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = ShapeTextField,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BiruPrimary,
                    unfocusedBorderColor = BiruBorder,
                    cursorColor = BiruPrimary
                )
            )

            BiruButton(
                text = "Simpan Profil",
                onClick = {
                    viewModel.updateProfile(user, name, nim, email, password, photoUri)
                },
                modifier = Modifier.fillMaxWidth(),
                loading = state.isLoading
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
