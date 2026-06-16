package com.example.birukelompok2.data.repository

import android.content.Context
import android.net.Uri
import com.example.birukelompok2.data.api.ApiClient046
import com.example.birukelompok2.data.model.Room

class RoomRepository046 {

    suspend fun getRooms(): Result<List<Room>> {
        return try {
            val response = ApiClient046.api.getRooms()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat ruangan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveRoom(context: Context, room: Room, photoUri: Uri?): Result<String> {
        return try {
            val photo = ApiClient046.toMultipartPhoto(photoUri, context)
            val response = ApiClient046.api.saveRoom(
                action = ApiClient046.toText(if (room.id == 0) "create" else "update"),
                id = if (room.id > 0) ApiClient046.toText(room.id.toString()) else null,
                name = ApiClient046.toText(room.name),
                location = ApiClient046.toText(room.location),
                capacity = ApiClient046.toText(room.capacity.toString()),
                facilities = ApiClient046.toText(room.facilities),
                description = ApiClient046.toText(room.description),
                status = ApiClient046.toText(room.status),
                photo = photo
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Ruangan disimpan")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menyimpan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(id: Int): Result<String> {
        return try {
            val response = ApiClient046.api.deleteRoom(mapOf("action" to "delete", "id" to id))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Ruangan dihapus")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
