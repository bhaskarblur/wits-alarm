import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.domain.models.AlarmModel
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmsList
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.DatePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(viewModel: AlarmViewModel) {

    val context = LocalContext.current
    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val pickedTime = remember {
        mutableStateOf(LocalTime.NOON)
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
        val name = remember {
            mutableStateOf("")
        }
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
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
                        onClick = { openDialog.value = false },
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
                            viewModel.createAlarm(
                                alarm =
                                AlarmModel(-1, pickedDateTimeStamp.value, name.value, true)
                            )
                            openDialog.value = false
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

        Spacer(Modifier.height(8.dp))

        AlarmsList(viewModel.alarmsList) { id: Long, isActive: Boolean ->
            viewModel.toggleAlarm(id, isActive)
        }
    }

}


