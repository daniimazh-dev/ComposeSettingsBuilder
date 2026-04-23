package com.daniil.csb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatModelFactory(private val instance: ScreenInstance) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
