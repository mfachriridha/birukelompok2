package com.example.birukelompok2

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birukelompok2.data.api.ApiClient046
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.ui.auth.AuthScreen046
import com.example.birukelompok2.ui.auth.AuthViewModel046
import com.example.birukelompok2.ui.bookings.BookingsScreen046
import com.example.birukelompok2.ui.components.*
import com.example.birukelompok2.ui.navigation.BottomNavItem
import com.example.birukelompok2.ui.profile.ProfileScreen046
import com.example.birukelompok2.ui.report.ReportScreen046
import com.example.birukelompok2.ui.rooms.RoomsScreen046
import com.example.birukelompok2.ui.theme.*
import com.example.birukelompok2.ui.users.UsersScreen046

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiruApp046() {
    val context = LocalContext.current
    val authViewModel: AuthViewModel046 = viewModel()

    var message by remember { mutableStateOf("") }
    var bookingRoom by remember { mutableStateOf<Room?>(null) }
    var currentTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Rooms) }

    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        ApiClient046.init(context)
        if (ApiClient046.baseUrl == "http://192.168.50.20/biru-kelompok2") {
            val status = ApiClient046.discoverBaseUrl(context)
            if (!status.contains("Tersambung")) {
                message = status
            }
        }
    }

    val user = authState.user
    val tabs = if (user?.role == "admin") BottomNavItem.adminItems() else BottomNavItem.userItems()

    Surface(
        color = BiruBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        if (user == null) {
            AuthScreen046(
                onLoggedIn = { }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                BiruAvatar(
                                    photoUrl = user.photo,
                                    name = user.name,
                                    size = 36.dp
                                )
                                Column {
                                    Text(
                                        "Biru",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        "${user.name} · ${user.role}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                TextButton(
                                    onClick = {
                                        authViewModel.logout()
                                        currentTab = BottomNavItem.Rooms
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = BiruError
                                    )
                                ) {
                                    Text(
                                        "Keluar",
                                        fontWeight = FontWeight.SemiBold,
                                        color = BiruError
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BiruPrimary
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = BiruSurface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        tabs.forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        item.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                selected = currentTab == item,
                                onClick = {
                                    currentTab = item
                                    message = ""
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BiruPrimary,
                                    selectedTextColor = BiruPrimary,
                                    unselectedIconColor = BiruTextTertiary,
                                    unselectedTextColor = BiruTextTertiary,
                                    indicatorColor = BiruPrimaryLight.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (message.isNotBlank()) {
                        MessageBar(
                            message = message,
                            type = com.example.birukelompok2.ui.components.MessageType.INFO,
                            onDismiss = { message = "" }
                        )
                    }

                    when (currentTab) {
                        BottomNavItem.Rooms -> {
                            RoomsScreen046(
                                user = user,
                                onMessage = { message = it },
                                onBookingRequested = { room ->
                                    bookingRoom = room
                                    currentTab = BottomNavItem.Bookings
                                }
                            )
                        }
                        BottomNavItem.Bookings -> {
                            BookingsScreen046(
                                user = user,
                                onMessage = { message = it },
                                onBookingRoom = bookingRoom,
                                onBookingDismissed = { bookingRoom = null }
                            )
                        }
                        BottomNavItem.Users -> {
                            UsersScreen046(
                                onMessage = { message = it }
                            )
                        }
                        BottomNavItem.Report -> {
                            ReportScreen046(
                                user = user,
                                onMessage = { message = it }
                            )
                        }
                        BottomNavItem.Profile -> {
                            ProfileScreen046(
                                user = user,
                                onUserUpdated = { authViewModel.updateUser(it) },
                                onMessage = { message = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
