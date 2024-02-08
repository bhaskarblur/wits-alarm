package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DatePicker( dateDialogState : MaterialDialogState,
               timeDialogState : MaterialDialogState, onDatePicked : (LocalDate) -> Unit,
               onTimePicked : (LocalTime) -> Unit) {
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
            initialTime = LocalTime.NOON,
            title = "Pick a time",
        ) {
            onTimePicked(it)
        }
    }
}