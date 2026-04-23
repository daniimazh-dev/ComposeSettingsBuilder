package com.daniil.csb

import com.daniil.csb.classes.SettingsSealed
import kotlinx.coroutines.flow.StateFlow

object SettingsProvider {
    lateinit var navigationModel: SettingsNavigationModel
    fun innit(model: SettingsNavigationModel) {
        navigationModel = model
    }

    fun findById(id: String): SettingsSealed<*> {
        val setting = navigationModel.screenHeap.value
            .flatMap { it.settings }
            .find { it.id == id } ?: error("Setting $id not found")
        return setting
    }

    inline fun <reified T> getValue(id: String): StateFlow<T> {

        val setting = findById(id)
        if (setting.value.value is T) {
            @Suppress("UNCHECKED_CAST")
            return setting.value as StateFlow<T>
        }
        error("Type mismatch")
    }

    inline fun <reified T> setValue(id: String, newValue: T) {
        val setting = findById(id)

        @Suppress("UNCHECKED_CAST")
        val target = setting as? SettingsSealed<T>
        target?.changeValue(newValue) ?: error("Type mismatch $id")
    }

    fun enable(id: String, state: Boolean) {
        val setting = findById(id)
        setting.enabled(state)
    }
}