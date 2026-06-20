package com.daniil.csb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.csb.screens.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsScreenModel(screen: Screen) : ViewModel() {
    val currentScreen = MutableStateFlow<Screen?>(null).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = screen
    )
    val modifier = currentScreen.value?.modifier

    private val _settings = MutableStateFlow(screen.settings)
    val settings = _settings.asStateFlow()

    private var _title = MutableStateFlow(screen.title)
    val title = _title.asStateFlow()
}