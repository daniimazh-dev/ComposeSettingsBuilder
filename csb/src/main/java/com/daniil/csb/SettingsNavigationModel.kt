package com.daniil.csb

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.FragmentController
import com.daniil.csb.screens.FragmentedGroup
import com.daniil.csb.screens.Group
import com.daniil.csb.screens.GroupController
import com.daniil.csb.screens.GroupSealed
import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.screens.ScreenController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.error

class SettingsNavigationModel : ViewModel() {

    private var _screenHeap = MutableStateFlow(listOf<Screen>())
    val screenHeap = _screenHeap.asStateFlow()

    fun setScreensHeap(
        vararg screen: Screen
    ) {
        _screenHeap.value = screen.toList()
        if (screenStack.value.isEmpty()) {
            val primary = screenHeap.value.firstOrNull {
                it.attribute?.contains(ScreenAttribute.Primary) == true ||
                it.id == CSB.config.primaryScreenId
            }
            if (primary == null) {
                val index = screenHeap.value.indexOfFirst { it !is AbstractScreen }
                if (index != -1) {
                    val targetScreen = screen[index]
                    val attribute = targetScreen.attribute ?: listOf()
                    targetScreen.attribute = attribute + listOf(ScreenAttribute.Primary)
                    _screenStack.value.add(targetScreen)
                    _currentScreen.value = targetScreen
                } else error("Not found primary screen")
            } else {
                _screenStack.value.add(primary)
                _currentScreen.value = primary
            }
        }
    }

    private val _screenStack = MutableStateFlow(mutableStateListOf<Screen>())
    val screenStack = _screenStack.asStateFlow()


    private var _currentScreen = MutableStateFlow<Screen?>(null)
    val currentScreen = _currentScreen.asStateFlow()
    var lastNavigateAction = MutableStateFlow(LastNavigateAction.Forward)
        private set

    enum class LastNavigateAction {
        Forward,
        Back,
    }

    fun findScreenById(id: String): Screen {
        return screenHeap.value.find { it.id == id } ?: error("Screen $id not found")
    }

    fun findScreenBySetting(id: String): Screen {
        val screen =
            screenHeap.value.find { it.settings.flatMap { it.settings }.find { it.id == id } != null }
        return screen ?: error("Setting $id not found")
    }

    fun findScreenByGroup(id: String): Screen {
        val screen = screenHeap.value.find { it.settings.find { it.id == id } != null }
        return screen ?: error("Setting $id not found")
    }

    fun findSettingById(id: String): ComposeSetting<*> {
        val settingsHeap = screenHeap.value.flatMap { it.settings.flatMap { it.settings } }
        val setting = settingsHeap.find { it.id == id } ?: error("Setting $id not found")
        return setting
    }

    fun findGroupById(id: String): GroupSealed {
        val groupHeap = _screenHeap.value.flatMap { it.settings }
        val group = groupHeap.find { it.id == id } ?: error("Group $id not found")
        return group
    }

    fun navigateToSetting(id: String) {
        val setting = findSettingById(id)
        val screen = findScreenBySetting(id)
        if (currentScreen != screen) goToScreen(screen)
        screen.settingsScreenModel.focusToSetting(setting.id)
    }

    fun navigateToGroup(groupId: String) {
        val group = findGroupById(groupId)
        val screen = findScreenByGroup(group.id)
        if (currentScreen != screen) goToScreen(screen)
        screen.settingsScreenModel.focusToGroup(group.id)
    }

    fun hideGroup(screenId: String = currentScreen.value!!.id, groupId: String, isHide: Boolean) {
        val screen = findScreenById(screenId)
        screen.settingsScreenModel.hideGroup(groupId, isHide)
    }

    fun disableGroup(
        screenId: String = currentScreen.value!!.id,
        groupId: String,
        isDisable: Boolean
    ) {
        val screen = findScreenById(screenId)
        screen.settingsScreenModel.disableGroup(groupId, isDisable)
    }
    fun fragmentController(id: String): FragmentController {
        val fragmentedGroup = findGroupById(id)
        return FragmentController((fragmentedGroup as? FragmentedGroup) ?: error("Group \"$id\" is not FragmentedGroup"))
    }
    fun groupController(id: String): GroupController {
        val fragmentedGroup = findGroupById(id)
        return GroupController((fragmentedGroup as? Group) ?: error("Group \"$id\" is not BasicGroup"))
    }
    fun screenController(id: String): ScreenController {
        val screen = findScreenById(id)
        return ScreenController(screen)
    }

    fun goToScreen(screen: Screen) {
        if (screen is AbstractScreen && !"allowDisplayAbstractScreen".isInFlag()) error("Cannot go to abstract screen")
        if (screen.attribute?.contains(ScreenAttribute.NonRedirectable) == true) return
        val indexInStack = screenStack.value.indexOfFirst { it.id == screen.id }
        val isInStack = indexInStack != -1

        if (isInStack) {
            _currentScreen.update { screen }
            val stackSize = screenStack.value.size
            _screenStack.value.removeRange(indexInStack + 1, stackSize)
            lastNavigateAction.update { LastNavigateAction.Back }
        } else {
            _currentScreen.update { screen }
            _screenStack.value.add(screen)
            lastNavigateAction.update { LastNavigateAction.Forward }
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