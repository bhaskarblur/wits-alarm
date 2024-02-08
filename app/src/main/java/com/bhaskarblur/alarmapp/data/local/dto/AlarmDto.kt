package com.bhaskarblur.alarmapp.data.local.dto

import com.bhaskarblur.alarmapp.domain.models.AlarmModel

data class AlarmDto(
    val id : Long = 0,
    val time: Long,
    val name : String,
    val isActive : Boolean
) {

    fun toAlarm() : AlarmModel {
        return AlarmModel(id, time, name, isActive)
    }
}