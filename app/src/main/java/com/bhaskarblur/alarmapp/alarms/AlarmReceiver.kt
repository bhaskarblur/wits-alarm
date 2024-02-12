package com.bhaskarblur.alarmapp.alarms

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bhaskarblur.alarmapp.alarms.service.NoiseAlarmService
import com.example.todolistinkotlin.database.AlarmDatabase
import java.util.Date

class AlarmReceiver : BroadcastReceiver() {

    var alarmDatabase: AlarmDatabase? = null
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceived", "true")
        val dbId = intent?.getLongExtra("id", 101) ?: -1
        context?.let {
            if (isAlarmValid(context, dbId)) {
                val title = intent?.getStringExtra("name") ?: ""
                val time = intent?.getStringExtra("dateTime")?:""
                Log.d("Alarm Title", "Name : $title at $time")
                val notiNumber = getNumber()
                val serviceIntent = Intent(context, NoiseAlarmService::class.java)
                toggleAlarm(context, dbId)
                serviceIntent.putExtra("notificationId", notiNumber)
                serviceIntent.putExtra("id", dbId)
                serviceIntent.putExtra("name", title)
                serviceIntent.putExtra("dateTime", time)
                context.startService(serviceIntent)
            }
            else {
                Log.d("alarmExpired", "true")
            }
        }
    }


    private fun initiateDatabase(context: Context) {
        if (alarmDatabase == null)
            alarmDatabase = AlarmDatabase.getInstance(context)
    }

    private fun toggleAlarm(context: Context, dbId : Long) {
        initiateDatabase(context)
        context.let {
            alarmDatabase?.let {
                alarmDatabase!!.alarmsDto().toggleIsActive(id = dbId, isActive = false)
                Log.d("alarmToggled", "true")
            }
        }
    }

    private fun isAlarmValid(context: Context, alarmId : Long) : Boolean {
            initiateDatabase(context)
            Log.d("AlarmActiveChecking",alarmId.toString())
            val alarmEntity = alarmDatabase?.alarmsDto()?.getAlarmById(alarmId)
            alarmEntity?.let {
                val alarm = alarmEntity.toAlarmDto()
                Log.d("AlarmIsActive",alarm.isActive.toString())
                return alarm.isActive
            }
        return false
    }
    fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
}