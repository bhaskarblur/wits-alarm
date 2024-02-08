package com.bhaskarblur.alarmapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UiUtils {

    fun getDateTime(s: String): String? {
        return try {
            val sdf: SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy , hh:mm:ss", Locale.getDefault())
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun getDate(s: String): String? {
        return try {
            val sdf: SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}