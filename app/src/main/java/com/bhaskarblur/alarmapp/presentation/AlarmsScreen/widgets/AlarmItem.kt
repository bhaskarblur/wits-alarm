package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.utils.UiUtils

@Composable
fun AlarmItem(alarm: AlarmModel, onTimeEdit : (id : Long) -> Unit, onToggled : (id : Long, isActive : Boolean) -> Unit) {

    val checked = remember {
        mutableStateOf(alarm.isActive)
    }

    LaunchedEffect(alarm.isActive) {
        checked.value = alarm.isActive
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {

        Column {

            Text(
                text = alarm.name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row {
            Text(
                text = UiUtils.getDateTime(alarm.time.toString())?:"Invalid Date time",
                color = Color.Gray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(Icons.Filled.Edit, contentDescription = "change time",
                    tint = Color.Black, modifier = Modifier.size(22.dp)
                        .clickable {
                            onTimeEdit(alarm.id)
                        })

            }
        }


        Switch(
            checked = checked.value,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Blue,
                checkedTrackColor = Color.Gray,
                uncheckedTrackColor = Color.Gray,
            ),
            onCheckedChange = {
                checked.value = it
                onToggled(alarm.id, it)
                              },
            modifier = Modifier.padding(10.dp)
        )
    }
}