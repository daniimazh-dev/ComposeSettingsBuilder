package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.AbstractGroup
import com.daniil.csb.group.Group
import com.daniil.csb.group.GroupSealed
import com.daniil.csb.isInFlag
import com.daniil.csb.screens.title.ScreenTitle
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.SettingBuilder

class AbstractScreen
internal constructor(
    id: String,
    val abstractSettings: List<ComposeSetting<*>>
) : Screen(id, ScreenTitle.setText(id), Modifier, PaddingValues.Zero) {

    override val settings: List<GroupSealed>
        get() = if ("allowDisplayAbstractScreen".isInFlag())
            listOf(Group(id, null, true, abstractSettings))
        else listOf(AbstractGroup(id, abstractSettings))

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
class AbstractScreenBuilderScope(): SettingBuilder()

