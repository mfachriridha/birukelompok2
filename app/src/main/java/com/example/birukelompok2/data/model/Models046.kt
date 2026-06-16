package com.example.birukelompok2.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int = 0,
    val name: String = "",
    val nim: String = "",
    val email: String = "",
    val role: String = "pengguna",
    val photo: String = ""
)

data class Room(
    val id: Int = 0,
    val name: String = "",
    val location: String = "",
    val capacity: Int = 0,
    val facilities: String = "",
    val description: String = "",
    val status: String = "tersedia",
    val photo: String = ""
)

data class Booking(
    val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("room_id") val roomId: Int = 0,
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("user_nim") val userNim: String = "",
    @SerializedName("room_name") val roomName: String = "",
    val date: String = "",
    @SerializedName("start_time") val startTime: String = "",
    @SerializedName("end_time") val endTime: String = "",
    val purpose: String = "",
    val status: String = "pending",
    @SerializedName("admin_note") val adminNote: String = ""
)

data class ApiResponse<T>(
    val success: Boolean = false,
    val message: String = "",
    val data: T? = null
)

data class LoginRequest(
    val action: String = "login",
    val email: String,
    val password: String
)

data class RegisterRequest(
    val action: String = "register",
    val name: String,
    val nim: String,
    val email: String,
    val password: String
)

data class BookingStatusRequest(
    val action: String = "status",
    val id: Int,
    val status: String,
    @SerializedName("admin_note") val adminNote: String = ""
)

data class ActionRequest(
    val action: String,
    val id: Int = 0
)

data class LoginResponse(
    val token: String = "",
    val user: User? = null
)
