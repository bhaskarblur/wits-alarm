package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.domain.models.AlarmModel

@Composable
fun AlarmDialog(onDismiss : () -> Unit, createAlarm : (name: String) -> Unit) {
    val name = remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
                           onDismiss()
        },
        title = {
            Text(
                text = "Set Alarm", fontSize = 22.sp, color = Color.Black,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            // add text field here
            Column(Modifier.padding(top = 12.dp)) {

                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = name.value, onValueChange = {
                        name.value = it
                    },
                    placeholder = {
                        Text(text = "Enter name of the alarm")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)

                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Dismiss")
                }
                Spacer(Modifier.width(12.dp))


                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        createAlarm(name.value)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Set Alarm")
                }
            }
        }
    )
}