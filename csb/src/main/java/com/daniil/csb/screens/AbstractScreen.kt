package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder

class AbstractScreen
internal constructor(
    id: String,
    val abstractSettings: List<ComposeSetting<*>>
) : Screen(id, id, Modifier, PaddingValues.Zero) {

    override val settings: List<GroupSealed>
        get() = listOf(Group(id, null,false, abstractSettings))

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
class CreateAbstractScreenScope(): SettingBuilder() {}

