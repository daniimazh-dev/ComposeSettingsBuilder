package com.daniil.csb.screens

class ScreenController internal constructor(
    private val screen: Screen
) {
    val id = screen.id
    fun resetToDefault(vararg ignoreId: String) {
        getSettings().forEach { if (it.id !in ignoreId) it.resetToDefault() }
    }
    fun disableAll(state: Boolean) {  getSettings().forEach { it.enabled(state) } }
    fun getSettings() = screen.settings.flatMap { it.settings }
}

