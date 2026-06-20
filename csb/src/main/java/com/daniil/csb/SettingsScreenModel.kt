package com.daniil.csb

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.screens.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenModel(screen: Screen) : ViewModel() {
    val currentScreen = MutableStateFlow<Screen?>(null).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = screen
    )
    private val _settings = MutableStateFlow(screen.settings)
    val settings = _settings.asStateFlow()

    val modifier = currentScreen.value?.modifier
    val lazyListState = LazyListState()

    private val _scrollFocusIndex = MutableStateFlow<Int?>(null)
    val scrollFocusIndex = _scrollFocusIndex.asStateFlow()

    fun focusToSetting(id: String) {
        val setting = findSettingById(id)
        viewModelScope.launch {
            val index = settings.value.values.flatten().indexOfFirst { it.id == id }
            if (index == -1) error("Index of setting $id not found in screen ${currentScreen.value?.id}")
            _scrollFocusIndex.value = index
            delay(300)
            _scrollFocusIndex.value = null
            repeat(2) {
                delay(200)
                setting.focus(true)
                delay(200)
                setting.focus(false)
            }
        }
    }

    fun findSettingById(id: String): ComposeSetting<*> {
        val settingsHeap = settings.value.values.flatten()
        val setting = settingsHeap.find { it.id == id } ?: error("Setting $id not found in screen ${currentScreen.value?.id}")
        return setting
    }
    private var _title = MutableStateFlow(screen.title)
    val title = _title.asStateFlow()
}