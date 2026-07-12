package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

open class Screen internal constructor(
    open var id: String,
    open var title: String? = id,
    open var modifier: Modifier,
    open var paddingValues: PaddingValues,
    open var attribute: List<ScreenAttribute>? = null,
    open val settings: List<GroupSealed> = emptyList(),
    open val onCloseScreen: () -> Unit = {},
) {
    internal open val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }

    class Builder(val id: String) {
        private var title: String? = id
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private var attribute: List<ScreenAttribute>? = null
        private lateinit var settings: List<GroupSealed>
        private var onCloseScreen: () -> Unit = {}

        fun setTitle(title: String?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = listOf(Group(id, settings = settings.toList()))
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) =
            apply { this.paddingValues = paddingValues }

        fun setAttribute(screenAttribute: List<ScreenAttribute>?) =
            apply { this.attribute = screenAttribute }

        fun setOnCloseScreen(onCloseScreen: () -> Unit) =
            apply { this.onCloseScreen = onCloseScreen }

        fun setGroupedContent(settings: List<GroupSealed>) = apply { this.settings = settings }

        fun build() = Screen(
            id,
            title,
            modifier ?: Modifier,
            paddingValues ?: PaddingValues.Zero,
            attribute,
            settings
        )
    }
}


class CreateScreenScope() : ScreenBuilderScope() {
    var title: String? = null
    var modifier: Modifier? = Modifier
    var paddingValues: PaddingValues? = null
    var onCloseScreen: () -> Unit = {}
}

class ScreenController internal constructor(
    private val screen: Screen
) {
    val id = screen.id
    fun resetToDefault() {  screen.settings.flatMap { it.settings }.forEach { it.resetToDefault() } }
}

