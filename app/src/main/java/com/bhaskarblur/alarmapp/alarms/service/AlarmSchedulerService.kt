package com.bhaskarblur.alarmapp.alarms.service;

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bhaskarblur.alarmapp.alarms.workManager.AlarmWorker
import com.bhaskarblur.alarmapp.utils.UiUtils
import java.util.concurrent.TimeUnit

class AlarmSchedulerService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val id = intent?.getLongExtra("id", 0)
        val active = intent?.getBooleanExtra("isActive", true)
        val name = intent?.getStringExtra("name")
        val scheduleTime = intent?.getLongExtra(
            "scheduleTime",
            System.currentTimeMillis()
        )
        val dateTime = intent?.getStringExtra("dateTime")
        Log.d(
            "YourService",
            "Received extras - id: $id, name: $name, scheduleTime: $scheduleTime, " +
                    "dateTime: ${UiUtils.getDateTime(dateTime.toString())},  isActive: $active"
        )
        try {
            if(name.isNullOrEmpty().not()) {
                startWorkManagerLogic(active!!, scheduleTime!!, id!!, name!!, dateTime!!)
            }
        } catch(e : Exception) {
            e.printStackTrace()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun startWorkManagerLogic(
        isActive: Boolean,
        scheduleTime: Long,
        id: Long, name: String,
        dateTime: String
    ) {
        if (isActive) {
            val inputData = Data.Builder()
                .putLong("id", id)
                .putString("name", name)
                .putString("dateTime", dateTime)
                .build()

            val backoffCriteria = BackoffPolicy.EXPONENTIAL
            val backoffDelay = 10_000L

            val alarmWorkRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
                .setInitialDelay(scheduleTime, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(backoffCriteria, backoffDelay, TimeUnit.MILLISECONDS)
                .addTag(id.toString())
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(this)
                .beginUniqueWork(
                    id.toString(),
                    ExistingWorkPolicy.APPEND,
                    alarmWorkRequest
                ).enqueue()

            Log.d("AlarmWorkEnqueued", "true")
        } else {
            WorkManager.getInstance(this).cancelAllWorkByTag(
                id.toString()
            )
            Log.d("AlarmWorkCancelled", "true")
        }
        stopSelf()
    }
}
