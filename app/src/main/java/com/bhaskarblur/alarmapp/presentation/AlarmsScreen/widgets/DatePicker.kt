package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bhaskarblur.alarmapp.utils.TimeUtil
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(dateDialogState : MaterialDialogState,
               timeDialogState : MaterialDialogState, onDatePicked : (LocalDate) -> Unit,
               onTimePicked : (LocalTime) -> Unit, hasToChangeTime: Boolean,
               onTimeChanged : (Long) -> Unit) {
    val selectedDate = remember {
        mutableStateOf(LocalDate.now())
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                timeDialogState.show()
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
        ) {
            onDatePicked(it)
            selectedDate.value = it
        }
    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {
            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.now(),
            title = "Pick a time",
        ) {time ->
            if(hasToChangeTime) {
                val timeMillis = TimeUtil.timeFormat.parse( DateTimeFormatter
                    .ofPattern("MMM dd yyyy")
                    .format(selectedDate.value).toString().plus(
                        DateTimeFormatter
                            .ofPattern(", hh:mm")
                            .format(time)
                    ))?.time?:0L

                onTimeChanged(timeMillis)
            }
            else {
                onTimePicked(time)
            }
        }
    }
}