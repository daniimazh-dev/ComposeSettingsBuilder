package com.daniil.csb.screens

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.AbstractGroup
import com.daniil.csb.group.Group
import com.daniil.csb.isInFlag
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.SettingBuilder

class AbstractScreen internal constructor(
    id: String,
) : Screen(id) {
    internal constructor(
        id: String,
        abstractSettings: List<ComposeSetting<*>>
    ): this(id) {
        settings = if ("allowDisplayAbstractScreen".isInFlag())
            listOf(Group(id, null, true, abstractSettings))
        else listOf(AbstractGroup(id, abstractSettings))
    }

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
@CsbDslMarkers
class AbstractScreenBuilderScope internal constructor(): SettingBuilder()

