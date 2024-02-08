package com.bhaskarblur.alarmapp.presentation

sealed class UIEvents {
    data class ShowError(val message: String) : UIEvents()
}