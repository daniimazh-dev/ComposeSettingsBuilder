package com.daniil.csb.classes.utils

import com.daniil.csb.classes.ComposeSetting


open class SettingBuilder {
    val settings: MutableList<ComposeSetting<*>> = mutableListOf()
    fun ComposeSetting<*>.addToHeap() {
        settings.add(this)
    }
}
