package com.daniil.csb.local

import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.screens.ContentConfiguredToken
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.Screen
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

open class LocalSettingsController() {
    internal lateinit var screen: Screen

    fun setCustomScreen(localCustomScreenBuilderScope: LocalCustomScreenBuilderScope.() -> ContentConfiguredToken) {
        screen = createCustomLocalScreen(
            id = UUID.randomUUID().toString(),
            scope = localCustomScreenBuilderScope
        )
    }

    fun setScreen(localScreenBuilderScope: LocalScreenBuilderScope.() -> Unit) {
        screen = createLocalScreen(
            id = UUID.randomUUID().toString(),
            scope = localScreenBuilderScope
        )
    }

    fun setEmptyScreen() {
        screen = createLocalScreen(
            id = UUID.randomUUID().toString(),
            scope = {}
        )
    }

    private fun createLocalScreen(
        id: String,
        scope: LocalScreenBuilderScope.() -> Unit
    ): Screen {
        val data = LocalScreenBuilderScope(this).apply(scope)
        val screen = Screen.Builder(id)
            .setGroupedContent(data.getData())
            .build()
        return screen
    }

    private fun createCustomLocalScreen(
        id: String,
        scope: LocalCustomScreenBuilderScope.() -> ContentConfiguredToken
    ): CustomScreen {
        val data = LocalCustomScreenBuilderScope(this)
        data.scope()

        val screen = CustomScreen.Builder(id)
            .registerSettings(*data.settings.toTypedArray())
            .setContent(data.content).build()
        return screen
    }

    fun findById(id: String): ComposeSetting<*> {
        return screen.settingsScreenModel.findSettingById(id)
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
    fun resetAllToDefault(vararg ignoreId: String) {
        getAllSettings().forEach { if (it.id !in ignoreId) it.resetToDefault()}
    }

    fun getAllSettings() = screen.settingsScreenModel.settings.value.flatMap { it.settings }

    fun generateLocalSave(): LocalSave {
        val packages =
            getAllSettings().map { it.saveLogic() }
        return LocalSave(packages)
    }

    fun loadLocalSave(localSave: LocalSave) {
        val packages = localSave.savePackages
        packages.filterNotNull().forEach { pack ->
            try {
                val setting = findById(pack.id)
                setting.loadLogic(pack)
            } catch (_: Exception) {
            }
        }
    }

    fun focusToSetting(id: String) {
        screen.settingsScreenModel.focusToSetting(id)
    }

    fun hideGroup(groupId: String, isHide: Boolean) {
        screen.settingsScreenModel.hideGroup(groupId, isHide)
    }

    fun disableGroup(groupId: String, isHide: Boolean) {
        screen.settingsScreenModel.disableGroup(groupId, isHide)
    }
}