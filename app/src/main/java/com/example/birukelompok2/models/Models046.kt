package com.example.birukelompok2.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: com.google.gson.JsonElement? = null
)

data class User(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("nim") val nim: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("role") val role: String = "user",
    @SerializedName("foto_url") val fotoUrl: String = "",
    @SerializedName("api_token") val apiToken: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

data class Room(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nama") val nama: String = "",
    @SerializedName("lokasi") val lokasi: String = "",
    @SerializedName("kapasitas") val kapasitas: Int = 0,
    @SerializedName("fasilitas") val fasilitas: String = "",
    @SerializedName("deskripsi") val deskripsi: String = "",
    @SerializedName("foto_url") val fotoUrl: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

data class Booking(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("room_id") val roomId: Int = 0,
    @SerializedName("room_name") val roomName: String = "",
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("tanggal") val tanggal: String = "",
    @SerializedName("jam_mulai") val jamMulai: String = "",
    @SerializedName("jam_selesai") val jamSelesai: String = "",
    @SerializedName("keperluan") val keperluan: String = "",
    @SerializedName("status") val status: String = "pending",
    @SerializedName("admin_note") val adminNote: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)
