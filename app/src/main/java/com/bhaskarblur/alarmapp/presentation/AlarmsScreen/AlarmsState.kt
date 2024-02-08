package com.bhaskarblur.alarmapp.presentation.AlarmsScreen

import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import javax.annotation.concurrent.Immutable

@Immutable
data class AlarmsState(
    var isLoading : Boolean = false,
    var alarms: ArrayList<AlarmModel> = arrayListOf()
)