package com.bhaskarblur.alarmapp.domain.usecases

import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.domain.repositories.IAlarmRepository
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmUseCase @Inject constructor(
    private val alarmRepo : IAlarmRepository
) {

    fun createAlarm(alarmModel: AlarmModel) : Flow<Resources<Long>> {
        return alarmRepo.createAlarm(alarmModel)
    }
    fun toggleAlarm(id: Long, status : Boolean) : Flow<Resources<Boolean>> {
        return alarmRepo.toggleAlarm(id, status)
    }
    fun getAllAlarms() : Flow<Resources<List<AlarmModel>>> {
        return alarmRepo.getAllAlarms()
    }

    fun changeTime(id: Long, time: Long): Flow<Resources<Boolean>> {
        return alarmRepo.changeTime(id, time)
    }
}