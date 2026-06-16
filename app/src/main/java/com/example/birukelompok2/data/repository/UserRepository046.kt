package com.example.birukelompok2.data.repository

import android.content.Context
import android.net.Uri
import com.example.birukelompok2.data.api.ApiClient046
import com.example.birukelompok2.data.model.User

class UserRepository046 {

    suspend fun getUsers(): Result<List<User>> {
        return try {
            val response = ApiClient046.api.getUsers()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Int): Result<String> {
        return try {
            val response = ApiClient046.api.deleteUser(mapOf("action" to "delete", "id" to id))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Pengguna dihapus")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        context: Context, user: User, name: String, nim: String,
        email: String, password: String, photoUri: Uri?
    ): Result<User> {
        return try {
            val photo = ApiClient046.toMultipartPhoto(photoUri, context)
            val response = ApiClient046.api.updateProfile(
                id = ApiClient046.toText(user.id.toString()),
                name = ApiClient046.toText(name),
                nim = ApiClient046.toText(nim),
                email = ApiClient046.toText(email),
                password = if (password.isNotBlank()) ApiClient046.toText(password) else null,
                photo = photo
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val updated = response.body()?.data
                if (updated != null) Result.success(updated)
                else Result.failure(Exception("Data user kosong"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal update profil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
