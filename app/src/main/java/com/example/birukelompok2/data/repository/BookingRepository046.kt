package com.example.birukelompok2.data.repository

import com.example.birukelompok2.data.api.ApiClient046
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.User

class BookingRepository046 {

    suspend fun getBookings(user: User): Result<List<Booking>> {
        return try {
            val role = user.role
            val userId = if (role == "admin") null else user.id
            val response = ApiClient046.api.getBookings(role, userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat booking"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBooking(
        user: User, roomId: Int, roomName: String,
        date: String, startTime: String, endTime: String, purpose: String
    ): Result<String> {
        return try {
            val body = mapOf<String, Any>(
                "action" to "create",
                "user_id" to user.id,
                "room_id" to roomId,
                "user_name" to user.name,
                "user_nim" to user.nim,
                "room_name" to roomName,
                "date" to date,
                "start_time" to startTime,
                "end_time" to endTime,
                "purpose" to purpose
            )
            val response = ApiClient046.api.createBooking(body)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Booking berhasil")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal booking"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: Int, status: String, note: String): Result<String> {
        return try {
            val body = mapOf<String, Any>(
                "action" to "status",
                "id" to id,
                "status" to status,
                "admin_note" to note
            )
            val response = ApiClient046.api.updateBookingStatus(body)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Status diperbarui")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal update status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
