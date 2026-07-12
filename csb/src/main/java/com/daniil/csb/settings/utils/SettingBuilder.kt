package com.daniil.csb.settings.utils

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.screens.ScreenBuilder

@CsbDslMarkers
open class SettingBuilder: SettingDslInterface {
    val settings: MutableList<ComposeSetting<*>> = mutableListOf()
    internal  fun <T : ComposeSetting<*>> addToHeap(setting: T): SettingToken<T> {
        settings.add(setting)
        return SettingToken(setting)
    }

    override fun <T: ComposeSetting<*>> T.register(): SettingToken<T> {
        return addToHeap(this)
    }
}
