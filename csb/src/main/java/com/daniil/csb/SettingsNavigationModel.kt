package com.daniil.csb

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.error

class SettingsNavigationModel : ViewModel() {

    private var _screenHeap = MutableStateFlow(listOf<Screen>())
    val screenHeap = _screenHeap.asStateFlow()

    fun setScreensHeap(
        vararg screen: Screen
    ) {
        _screenHeap.value = screen.toList()
        if (screenStack.value.isEmpty()) {
            val primary = _screenHeap.value.firstOrNull { it.attribute?.contains(ScreenAttribute.Primary) == true }
            _screenStack.value.add(primary ?: screen[0])
            _currentScreen.value = primary ?: screen[0]
        }
    }

    private val _screenStack = MutableStateFlow(mutableStateListOf<Screen>())
    val screenStack = _screenStack.asStateFlow()


    private var _currentScreen = MutableStateFlow<Screen?>(null)
    val currentScreen = _currentScreen.asStateFlow()
    var lastNavigateAction = MutableStateFlow(LastNavigateAction.Go)
        private set
    enum class LastNavigateAction {
        Go,
        Back,
    }

    fun findScreenById(id: String): Screen {
        return screenHeap.value.find { it.id == id } ?: error("Screen $id not found")
    }
    fun findScreenBySetting(id: String): Screen {
        val screen = screenHeap.value.find { it.settings.values.flatten().find { it.id == id } != null }
        return screen ?: error("Setting $id not found")
    }
    fun findSettingById(id: String): ComposeSetting<*> {
        val settingsHeap = screenHeap.value.flatMap { it.settings.values }.flatten()
        val setting = settingsHeap.find { it.id == id } ?: error("Setting $id not found")
        return setting
    }
    fun findGroupById(id: String): Screen.Group {
        val groupHeap = _screenHeap.value.flatMap { it.settings.keys }
        val group = groupHeap.find { it.id == id } ?: error("Group $id not found")
        return group
    }

    fun navigateToSetting(id: String) {
        val setting = findSettingById(id)
        val screen = findScreenBySetting(id)
        if (currentScreen != screen) goToScreen(screen)
        screen.settingsScreenModel.focusToSetting(setting.id)
    }

    fun hideGroup(id: String, hide: Boolean) {
        val group = findGroupById(id)
        if (hide) group.hide() else group.show()
    }

    fun goToScreen(screen: Screen) {
        if (screen is AbstractScreen) error("Cannot go to abstract screen")
        if (screen.attribute?.contains(ScreenAttribute.NonRedirectable) == true) return
        val indexInStack = screenStack.value.indexOfFirst { it.id == screen.id }
        val isInStack = indexInStack != -1

        if (isInStack) {
            _currentScreen.update { screen }
            val stackSize = screenStack.value.size
            _screenStack.value.removeRange(indexInStack+1, stackSize)
            lastNavigateAction.update { LastNavigateAction.Back }
        } else {
            _currentScreen.update { screen }
            _screenStack.value.add(screen)
            lastNavigateAction.update { LastNavigateAction.Go }
        }
    }
    fun goToScreen(screenId: String) {
        val screenInstance = findScreenById(screenId)
        goToScreen(screenInstance)
    }

    fun goBack() {
        if (screenStack.value.size < 2) return
        goToScreen(screenStack.value.dropLast(1).last())
    }

}