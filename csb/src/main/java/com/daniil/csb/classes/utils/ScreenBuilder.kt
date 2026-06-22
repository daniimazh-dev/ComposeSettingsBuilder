package com.daniil.csb.classes.utils

import com.daniil.csb.CSB
import com.daniil.csb.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class ScreenBuilder() {
    val screenHeap: MutableList<Screen> = mutableListOf()
    internal fun Screen.addToHeap() {
        screenHeap.add(this)
    }
}

fun registerSettingScreens(
    coroutineScope: CoroutineScope,
    screenBuilderScope: ScreenBuilder.() -> Unit
) {
    val data = ScreenBuilder().apply(screenBuilderScope)
    CSB.navigationModel.setScreensHeap(*data.screenHeap.toTypedArray())
    coroutineScope.launch { CSB.loadData() }
}

