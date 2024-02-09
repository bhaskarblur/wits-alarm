package com.bhaskarblur.alarmapp.Alarms

import android.annotation.SuppressLint
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bhaskarblur.alarmapp.R
import java.util.Date

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceived", "true")

        val dbId = intent?.getLongExtra("id", 101) ?: -1
        val title = intent?.getStringExtra("name") ?: ""
        val time = intent?.getStringExtra("dateTime")?:""
        Log.d("Alarm Title", "Name : $title at $time")

        val notiNumber = getNumber()

        val serviceIntent = Intent(context, NoiseAlarmService::class.java)
        serviceIntent.putExtra("notificationId", notiNumber)
        serviceIntent.putExtra("id", dbId)
        serviceIntent.putExtra("name", title)
        serviceIntent.putExtra("dateTime", time)
        context?.let {
            context.startService(serviceIntent)
        }

    }

    fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
}