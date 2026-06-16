package com.example.birukelompok2.data.api

import com.example.birukelompok2.data.local.TokenManager046
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor046(
    private val tokenManager: TokenManager046
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
