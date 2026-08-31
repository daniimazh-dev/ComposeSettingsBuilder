package com.daniil.csb.screens

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.TranslatableScope

@CsbDslMarkers
open class ScreenBuilder(): ScreenDslInterface, TranslatableScope {
    internal val screenHeap: MutableList<Screen> = mutableListOf()
    override fun Screen.register() {
        screenHeap.add(this)
    }
}