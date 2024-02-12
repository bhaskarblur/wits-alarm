package com.bhaskarblur.alarmapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object TimeUtil {
    val timeFormat =  SimpleDateFormat("MMM dd yyyy, HH:mm")

    val calendar : Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())

}