package com.daniil.csb.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniil.csb.classes.SettingsSealed
import com.daniil.csb.classes.utils.ItemGroupPosition
import com.daniil.csb.screens.CustomScreen.CustomScreenScope

open class CustomScreen() : ScreenInstance() {

    constructor(
        id: String,
        title: String = "Custom Screen",
        settings: List<SettingsSealed<*>>,
        content: @Composable CustomScreenScope.() -> Unit
    ): this() {
        this.id = id
        this.title = title
        this.registeredSettings = settings.toMutableList()
        this.content = content
    }


    var registeredSettings = mutableListOf<SettingsSealed<*>>()
    override var settings: Map<Group, List<SettingsSealed<*>>>
        get() = mapOf(ScreenInstance.Group("", "", true) to registeredSettings)
        set(value) {}

    var content: @Composable CustomScreenScope.() -> Unit = {}

    inner class CustomScreenScope {
        @Composable
        fun RenderSetting(index: Int) {
            val setting = registeredSettings.getOrNull(index)
            setting?.UI(this@CustomScreen, ItemGroupPosition.None)
        }

        @Composable
        fun RenderSetting(setting: SettingsSealed<*>) {
            setting.UI(this@CustomScreen, ItemGroupPosition.None)
        }

        @Composable
        fun RenderSetting(id: String) {
            registeredSettings.find { it.id == id }?.UI(this@CustomScreen, ItemGroupPosition.None)
        }
    }
    class Builder(val id: String) {
        private val builderSettings = mutableListOf<SettingsSealed<*>>()
        private lateinit var content: @Composable CustomScreenScope.() -> Unit
        var title = "Custom screen"

        fun registerSettings(vararg items: SettingsSealed<*>) = apply {
            this.builderSettings.addAll(items)
        }
        fun setTitle(title: String) = apply { this.title = title }

        fun setContent(content: @Composable CustomScreenScope.() -> Unit) = apply {
            this.content = content
        }

        fun build(): CustomScreen {
            val instance = CustomScreen(id, title, builderSettings, content)
            return instance
        }
    }

    @Composable
    fun Render() {
        val scope = remember { CustomScreenScope() }
        scope.content()
    }


}

class CreateCustomScreenScope() {

    var title: String = "Screen"
    val registeredSettings = mutableListOf<SettingsSealed<*>>()
    lateinit var content: @Composable CustomScreenScope.() -> Unit

    fun register(vararg settings: SettingsSealed<*>) {
        this.registeredSettings.addAll(settings)
    }
}

fun createCustomScreen(
    id: String,
    scope: CreateCustomScreenScope.() -> Unit
): CustomScreen {
    val data = CreateCustomScreenScope().apply(scope)

    val screen =
        CustomScreen.Builder(id).setTitle(data.title)
            .registerSettings(*data.registeredSettings.toTypedArray())
            .setContent(data.content).build()
    return screen
}