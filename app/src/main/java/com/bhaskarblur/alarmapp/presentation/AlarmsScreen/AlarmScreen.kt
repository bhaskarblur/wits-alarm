import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmDialog
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmsList
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.DatePicker
import com.bhaskarblur.alarmapp.presentation.UIEvents
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AlarmScreen(viewModel: AlarmViewModel) {

    val context = LocalContext.current
    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val pickedTime = remember {
        mutableStateOf(LocalTime.now())
    }

    val pickedDateTimeStamp = remember {
        mutableStateOf(0L)
    }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    val dateTimeText = remember {
        mutableStateOf("Select Date time")
    }

    LaunchedEffect(pickedTime.value) {
        dateTimeText.value = DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(pickedDate.value).toString().plus(
                DateTimeFormatter
                    .ofPattern(", hh:mm")
                    .format(pickedTime.value)
            )

        val dateFormatter = SimpleDateFormat("MMM dd yyyy, HH:mm")
        pickedDateTimeStamp.value = dateFormatter.parse(
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedDate.value).toString().plus(
                    DateTimeFormatter
                        .ofPattern(", hh:mm")
                        .format(pickedTime.value)
                )
        )?.time ?: 0L

        Log.d("pickedTimeStamp", pickedDateTimeStamp.value.toString())
    }
    DatePicker(
        dateDialogState = dateDialogState,
        timeDialogState = timeDialogState,
        onDatePicked = {
            pickedDate.value = it
        },
        onTimePicked = {
            pickedTime.value = it
        }
    )

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlarmDialog(onDismiss = {
            openDialog.value = false
        }) { name ->
            if(name.isNotEmpty()) {
                viewModel.createAlarm(
                    alarm =
                    AlarmModel(
                        time = pickedDateTimeStamp.value,
                        name = name, isActive = true
                    )
                )
                openDialog.value = false
            }
            else {
                viewModel.emitUiEvent(UIEvents.ShowError(
                    message = "Please enter the name of the alarm."
                ))
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
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    // Open DateTime Picker

                    Log.d("Clicked", "true")
                    dateDialogState.show()
                }
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                // Open save dialog
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

        AlarmsList(viewModel.alarmsList) { id: Long, isActive: Boolean ->
            viewModel.toggleAlarm(id, isActive)
        }
    }

}


