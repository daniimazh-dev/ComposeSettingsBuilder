package com.daniil.csb.local

import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.screens.CreateCustomScreenScope
import com.daniil.csb.screens.CustomScreen
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class LocalScreenBuilderScope(
    val localController: LocalSettingsController
): CreateCustomScreenScope()
open class LocalSettingsController(
    localScreenBuilder: LocalScreenBuilderScope.() -> Unit
) {

    var customScreen: CustomScreen
    init {
        customScreen = createLocalScreen(id = UUID.randomUUID().toString(), scope = localScreenBuilder)
    }
    private fun createLocalScreen(
        id: String,
        scope: LocalScreenBuilderScope.() -> Unit
    ): CustomScreen {
        val data = LocalScreenBuilderScope(this).apply(scope)

        val screen = CustomScreen.Builder(id).setTitle(data.title)
                .setModifier(data.modifier)
                .registerSettings(*data.registeredSettings.toTypedArray())
                .setContent(data.content).build()
        return screen
    }

    fun findById(id: String): ComposeSetting<*> {
        return customScreen.settingsScreenModel.findSettingById(id)
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
        val target = setting as? ComposeSetting<T>
        target?.changeValue(newValue) ?: error("Type mismatch $id")
    }

    fun enable(id: String, state: Boolean) {
        val setting = findById(id)
        setting.enabled(state)
    }

    fun resetToDefault(id: String) {
        val setting = findById(id)
        setting.resetToDefault()
    }

    fun saveState(id: String, state: Boolean) {
        val setting = findById(id)
        if (state) setting.saveOn() else setting.saveOff()
    }

    fun focusToSetting(id: String) {
        customScreen.settingsScreenModel.focusToSetting(id)
    }

    fun hideGroup(id: String, hide: Boolean) {
        customScreen.settingsScreenModel.hideGroup(id, hide)
    }
}