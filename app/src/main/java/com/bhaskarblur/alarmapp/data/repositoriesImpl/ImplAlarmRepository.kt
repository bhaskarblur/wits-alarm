package com.bhaskarblur.alarmapp.data.repositoriesImpl

import android.util.Log
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.domain.repositories.IAlarmRepository
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.example.todolistinkotlin.database.AlarmDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImplAlarmRepository
@Inject constructor(
    private val alarmsDb: AlarmDatabase
) : IAlarmRepository {
    override fun createAlarm(alarmModel: AlarmModel): Flow<Resources<Long>> = flow {
        try {
            Log.d("creatingAlarmFromRepo", "true")
            emit(
                Resources.Success(
                    data = alarmsDb.alarmsDto().insert(alarmModel.toAlarmDto().toAlarmEntity())
                )
            )
        } catch (e : Exception) {
            emit(Resources.Error(
                data = -1,
                message = e.message ?: "There was an error in creating alarms."
            ))
        }
    }

    override fun toggleAlarm(id: Long, status: Boolean) : Flow<Resources<Boolean>> = flow{
        try {
            alarmsDb.alarmsDto().toggleIsActive(id, status)
            emit(Resources.Success(data = true))
        }
        catch (e : Exception) {
            e.printStackTrace()
            emit(Resources.Error(
                data = false,
                message = e.message ?: "There was an error in toggling alarms."
            ))
        }
    }

    override fun getAllAlarms(): Flow<Resources<List<AlarmModel>>> = flow{
        try {
            val alarms = alarmsDb.alarmsDto().getAll().map {
                it.toAlarmDto()
            }
            emit(Resources.Success(
                data = alarms.map { it.toAlarm() }
            ))
        }
        catch (e : Exception) {
            e.printStackTrace()
            emit(Resources.Error(
                data = listOf(),
                message = e.message ?: "There was an error in fetching alarms."
            ))
        }
    }

    override fun changeTime(id: Long, time: Long): Flow<Resources<Boolean>> = flow{
        try {
            alarmsDb.alarmsDto().changeTime(id, time)
            emit(Resources.Success(data = true))
        }
        catch (e : Exception) {
            e.printStackTrace()
            emit(Resources.Error(
                data = false,
                message = e.message ?: "There was an error in changing time."
            ))
        }
    }
}