package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import android.util.Log
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DatePicker(
    currentDate: LocalDate?,
    currentTime: LocalTime?,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState, onDatePicked: (LocalDate) -> Unit,
    onTimePicked: (LocalTime, Long, LocalDateTime) -> Unit, hasToChangeTime: Boolean,
    onTimeChanged: (Long, LocalDateTime) -> Unit
) {
    val selectedDate = remember {
        mutableStateOf(currentDate ?: LocalDate.now())
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
            initialDate = currentDate ?: LocalDate.now(),
            allowedDateValidator = {
                it.isAfter(LocalDate.now()) || it.isEqual(LocalDate.now())
            },
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
            initialTime = currentTime ?: LocalTime.now(),
            title = "Pick a time",

        ) { time ->

            val selectedDateTime = LocalDateTime.of(selectedDate.value, time)
            val calendar = TimeUtil.calendar
            calendar.set(Calendar.YEAR, selectedDateTime.year)
            calendar.set(Calendar.MONTH, selectedDateTime.monthValue -1)
            calendar.set(Calendar.DATE, selectedDateTime.dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, time.hour)
            calendar.set(Calendar.MINUTE, selectedDateTime.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val timeMillis = calendar.timeInMillis
            if (hasToChangeTime) {
                onTimeChanged(timeMillis, selectedDateTime ?: LocalDateTime.now())
            } else {
                onTimePicked(time,timeMillis, selectedDateTime ?: LocalDateTime.now())
            }
        }
    }
}