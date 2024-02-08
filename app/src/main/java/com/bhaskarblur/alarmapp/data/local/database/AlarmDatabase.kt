package com.example.todolistinkotlin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bhaskarblur.alarmapp.domain.entities.AlarmEntity

/**
 *   Created by Sundar Pichai on 13/8/19.
 */

@Database(entities = [AlarmEntity::class] , version = 1, exportSchema = true)
abstract class AlarmDatabase : RoomDatabase(){

    abstract fun alarmsDto() : AlarmDAO

    companion object{
        @Volatile
        private var instance : AlarmDatabase? = null

        fun getInstance(context: Context): AlarmDatabase {
            if (instance == null) {
                synchronized(AlarmDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        AlarmDatabase::class.java, "alarmDb")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance!!
        }

    }

}