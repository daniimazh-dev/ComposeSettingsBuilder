package com.daniil.csb.screens

import com.daniil.csb.CsbDslMarkers

@CsbDslMarkers
open class ScreenBuilder(): ScreenDslInterface {
    internal val screenHeap: MutableList<Screen> = mutableListOf()
    override fun Screen.register() {
        screenHeap.add(this)
    }
}