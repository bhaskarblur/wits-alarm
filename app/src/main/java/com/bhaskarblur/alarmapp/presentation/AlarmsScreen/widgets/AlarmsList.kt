package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.AlarmsState

@Composable
fun AlarmsList(alarmsList: MutableState<AlarmsState>, onToggled : (id : Long, isActive : Boolean) -> Unit) {

    Column(Modifier.fillMaxSize()) {

    if (alarmsList.value.isLoading) {

        CircularProgressIndicator(
            color = Color.Blue, strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
    } else {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(alarmsList.value.alarms, key = { alarm ->
                alarm.id
            }) { alarm ->

                AlarmItem(alarm = alarm, onToggled = { id: Long, isActive: Boolean ->
                    onToggled(id, isActive)
                })
            }
        }
    }
}
}