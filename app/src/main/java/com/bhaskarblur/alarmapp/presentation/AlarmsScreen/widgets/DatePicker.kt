package com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bhaskarblur.alarmapp.utils.TimeUtil
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(
    currentDate: LocalDate?,
    currentTime: LocalTime?,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState, onDatePicked: (LocalDate) -> Unit,
    onTimePicked: (LocalTime, LocalDateTime) -> Unit, hasToChangeTime: Boolean,
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

            if (hasToChangeTime) {
                val timeMillis = TimeUtil.timeFormat.parse(
                    DateTimeFormatter
                        .ofPattern("MMM dd yyyy")
                        .format(selectedDate.value).toString().plus(
                            DateTimeFormatter
                                .ofPattern(", hh:mm")
                                .format(time)
                        )
                )?.time ?: 0L

                onTimeChanged(timeMillis, selectedDateTime ?: LocalDateTime.now())
            } else {
                onTimePicked(time, selectedDateTime ?: LocalDateTime.now())
            }
        }
    }
}