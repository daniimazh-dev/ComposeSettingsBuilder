package com.daniil.csb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.csb.classes.SettingsSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(screenInstance: ScreenInstance) : ViewModel() {
    val currentScreen = MutableStateFlow<ScreenInstance?>(null).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = screenInstance
    )

    private val _settings = MutableStateFlow(screenInstance.settings)
    val settings = _settings.asStateFlow()

    private var _title = MutableStateFlow(screenInstance.title)
    val title = _title.asStateFlow()


}