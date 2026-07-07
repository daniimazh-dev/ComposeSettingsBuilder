package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder

class AbstractScreen
internal constructor(
    id: String,
    val abstractSettings: List<ComposeSetting<*>>
) : Screen(id, id, Modifier, PaddingValues.Zero) {

    override var settings: Map<Group, List<ComposeSetting<*>>>
        get() = mapOf(Screen.Group(id, null,false) to abstractSettings)
        set(value) {}

    class Builder(
        val id: String
    ) {
        private lateinit var settings: List<ComposeSetting<*>>
        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = settings.toList()
        }
        fun build() = AbstractScreen(id, settings)
    }
}


fun ScreenBuilder.createAbstractScreen(
    id: String,
    settingsBuilderScope: SettingBuilder.() -> Unit,
): AbstractScreen {
    val data = SettingBuilder().apply(settingsBuilderScope)
    val screen = AbstractScreen.Builder(id).setContent(*data.settings.map { it.second }.toTypedArray()).build()
    screen.register()
    return screen
}
