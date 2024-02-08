import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.bhaskarblur.alarmapp.presentation.AlarmsScreen.widgets.AlarmsList
import java.util.Calendar

@Composable
fun AlarmScreen(viewModel: AlarmViewModel) {
    val selectedDateText = remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val timeText = rememberSaveable {
        mutableStateOf("")
    }
    val time = rememberSaveable {
        mutableLongStateOf(-1L)
    }

    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    val datePicker = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText.value = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, dayOfMonth
    )
    datePicker.datePicker.minDate = calendar.timeInMillis

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

        Spacer(Modifier.height(8.dp))

        TextField(
            value = timeText.value, onValueChange = {},
            readOnly = true, modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    // Open DateTime Picker
                    datePicker.show()
                }
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {

            if (time.longValue != -1L) {

            } else {
                // show Toast
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )) {
            Text("Set Alarm", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
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


