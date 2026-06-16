package com.example.birukelompok2.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

object VolleyClient046 {
    private var requestQueue: RequestQueue? = null
    private var _baseUrl: String = ""

    val baseUrl: String
        get() = _baseUrl

    fun init(context: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
            _baseUrl = discoverBaseUrl()
        }
    }

    fun getRequestQueue(): RequestQueue {
        return requestQueue ?: throw IllegalStateException("VolleyClient046 not initialized")
    }

    fun getBaseUrl(): String = _baseUrl

    private fun discoverBaseUrl(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        val ip = address.hostAddress
                        if (ip != null && ip.startsWith("192.168.")) {
                            return "http://$ip/biru-kelompok2/"
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
        return "http://10.0.2.2/biru-kelompok2/"
    }

    fun discoverBaseUrlSync(): String {
        _baseUrl = discoverBaseUrl()
        return _baseUrl
    }
}
