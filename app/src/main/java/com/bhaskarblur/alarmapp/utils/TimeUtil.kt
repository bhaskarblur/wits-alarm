package com.bhaskarblur.alarmapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtil {
    val timeFormat =  SimpleDateFormat("MMM dd yyyy, HH:mm")

    fun getCurrentTimeAndCompareWithAlarmTime(alarmTime12Hr: LocalTime): Boolean {
        // Get the current local date and time
        val currentDateTime = LocalDateTime.now()

        // Parse the input alarm time in 12-hour format
        val formatter12Hr = DateTimeFormatter.ofPattern("hh:mm a")
        val alarmLocalTime = LocalTime.parse(alarmTime12Hr.format(
            DateTimeFormatter.ofPattern("hh:mm a")), formatter12Hr)

        // Format the current time in 12-hour format
        val currentLocalTime = currentDateTime.toLocalTime()
        val formattedCurrentTime12Hr = currentDateTime.format(formatter12Hr)

        // Compare the times
        val isAfter = currentLocalTime.isAfter(alarmLocalTime)

        // Print results
        println("Current Time (12-hour format): $formattedCurrentTime12Hr")
        println("Alarm Time (12-hour format): $alarmLocalTime")
        println("Is Current Time after Alarm Time? $isAfter")

        return isAfter
    }
}