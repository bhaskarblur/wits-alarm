package com.bhaskarblur.alarmapp.presentation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.alarmapp.alarms.AlarmReceiver
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.domain.usecases.AlarmUseCase
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.AlarmsState
import com.bhaskarblur.alarmapp.utils.TimeUtil
import com.bhaskarblur.alarmapp.utils.UiUtils
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel
@Inject constructor(
    private val alarmUseCase: AlarmUseCase,
    private val context: Context
) : ViewModel() {

    private val _alarmsList = mutableStateOf(AlarmsState())
    val alarmsList get() = _alarmsList

    val eventFlow = MutableSharedFlow<UIEvents>()

    fun toggleAlarm(id : Long, isActive : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmUseCase.toggleAlarm(id, isActive).collectLatest { result ->
                Log.d("togglingAlarmFromVM", "")
                when(result.data) {
                    true -> {
                        val alarm = _alarmsList.value.alarms.find {
                            it.id == id
                        }
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.find {
                                it.id == id
                            }?.isActive = isActive
                        }
                        alarm?.let {
                            setAlarm(
                                isActive = isActive,
                                id = alarm.id, time = alarm.time,
                                name = alarm.name)
                        }

                    }
                    else -> {
                        // generate an error here
                        emitUiEvent(UIEvents.ShowError(
                            message = result.message.toString()
                        ))
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.find {
                                it.id == id
                            }?.isActive = !isActive
                        }

                    }
                }
            }
        }
    }

    fun changeAlarmTime(id : Long, time : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmUseCase.changeTime(id, time).collectLatest { result ->
                Log.d("changingTimeFromVM", result.data.toString())
                when(result.data) {
                    true -> {
                        val alarm = _alarmsList.value.alarms.find {
                            it.id == id
                        }
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.find {
                                it.id == id
                            }?.time = time
                        }
                        alarm?.let {
                            // Cancel old alarm time
                            setAlarm(
                                isActive = false,
                                id = alarm.id, time = alarm.time,
                                name = alarm.name)

                            // Set new alarm time
                            setAlarm(
                                isActive = true,
                                id = alarm.id, time = time,
                                name = alarm.name)
                        }

                    }
                    else -> {
                        // generate an error here
                        emitUiEvent(UIEvents.ShowError(
                            message = result.message.toString()
                        ))
                    }
                }
            }
        }
    }
    fun emitUiEvent(event: UIEvents) {
        viewModelScope.launch(Dispatchers.IO) {
            eventFlow.emit(event)
        }
    }
    fun createAlarm(alarm : AlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmUseCase.createAlarm(alarm).collectLatest { result ->
                Log.d("creatingAlarmFromVM", result.data.toString())
                when(result.data?.toInt() == -1) {
                    false -> {
                        val alarmToBeAdded = AlarmModel().let {
                            it.id = result.data?:-1
                            it.time = alarm.time
                            it.name = alarm.name
                            it.isActive = alarm.isActive
                            it
                        }
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.add(alarmToBeAdded)
                        }
                        setAlarm(
                            isActive = alarmToBeAdded.isActive,
                            id = alarmToBeAdded.id, time = alarmToBeAdded.time,
                            name = alarmToBeAdded.name)

                    }
                    else -> {
                        // generate an error here
                        emitUiEvent(UIEvents.ShowError(
                            message = result.message.toString()
                        ))
                    }
                }
            }
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            _alarmsList.value = _alarmsList.value.apply {
                this.isLoading = true
            }
            alarmUseCase.getAllAlarms().collectLatest {result ->
                when(result) {
                    is Resources.Success -> {
                         val alarms = arrayListOf<AlarmModel>()
                        result.data?.forEach { alarm ->
                            alarms.add(alarm)
                        }
                        _alarmsList.value = _alarmsList.value.apply {
                            this.alarms = alarms
                            this.isLoading = false
                        }
                    }
                    is Resources.Error -> {
                        // generate an error here
                        emitUiEvent(UIEvents.ShowError(
                            message = result.message.toString()
                        ))
                    }
                    is Resources.Loading -> {
                        _alarmsList.value = _alarmsList.value.apply {
                            isLoading = true
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(isActive: Boolean, id: Long, name: String, time : Long) {
        Log.d("SettingAlarm", isActive.toString())
        Log.d("timeText", "${UiUtils.getDateTime(time.toString())}")
        Log.d("timeMillis", time.toString())

        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("INTENT_NOTIFY", true)
        intent.putExtra("id", id)
        intent.putExtra("name", name)
        intent.putExtra("dateTime", "Your alarm at ${UiUtils.getDateTime(time.toString())}")

        val pendingIntent = PendingIntent.getBroadcast(context, id.toInt(),
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        try {
            val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java) as AlarmManager
        if (isActive) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
               time, pendingIntent)
        } else {
            alarmManager.cancel(pendingIntent)
        }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}