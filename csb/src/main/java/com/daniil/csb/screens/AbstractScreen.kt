package com.daniil.csb.screens

import com.daniil.csb.classes.SettingsSealed

class AbstractScreen(): ScreenInstance() {
    constructor(
        id: String,
        settings: Map<Group, List<SettingsSealed<*>>>,

        ): this() {
        this.id = id
        this.settings = settings
    }

    class Builder(
        val id: String
    ) {
        private lateinit var settings: Map<Group, List<SettingsSealed<*>>>
        fun setContent(vararg settings: SettingsSealed<*>) = apply {
            this.settings = mapOf(Group(id, "abstract_$id", true) to settings.toList())
        }
        fun build() = AbstractScreen(id, settings)
    }
}


fun createAbstractScreen(
    id: String,
    vararg settings: SettingsSealed<*>
): AbstractScreen {
    val screen = AbstractScreen.Builder(id).setContent(*settings).build()
    return screen
}
