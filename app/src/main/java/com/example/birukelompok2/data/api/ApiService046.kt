package com.example.birukelompok2.data.api

import com.example.birukelompok2.data.model.ApiResponse
import com.example.birukelompok2.data.model.Booking
import com.example.birukelompok2.data.model.LoginResponse
import com.example.birukelompok2.data.model.Room
import com.example.birukelompok2.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService046 {
    @POST("auth046.php")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("auth046.php")
    suspend fun register(@Body body: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @GET("ping046.php")
    suspend fun ping(): Response<ApiResponse<Any>>

    @GET("rooms046.php")
    suspend fun getRooms(): Response<ApiResponse<List<Room>>>

    @Multipart
    @POST("rooms046.php")
    suspend fun saveRoom(
        @Part("action") action: RequestBody,
        @Part("id") id: RequestBody?,
        @Part("name") name: RequestBody,
        @Part("location") location: RequestBody,
        @Part("capacity") capacity: RequestBody,
        @Part("facilities") facilities: RequestBody,
        @Part("description") description: RequestBody,
        @Part("status") status: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ApiResponse<Any>>

    @POST("rooms046.php")
    suspend fun deleteRoom(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @GET("bookings046.php")
    suspend fun getBookings(
        @Query("role") role: String,
        @Query("user_id") userId: Int? = null
    ): Response<ApiResponse<List<Booking>>>

    @POST("bookings046.php")
    suspend fun createBooking(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @POST("bookings046.php")
    suspend fun updateBookingStatus(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @GET("users046.php")
    suspend fun getUsers(): Response<ApiResponse<List<User>>>

    @POST("users046.php")
    suspend fun deleteUser(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Any>>

    @Multipart
    @POST("profile046.php")
    suspend fun updateProfile(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("nim") nim: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Response<ApiResponse<User>>

    @GET("report046.php")
    suspend fun getReport(
        @Query("role") role: String,
        @Query("user_id") userId: Int? = null
    ): Response<ApiResponse<List<Booking>>>
}
