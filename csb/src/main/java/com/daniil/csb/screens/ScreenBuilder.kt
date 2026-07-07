package com.daniil.csb.screens

import com.daniil.csb.CsbDslMarkers

@CsbDslMarkers
open class ScreenBuilder() {
    internal val screenHeap: MutableList<Screen> = mutableListOf()
    internal fun Screen.register() {
        screenHeap.add(this)
    }
}