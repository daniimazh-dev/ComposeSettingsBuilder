package com.daniil.csb.screens

import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.classes.utils.CSBCreator

class AbstractScreen
internal constructor(
    id: String,
    settings: Map<Group, List<ComposeSetting<*>>>,
) : ScreenInstance(id, settings = settings) {


    class Builder(
        val id: String
    ) {
        private lateinit var settings: Map<Group, List<ComposeSetting<*>>>
        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = mapOf(Group(id, "abstract_$id", true) to settings.toList())
        }

        fun build() = AbstractScreen(id, settings)
    }
}


fun CSBCreator.createAbstractScreen(
    id: String,
    vararg settings: ComposeSetting<*>
): AbstractScreen {
    val screen = AbstractScreen.Builder(id).setContent(*settings).build()
    return screen
}
