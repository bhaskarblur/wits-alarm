package com.bhaskarblur.alarmapp.alarms.workManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker

import androidx.work.WorkerParameters
import com.bhaskarblur.alarmapp.MainActivity
import com.bhaskarblur.alarmapp.R
import com.bhaskarblur.alarmapp.alarms.CancelReceiver
import com.bhaskarblur.alarmapp.alarms.service.NoiseAlarmService
import com.example.todolistinkotlin.database.AlarmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class AlarmWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private var mediaPlayer: MediaPlayer? = null
    private val GROUP_MESSAGE: String = "ALARM"
    var alarmDatabase: AlarmDatabase? = null

    companion object {
        const val ACTION_STOP_SOUND = "stop_sound"
    }
    override suspend fun doWork(): Result {
        Log.d("AlarmReceived", "true")
        val dbId = inputData.getLong("id", 1L)
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(stopSoundReceiver, IntentFilter(ACTION_STOP_SOUND))
        if (isAlarmValid(applicationContext, dbId)) {
            val title = inputData.getString("name") ?: ""
            val time = inputData.getString("dateTime") ?: ""
            Log.d("Alarm Title", "Name : $title at $time")
            val notiNumber = getNumber()
            val serviceIntent = Intent(applicationContext, NoiseAlarmService::class.java)
            toggleAlarm(applicationContext, dbId)
                serviceIntent.putExtra("notificationId", notiNumber)
                serviceIntent.putExtra("id", dbId)
                serviceIntent.putExtra("name", title)
                serviceIntent.putExtra("dateTime", time)
            withContext(Dispatchers.Default) {
                try {
                    val notification = createNotification(
                        title, time, notiNumber, dbId
                    )
                    val notificationManager =
                        applicationContext.getSystemService(NotificationManager::class.java) as NotificationManager
                    notificationManager.notify(notiNumber, notification)
                    startNoiseAlarm()
                }
                catch(e : Exception) {
                    Log.d("errorInRunning Service",
                        e.message.toString())
                    e.printStackTrace()
                }
            }

        } else {
            Log.d("alarmExpired", "true")
        }
        return Result.success()
    }

    private val stopSoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stopAlarmSound()
        }
    }
    private fun startNoiseAlarm() {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.smart_alarm)
        mediaPlayer?.let {
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.start()
        }
    }


    fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(stopSoundReceiver)
    }


    private fun createNotification(title : String, time: String, notiId : Int,
                                   dbId : Long): Notification {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        Log.d("Alarm Title Received Noti", "Name : $title at $time")
        val icon = R.drawable.ic_launcher_background

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val cancelIntent = Intent(applicationContext, CancelReceiver::class.java)

        cancelIntent.putExtra("notificationId", notiId)
        cancelIntent.putExtra("id", dbId)
        cancelIntent.putExtra("hideNotification", true)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            dbId.hashCode(), cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java) as NotificationManager
        val channel = NotificationChannel(
            notiId.toString(),
            "Alarm Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(null, null)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, notiId.toString())
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.RED)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(false)
            .setOngoing(true)
            .setDeleteIntent(cancelPendingIntent)
            .addAction(
                com.google.android.material.R.drawable.ic_clear_black_24,
                "Cancel Alarm",
                cancelPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun initiateDatabase(context: Context) {
        if (alarmDatabase == null)
            alarmDatabase = AlarmDatabase.getInstance(context)
    }


    private fun toggleAlarm(context: Context, dbId: Long) {
        initiateDatabase(context)
        context.let {
            alarmDatabase?.let {
                alarmDatabase!!.alarmsDto().toggleIsActive(id = dbId, isActive = false)
                Log.d("alarmToggled", "true")
            }
        }
    }

    private fun isAlarmValid(context: Context, alarmId: Long): Boolean {
        initiateDatabase(context)
        Log.d("AlarmActiveChecking", alarmId.toString())
        val alarmEntity = alarmDatabase?.alarmsDto()?.getAlarmById(alarmId)
        alarmEntity?.let {
            val alarm = alarmEntity.toAlarmDto()
            Log.d("AlarmIsActive", alarm.isActive.toString())
            return alarm.isActive
        }
        return false
    }

    fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()

}

