package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleyClient046 {
    private const val BASE_URL = "http://192.168.50.20/biru-kelompok2/"
    private var queue: RequestQueue? = null

    fun init(ctx: Context) {
        if (queue == null) queue = Volley.newRequestQueue(ctx)
    }

    fun getBaseUrl(): String = BASE_URL

    fun getRequestQueue(): RequestQueue = queue ?: error("VolleyClient046 not initialized")
}
