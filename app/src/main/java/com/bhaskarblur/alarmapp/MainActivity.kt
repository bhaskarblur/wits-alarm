package com.bhaskarblur.alarmapp

import AlarmScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.bhaskarblur.alarmapp.presentation.AlarmViewModel
import com.bhaskarblur.alarmapp.presentation.UIEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel by viewModels<AlarmViewModel>()
        viewModel.getAllAlarms()

        setContent {
            LaunchedEffect(viewModel.eventFlow) {
                viewModel.eventFlow.collectLatest {result ->
                    when(result) {
                        is UIEvents.ShowError -> {
                            Toast.makeText(this@MainActivity,
                                result.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }
            AlarmScreen(viewModel)
        }
    }

}

