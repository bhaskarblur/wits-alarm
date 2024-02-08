package com.example.todolistinkotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bhaskarblur.alarmapp.data.local.dto.AlarmDto
import com.bhaskarblur.alarmapp.domain.models.AlarmModel

/**
 *   Created by Sundar Pichai on 13/8/19.
 */

@Dao
interface AlarmDAO {
    @Query("SELECT * from alarms")
    fun getAll(): List<AlarmDto>

    @Insert
    fun insert(alarm: AlarmDto) : Long

    @Query("UPDATE alarms Set isActive = :isActive  where id LIKE :id")
    fun toggleIsActive(id:Long , isActive : Boolean)


}