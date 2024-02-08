package com.bhaskarblur.alarmapp.domain.models

import com.bhaskarblur.alarmapp.data.local.dto.AlarmDto
import com.bhaskarblur.alarmapp.domain.entities.AlarmEntity

data class AlarmModel(
    var id : Long = 0,
    var time: Long = 0,
    var name : String = "",
    var isActive : Boolean = true
) {
    fun toAlarmDto() : AlarmDto {
        return AlarmDto(id, time, name, isActive)
    }
}