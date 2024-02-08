package com.bhaskarblur.alarmapp.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *   Created by Sundar Pichai on 5/8/19.
 */

@Entity(tableName = "alarms")
data class AlarmEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "time" ) val time : String="",
    @ColumnInfo(name = "isActive" ) val isActive : Boolean = true
)
