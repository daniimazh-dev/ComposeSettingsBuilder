package com.daniil.csb

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.csb.classes.SettingsSealed
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.ScreenInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.error

class SettingsNavigationModel : ViewModel() {

    private var _screenHeap = MutableStateFlow(listOf<ScreenInstance>())
    val screenHeap = _screenHeap.asStateFlow()

    fun setScreensHeap(
        vararg screen: ScreenInstance
    ) {
        _screenHeap.value = screen.toList()
        _screenStack.value.add(screen[0])
        _currentScreen.value = screen[0]
    }
    lateinit var config: CSBConfig

    fun initialize(context: Context) {
        val json = context.assets.open("csb/csb_config.json").bufferedReader().use { it.readText() }
        val config = Json.decodeFromString<CSBConfig>(json)
        this.config = config
        SettingsProvider.innit(this)
    }

    private val _screenStack = MutableStateFlow(mutableStateListOf<ScreenInstance>())
    val screenStack = _screenStack.asStateFlow()



    private var _currentScreen = MutableStateFlow<ScreenInstance?>(null)
    val currentScreen = _currentScreen.asStateFlow()
    var lastNavigateAction = MutableStateFlow(LastNavigateAction.Go)
        private set
    enum class LastNavigateAction {
        Go,
        Back
    }

    fun findScreenById(id: String): ScreenInstance {
        return screenHeap.value.find { it.id == id } ?: error("Screen $id not found")
    }
    fun findScreenBySetting(id: String): ScreenInstance {
        val screen = screenHeap.value.find { it.settings.values.flatten().find { it.id == id } != null }
        return screen ?: error("Setting $id not found")
    }
    fun findSettingById(id: String): SettingsSealed<*> {
        val settingsHeap = screenHeap.value.flatMap { it.settings.values }.flatten()
        val setting = settingsHeap.find { it.id == id } ?: error("Setting $id not found")
        return setting
    }

    fun navigateToSetting(id: String) {
        val setting = findSettingById(id)
        val screen = findScreenBySetting(id)
        if (currentScreen != screen) goToScreen(screen)
        viewModelScope.launch {
            delay(200)
            repeat(2) {
                delay(200)
                setting.focus(true)
                delay(200)
                setting.focus(false)
            }
        }
    }

    fun goToScreen(screenInstance: ScreenInstance) {
        if (screenInstance is AbstractScreen) error("Cannot go to abstract screen")
        if (screenStack.value.find { it.id == screenInstance.id } != null) return
        _currentScreen.update { screenInstance }
        _screenStack.value.add(screenInstance)
        lastNavigateAction.update { LastNavigateAction.Go }
    }

    fun goBack() {
        if (screenStack.value.size < 2) return

        _screenStack.update { it.dropLast(1).toMutableStateList() }
        _currentScreen.update { screenStack.value.last() }
        lastNavigateAction.update { LastNavigateAction.Back }
    }

}