package com.bhaskarblur.alarmapp.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bhaskarblur.alarmapp.data.local.dto.AlarmDto

/**
 *   Created by Sundar Pichai on 5/8/19.
 */

@Entity(tableName = "alarms")
data class AlarmEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "time" ) val time : Long= 0,
    @ColumnInfo(name = "name" ) val name : String="",
    @ColumnInfo(name = "isActive" ) val isActive : Boolean = true
) {
    fun toAlarmDto() : AlarmDto {
        return AlarmDto(id, time, name, isActive)
    }
}
