import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmDialog
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmTimeConfirmDialog
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmsList
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.DatePicker
import com.bhaskarblur.alarmapp.presentation.UIEvents
import com.bhaskarblur.alarmapp.utils.TimeUtil
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext as LocalContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AlarmScreen(viewModel: AlarmViewModel) {

    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val pickedTime = remember {
        mutableStateOf(LocalTime.now())
    }
    val pickedDateTimeStamp = remember {
        mutableStateOf(
            TimeUtil.timeFormat.parse(
                DateTimeFormatter
                    .ofPattern("MMM dd yyyy")
                    .format(pickedDate.value).toString().plus(
                        DateTimeFormatter
                            .ofPattern(", hh:mm a")
                            .format(pickedTime.value)
                    )
            )?.time ?: 0L
        )
    }

    val selectedChangeTimeStamp = remember {
        mutableStateOf(0L)
    }

    val selectedEditId = remember {
        mutableStateOf(0L)
    }

    val hasToChangeTime = remember {
        mutableStateOf(false)
    }
    val confirmHasToChangeTime = remember {
        mutableStateOf(false)
    }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    val dateTimeText = remember {
        mutableStateOf("Select Date time")
    }

    val broadcastData = remember {
        mutableStateOf("No data received")
    }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val filter = IntentFilter("com.your.package.ACTION_CANCEL_ALARM")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val alarmId = intent?.getLongExtra("alarmId", -1L)
                broadcastData.value = "Received $alarmId from alarm"
                Log.d("SwitchToggleReceived", alarmId.toString())
                alarmId?.let {
                    viewModel.toggleAlarm(alarmId, false)
                    pickedDateTimeStamp.value = TimeUtil.timeFormat.parse(
                        DateTimeFormatter
                            .ofPattern("MMM dd yyyy")
                            .format(pickedDate.value).toString().plus(
                                DateTimeFormatter
                                    .ofPattern(", hh:mm")
                                    .format(pickedTime.value)
                            )
                    )?.time ?: 0L
                }
            }
        }
        context.registerReceiver(receiver, filter, ComponentActivity.RECEIVER_EXPORTED)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(pickedTime.value) {
        dateTimeText.value = DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(pickedDate.value).toString().plus(
                DateTimeFormatter
                    .ofPattern(", hh:mm a")
                    .format(pickedTime.value)
            )

//        with(TimeUtil.timeFormat) {
//            pickedDateTimeStamp.value = parse(
//                DateTimeFormatter
//                    .ofPattern("MMM dd yyyy")
//                    .format(pickedDate.value).toString().plus(
//                        DateTimeFormatter
//                            .ofPattern(", hh:mm a")
//                            .format(pickedTime.value)
//                    )
//            )?.time ?: 0L
//        }
        Log.d("pickedTimeStamp", pickedDateTimeStamp.value.toString())
    }
    DatePicker(
        currentDate = pickedDate.value,
        currentTime = pickedTime.value,
        dateDialogState = dateDialogState,
        timeDialogState = timeDialogState,
        onDatePicked = {
            pickedDate.value = it
        },
        onTimePicked = { localTime: LocalTime, time : Long, localDateTime: LocalDateTime ->
             if(localDateTime.isAfter(LocalDateTime.now()) ||
                localDateTime.isEqual(LocalDateTime.now())) {
                pickedTime.value = localTime
                 pickedDateTimeStamp.value = time

            } else {
                viewModel.emitUiEvent(
                    UIEvents.ShowError("The time cannot be in past.")
                )
            }
        },
        hasToChangeTime = hasToChangeTime.value,
        onTimeChanged = { timeMillis: Long, localDateTime: LocalDateTime ->
            if(localDateTime.isAfter(LocalDateTime.now()) ||
                localDateTime.isEqual(LocalDateTime.now())) {
                selectedChangeTimeStamp.value = timeMillis
                confirmHasToChangeTime.value = true
            } else {
                viewModel.emitUiEvent(
                    UIEvents.ShowError("The time cannot be in past.")
                )
            }
        }
    )

    val openDialog = remember { mutableStateOf(false) }

    if (confirmHasToChangeTime.value) {
        AlarmTimeConfirmDialog(onDismiss = {
            confirmHasToChangeTime.value = false
            pickedDate.value = LocalDate.now()
            pickedTime.value = LocalTime.now()
        }) {
            viewModel.changeAlarmTime(
                selectedEditId.value,
                selectedChangeTimeStamp.value
            )
            confirmHasToChangeTime.value = false
            pickedDate.value = LocalDate.now()
            pickedTime.value = LocalTime.now()
        }
    }
    if (openDialog.value) {
        AlarmDialog(
            onDismiss = {
                openDialog.value = false
            }
        ) { name ->
            if (name.isNotEmpty()) {
                selectedEditId.value = 0L
                viewModel.createAlarm(
                    alarm =
                    AlarmModel(
                        time = pickedDateTimeStamp.value,
                        name = name, isActive = true
                    )
                )
                Log.d("alarmCreated", "true")
                val currTime = DateTimeFormatter
                    .ofPattern("MMM dd yyyy")
                    .format(LocalDate.now()).toString().plus(
                        DateTimeFormatter
                            .ofPattern(", hh:mm a")
                            .format(LocalTime.now())
                    )
                dateTimeText.value = currTime
                pickedDateTimeStamp.value
                    TimeUtil.timeFormat.parse(
                        DateTimeFormatter
                            .ofPattern("MMM dd yyyy")
                            .format(LocalDate.now()).toString().plus(
                                DateTimeFormatter
                                    .ofPattern(", hh:mm a")
                                    .format(LocalTime.now())
                            )
                    )?.time ?: 0L

                openDialog.value = false
            } else {
                viewModel.emitUiEvent(
                    UIEvents.ShowError(
                        message = "Please enter the name of the alarm."
                    )
                )
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        Text(
            text = "Set an Alarm",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = dateTimeText.value, onValueChange = {},
            readOnly = true,
            enabled = false,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    hasToChangeTime.value = false
                    dateDialogState.show()
                }
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                openDialog.value = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text(
                "Set Alarm",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(18.dp))

            Text(
                text = "All Alarms",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )


        Spacer(Modifier.height(14.dp))

        Text(
            broadcastData.value,
            color = Color.Black,
            fontSize = 0.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        AlarmsList(viewModel.alarmsList.value, onToggled = { id: Long, isActive: Boolean ->
            viewModel.toggleAlarm(id, isActive)
        }) { id: Long, timeMillis: Long ->
            pickedDate.value =
                Instant.ofEpochMilli(timeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            pickedTime.value =
                Instant.ofEpochMilli(timeMillis).atZone(ZoneId.systemDefault()).toLocalTime()
            selectedEditId.value = id
            hasToChangeTime.value = true
            dateDialogState.show()
        }
    }

}


