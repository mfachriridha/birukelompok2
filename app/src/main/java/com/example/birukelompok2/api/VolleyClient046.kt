package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleyClient046 {
    private var requestQueue: RequestQueue? = null
    private var _baseUrl: String = ""

    fun init(context: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
        }
    }

    fun setServerIp(ip: String) {
        _baseUrl = "http://$ip/biru-kelompok2/"
    }

    fun getRequestQueue(): RequestQueue {
        return requestQueue ?: throw IllegalStateException("VolleyClient046 not initialized")
    }

    fun getBaseUrl(): String = _baseUrl
}
