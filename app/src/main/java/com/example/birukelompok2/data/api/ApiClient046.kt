package com.example.birukelompok2.data.api

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import com.example.birukelompok2.data.local.TokenManager046
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient046 {
    private const val PREFS = "biru_prefs"
    private const val KEY_BASE_URL = "base_url"
    private const val DEFAULT_URL = "http://192.168.50.20/biru-kelompok2"

    var baseUrl: String = DEFAULT_URL
        private set

    lateinit var api: ApiService046
        private set
    lateinit var tokenManager: TokenManager046
        private set

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        tokenManager = TokenManager046(context)
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_BASE_URL, null)
        if (saved != null) baseUrl = saved
        buildClient()
        initialized = true
    }

    private fun buildClient() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor046(tokenManager))
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService046::class.java)
    }

    suspend fun discoverBaseUrl(context: Context): String {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_BASE_URL, null)
        val candidates = listOfNotNull(
            saved,
            DEFAULT_URL,
            "http://127.0.0.1/biru-kelompok2",
            "http://10.0.2.2/biru-kelompok2"
        ).distinct()

        val results = mutableListOf<String>()
        for (candidate in candidates) {
            try {
                val testClient = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .build()
                val testRetrofit = Retrofit.Builder()
                    .baseUrl(candidate.trimEnd('/') + "/")
                    .client(testClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val testApi = testRetrofit.create(ApiService046::class.java)
                val response = testApi.ping()
                if (response.isSuccessful && response.body()?.success == true) {
                    baseUrl = candidate.trimEnd('/')
                    buildClient()
                    saveBaseUrl(context, baseUrl)
                    return "Tersambung ke $baseUrl"
                }
                results.add("$candidate: ${response.body()?.message ?: "gagal"}")
            } catch (e: Exception) {
                results.add("$candidate: ${e.message}")
            }
        }
        return "API tidak ditemukan. ${results.joinToString(" | ")}"
    }

    fun saveBaseUrl(context: Context, url: String) {
        baseUrl = url.trim().trimEnd('/')
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(KEY_BASE_URL, baseUrl)
        }
        buildClient()
    }

    fun toMultipartPhoto(imageUri: Uri?, context: Context): MultipartBody.Part? {
        if (imageUri == null) return null
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()
            val mime = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", "photo.jpg", requestBody)
        } catch (e: Exception) {
            null
        }
    }

    fun toText(value: String): okhttp3.RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}
