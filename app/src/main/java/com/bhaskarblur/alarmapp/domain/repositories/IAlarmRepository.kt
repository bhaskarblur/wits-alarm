package com.bhaskarblur.alarmapp.domain.repositories

import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import kotlinx.coroutines.flow.Flow

interface IAlarmRepository {
    fun createAlarm(alarmModel: AlarmModel) : Flow<Resources<Long>>
    fun toggleAlarm(id: Long, status : Boolean) : Flow<Resources<Boolean>>
    fun getAllAlarms() : Flow<Resources<List<AlarmModel>>>
}