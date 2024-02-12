package com.example.todolistinkotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bhaskarblur.alarmapp.data.local.dto.AlarmDto
import com.bhaskarblur.alarmapp.domain.entities.AlarmEntity
import com.bhaskarblur.alarmapp.domain.models.AlarmModel

/**
 *   Created by Sundar Pichai on 13/8/19.
 */

@Dao
interface AlarmDAO {
    @Query("SELECT * from alarms")
    fun getAll(): List<AlarmEntity>

    @Query("SELECT * from alarms where id LIKE :id")
    fun getAlarmById(id:Long): AlarmEntity
    @Insert
    fun insert(alarm: AlarmEntity) : Long

    @Query("UPDATE alarms Set isActive = :isActive where id LIKE :id")
    fun toggleIsActive(id:Long , isActive : Boolean)

    @Query("UPDATE alarms Set time = :time where id LIKE :id")
    fun changeTime(id:Long , time : Long)

}