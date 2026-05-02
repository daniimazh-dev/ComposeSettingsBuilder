package com.daniil.csb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daniil.csb.screens.ScreenInstance

class ChatModelFactory(private val instance: ScreenInstance) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsScreenModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsScreenModel(instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
