package com.bhaskarblur.alarmapp.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.domain.usecases.AlarmUseCase
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.AlarmsState
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel
@Inject constructor(
    private val alarmUseCase: AlarmUseCase
) : ViewModel() {

    private val _alarmsList = mutableStateOf(AlarmsState())
    val alarmsList get() = _alarmsList

    val eventFlow = MutableSharedFlow<UIEvents>()

    fun toggleAlarm(id : Long, isActive : Boolean) {
        viewModelScope.launch {
            alarmUseCase.toggleAlarm(id, isActive).collectLatest { result ->
                Log.d("togglingAlarmFromVM", result.data.toString())
                when(result.data) {
                    true -> {
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.find {
                                it.id == id
                            }?.isActive = isActive
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

   private fun emitUiEvent(event: UIEvents) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }
    fun createAlarm(alarm : AlarmModel) {
        viewModelScope.launch {
            alarmUseCase.createAlarm(alarm).collectLatest { result ->
                Log.d("creatingAlarmFromVM", result.data.toString())
                when(result.data?.toInt() == -1) {
                    false -> {
                        val alarmToBeAdded = AlarmModel(result.data?:-1,
                            alarm.time, alarm.name, alarm.isActive
                            )
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.add(alarmToBeAdded)
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

    fun getAllAlarms() {
        viewModelScope.launch {
            alarmUseCase.getAllAlarms().collectLatest {result ->
                when(result) {
                    is Resources.Success -> {
                         val alarms = arrayListOf<AlarmModel>()
                        result.data?.forEach { alarm ->
                            alarms.add(alarm)
                        }
                        _alarmsList.value = _alarmsList.value.apply {
                            this.alarms = alarms
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
}