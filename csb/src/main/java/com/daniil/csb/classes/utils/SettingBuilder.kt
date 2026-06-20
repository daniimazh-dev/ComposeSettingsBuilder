package com.daniil.csb.classes.utils

import com.daniil.csb.CSB
import com.daniil.csb.screens.Screen


class SettingBuilder {
    suspend fun build(
        vararg screens: Screen
    ) {
        CSB.navigationModel.setScreensHeap(*screens)
        CSB.loadData()
    }
    suspend fun build(
        screens: List<Screen>
    ) {
        CSB.navigationModel.setScreensHeap(*screens.toTypedArray())
        CSB.loadData()
    }
}
