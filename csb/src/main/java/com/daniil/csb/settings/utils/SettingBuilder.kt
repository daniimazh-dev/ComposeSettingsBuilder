package com.daniil.csb.settings.utils

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.screens.ScreenBuilder

@CsbDslMarkers
open class SettingBuilder: SettingDslInterface {
    val settings: MutableList<Pair<String?, ComposeSetting<*>>> = mutableListOf()
    internal  fun <T : ComposeSetting<*>> addToHeap(setting: T, groupId: String? = null): SettingToken<T> {
        settings.add(groupId to setting)
        return SettingToken(setting)
    }

    override fun <T: ComposeSetting<*>> T.register(): SettingToken<T> {
        return addToHeap(this)
    }
}
