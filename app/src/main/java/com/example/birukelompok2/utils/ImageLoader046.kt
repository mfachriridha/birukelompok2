package com.example.birukelompok2.utils

import android.graphics.BitmapFactory
import android.widget.ImageView
import java.net.URL

fun loadImageFromUrl(imageUrl: String, imageView: ImageView) {
    Thread {
        try {
            val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
            if (bitmap != null) {
                (imageView.context as? android.app.Activity)?.runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            }
        } catch (_: Exception) {
        }
    }.start()
}
