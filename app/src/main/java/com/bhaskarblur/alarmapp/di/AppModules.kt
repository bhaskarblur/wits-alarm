package com.bhaskarblur.alarmapp.di

import android.content.Context
import com.bhaskarblur.alarmapp.data.repositoriesImpl.ImplAlarmRepository
import com.bhaskarblur.alarmapp.domain.repositories.IAlarmRepository
import com.bhaskarblur.alarmapp.domain.usecases.AlarmUseCase
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.example.todolistinkotlin.database.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun providesAlarmDb(@ApplicationContext context: Context) : AlarmDatabase {
        return AlarmDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providesAlarmRepo(alarmDatabase: AlarmDatabase) : IAlarmRepository {
        return ImplAlarmRepository(alarmDatabase)
    }

    @Provides
    @Singleton
    fun providesAlarmUseCase(alarmRepo: IAlarmRepository) : AlarmUseCase {
        return AlarmUseCase(alarmRepo)
    }

    @Provides
    @Singleton
    fun providesAlarmViewModel(alarmUseCase: AlarmUseCase, @ApplicationContext
    context: Context) : AlarmViewModel {
        return AlarmViewModel(alarmUseCase, context)
    }
}