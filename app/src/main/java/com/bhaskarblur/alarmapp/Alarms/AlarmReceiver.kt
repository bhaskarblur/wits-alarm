package com.bhaskarblur.alarmapp.Alarms

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bhaskarblur.alarmapp.R
import java.util.Date

class AlarmReceiver : BroadcastReceiver() {

    private val GROUP_MESSAGE: String = "ALARM"

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceived", "true")

        val notificationManager: NotificationManager =
            context?.getSystemService(NotificationManager::class.java) as NotificationManager
        val dbId = intent?.getLongExtra("id", -1) ?: -1
        val title = intent?.getStringExtra("name") ?: ""
        val time = intent?.getStringExtra("dateTime")?:""
        Log.d("Alarm Title", "Name : $title at $time")
        val icon = R.drawable.ic_launcher_background

        val notiNumber = getNumber()
        val cancelIntent = Intent(context, CancelReceiver::class.java)

        cancelIntent.putExtra("notificationId", notiNumber)
        cancelIntent.putExtra("id", dbId)
        cancelIntent.putExtra("hideNotification", true)
        val cancelPendingIntent = PendingIntent.getBroadcast(context,
            dbId.hashCode(), cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationChannel = NotificationChannel("Alarm Reminder", "Alarm ringing!",
            NotificationManager.IMPORTANCE_MAX)

        notificationChannel.description = "Alarm is ringing"
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(context, "Alarm Reminder")
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(Color.RED)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(true)
            .addAction(com.google.android.material.R.drawable.ic_clear_black_24, "Cancel Alarm", cancelPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        notificationManager.notify(notiNumber, notification)

        val toggleAlarmIntent = Intent(context, CancelReceiver::class.java)

        toggleAlarmIntent.putExtra("notificationId", notiNumber)
        toggleAlarmIntent.putExtra("id", dbId)
        toggleAlarmIntent.putExtra("hideNotification", false)
        val toggleAlarmPendingIntent = PendingIntent.getBroadcast(context,
            dbId.hashCode() + 1, toggleAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        toggleAlarmPendingIntent.send()

    }


    fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
}