package com.example.birukelompok2.ui.users

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
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.theme.*

@Composable
fun UsersScreen046(
    onMessage: (String) -> Unit,
    viewModel: UsersViewModel046 = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            Text(
                "Pengguna",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.isLoading && state.users.isEmpty()) {
            item { LoadingShimmer() }
            item { LoadingShimmer() }
        }

        if (state.users.isEmpty() && !state.isLoading) {
            item { EmptyState("Belum ada pengguna.") }
        }

        items(state.users) { user ->
            BiruCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BiruAvatar(
                        photoUrl = user.photo,
                        name = user.name,
                        size = 44.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            user.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${user.nim} · ${user.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BiruTextSecondary
                        )
                    }
                }
                BiruOutlineButton(
                    text = "Hapus pengguna",
                    onClick = { viewModel.deleteUser(user.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
