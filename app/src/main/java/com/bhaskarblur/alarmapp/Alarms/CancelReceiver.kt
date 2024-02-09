package com.bhaskarblur.alarmapp.Alarms

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.todolistinkotlin.database.AlarmDatabase


class CancelReceiver : BroadcastReceiver() {
    var alarmDatabase: AlarmDatabase? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CancelReceived", "true")
        val notiId = intent?.getIntExtra("notificationId", -1) ?: -1
        val dbId = intent?.getLongExtra("id", -1) ?: -1
        val hideNotification = intent?.getBooleanExtra("hideNotification", false)

        context?.let {
            val stopServiceIntent = Intent(context, NoiseAlarmService::class.java)
            context.stopService(stopServiceIntent)
            val broadcastIntent = Intent("com.your.package.ACTION_CANCEL_ALARM")
            broadcastIntent.putExtra("alarmId", dbId)
            context.sendBroadcast(broadcastIntent)
        }
        cancelAlarm(context, dbId)
        if(hideNotification == true) {
            cancelNotification(context, notiId)
        }
    }

    private fun cancelAlarm(context: Context?, dbId : Long) {
        context?.let {
            initiateDatabase(context)
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
        context.let {
            alarmDatabase?.alarmsDto()?.toggleIsActive(id = dbId, isActive = false)
        }

    }

    private fun initiateDatabase(context: Context) {
        if (alarmDatabase == null)
            alarmDatabase = AlarmDatabase.getInstance(context)
    }
    private fun cancelNotification(context: Context?, id: Int) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id)
    }
}