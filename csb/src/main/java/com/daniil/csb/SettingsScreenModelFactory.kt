package com.daniil.csb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daniil.csb.screens.Screen

class SettingsScreenModelFactory(private val instance: Screen) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsScreenModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsScreenModel(instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }}
