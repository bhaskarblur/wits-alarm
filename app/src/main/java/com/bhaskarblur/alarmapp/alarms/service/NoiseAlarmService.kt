package com.bhaskarblur.alarmapp.alarms.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bhaskarblur.alarmapp.MainActivity
import com.bhaskarblur.alarmapp.R
import com.bhaskarblur.alarmapp.alarms.CancelReceiver

class NoiseAlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val GROUP_MESSAGE: String = "ALARM"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val dbId = intent?.getLongExtra("id", -1) ?: -1
        Log.d("NoiseAlarmReceived", "true")
            val notiId = intent?.getIntExtra("notificationId", 101) ?: 101
            val title = intent?.getStringExtra("name") ?: ""
            val time = intent?.getStringExtra("dateTime") ?: ""
            val notification = createNotification(
                title, time, notiId, dbId
            )
//            val notificationManager =
//                getSystemService(NotificationManager::class.java) as NotificationManager
//        notificationManager.notify(notiId, notification)
            startNoiseAlarm()
            startForeground(notiId, notification)

        return START_STICKY
    }


    private fun startNoiseAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.smart_alarm)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }


    private fun createNotification(title : String, time: String, notiId : Int,
                                   dbId : Long): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        Log.d("Alarm Title Received Noti", "Name : $title at $time")
        val icon = R.drawable.ic_launcher_background

        val pendingIntent =
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val cancelIntent = Intent(this, CancelReceiver::class.java)

        cancelIntent.putExtra("notificationId", notiId)
        cancelIntent.putExtra("id", dbId)
        cancelIntent.putExtra("hideNotification", true)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            dbId.hashCode(), cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        val channel = NotificationChannel(
            notiId.toString(),
            "Alarm Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)


        return NotificationCompat.Builder(this, notiId.toString())
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.RED)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(
                com.google.android.material.R.drawable.ic_clear_black_24,
                "Cancel Alarm",
                cancelPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
