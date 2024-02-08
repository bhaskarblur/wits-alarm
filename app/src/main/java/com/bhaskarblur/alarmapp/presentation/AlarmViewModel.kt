package com.bhaskarblur.alarmapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.domain.usecases.AlarmUseCase
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.AlarmsState
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun toggleAlarm(id : Long, isActive : Boolean) {
        viewModelScope.launch {
            alarmUseCase.toggleAlarm(id, isActive).collectLatest { result ->
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

    fun createAlarm(alarm : AlarmModel) {
        viewModelScope.launch {
            alarmUseCase.createAlarm(alarm).collectLatest { result ->
                when(result.data?.toInt() == -1) {
                    true -> {
                        val alarmToBeAdded = AlarmModel(result.data?:-1,
                            alarm.time, alarm.name, alarm.isActive
                            )
                        _alarmsList.value = _alarmsList.value.apply {
                            alarms.add(alarmToBeAdded)
                        }

                    }
                    else -> {
                        // generate an error here
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