package com.example.birukelompok2.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun DatePickerResult(): String {
    val cal = Calendar.getInstance()
    return String.format(
        Locale.getDefault(),
        "%04d-%02d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

fun formatDateDisplay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatTimeDisplay(time: String): String {
    return if (time.length >= 5) time.substring(0, 5) else time
}
