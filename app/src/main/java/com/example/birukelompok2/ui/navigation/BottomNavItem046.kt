package com.example.birukelompok2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Rooms : BottomNavItem("rooms", "Ruangan", Icons.Default.Home)
    data object Bookings : BottomNavItem("bookings", "Booking", Icons.Default.BookOnline)
    data object Users : BottomNavItem("users", "Pengguna", Icons.Default.People)
    data object Report : BottomNavItem("report", "Laporan", Icons.Default.BarChart)
    data object Profile : BottomNavItem("profile", "Profil", Icons.Default.Person)

    companion object {
        fun adminItems() = listOf(Rooms, Bookings, Users, Report, Profile)
        fun userItems() = listOf(Rooms, Bookings, Report, Profile)
    }
}
