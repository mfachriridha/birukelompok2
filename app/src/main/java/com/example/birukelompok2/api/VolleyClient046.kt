package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleyClient046 {
    private var requestQueue: RequestQueue? = null
    private const val BASE_URL = "http://192.168.50.20/biru-kelompok2/"

    fun init(context: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
        }
    }

    fun getRequestQueue(): RequestQueue {
        return requestQueue ?: throw IllegalStateException("VolleyClient046 not initialized")
    }

    fun getBaseUrl(): String = BASE_URL
}
