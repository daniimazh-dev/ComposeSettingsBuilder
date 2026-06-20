package com.daniil.csb.classes.utils

import com.daniil.csb.CSB
import com.daniil.csb.SettingsProvider
import com.daniil.csb.screens.ScreenInstance

/**
 * Helper class to build the settings structure.
 */
class SettingBuilder {
    
    /**
     * Builds the settings screens and loads saved data.
     * Uses the global CSB navigation model and context.
     */
    suspend fun build(
        vararg screens: ScreenInstance
    ) {
        CSB.navigationModel.setScreensHeap(*screens)
        SettingsProvider.loadData(CSB.context)
    }

    /**
     * Overload for list of screens.
     */
    suspend fun build(
        screens: List<ScreenInstance>
    ) {
        CSB.navigationModel.setScreensHeap(*screens.toTypedArray())
        SettingsProvider.loadData(CSB.context)
    }
}
