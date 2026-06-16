package com.example.birukelompok2.data.repository

import com.example.birukelompok2.data.api.ApiClient046
import com.example.birukelompok2.data.model.User

class AuthRepository046 {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = ApiClient046.api.login(
                mapOf("action" to "login", "email" to email, "password" to password)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val loginData = response.body()?.data
                if (loginData != null && loginData.user != null) {
                    ApiClient046.tokenManager.saveToken(loginData.token)
                    Result.success(loginData.user)
                } else {
                    Result.failure(Exception("Data user tidak ditemukan"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, nim: String, email: String, password: String): Result<String> {
        return try {
            val response = ApiClient046.api.register(
                mapOf("action" to "register", "name" to name, "nim" to nim, "email" to email, "password" to password)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val loginData = response.body()?.data
                if (loginData != null && loginData.token.isNotBlank()) {
                    ApiClient046.tokenManager.saveToken(loginData.token)
                }
                Result.success(response.body()?.message ?: "Register berhasil")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Register gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
