package com.bhaskarblur.alarmapp.domain.models

import com.bhaskarblur.alarmapp.data.local.dto.AlarmDto
import com.bhaskarblur.alarmapp.domain.entities.AlarmEntity

data class AlarmModel(
    val id : Long = 0,
    val time: Long = 0,
    val name : String = "",
    var isActive : Boolean = true
) {
    fun toAlarmDto() : AlarmDto {
        return AlarmDto(id, time, name, isActive)
    }
}