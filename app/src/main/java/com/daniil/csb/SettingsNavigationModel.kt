package com.daniil.csb

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun findScreenById(id: String): ScreenInstance? {
        return screenHeap.value.find { it.id == id }
    }

    fun goToScreen(screenInstance: ScreenInstance) {
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