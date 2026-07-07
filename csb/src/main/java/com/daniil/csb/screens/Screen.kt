package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder
open class Screen internal constructor(
    open var id: String,
    open var title: String? = id,
    open var modifier: Modifier,
    open var paddingValues: PaddingValues,
    open var attribute: List<ScreenAttribute>? = null,
    open val settings: Map<Group, List<ComposeSetting<*>>> = emptyMap(),
    open val onCloseScreen: () -> Unit = {},
) {
    internal val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }

    class Group(
        val id: String,
        var groupTitle: GroupTitle? = null,
        val isHide: Boolean = false
    ) {
        var hide by mutableStateOf(isHide)
        fun hide() { hide = true }
        fun show() { hide = false }
    }

    class Builder(val id: String) {
        private var title: String? = id
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private var attribute: List<ScreenAttribute>? = null
        private lateinit var settings: Map<Group, List<ComposeSetting<*>>>
        private var onCloseScreen: () -> Unit = {}

        fun setTitle(title: String?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = mapOf(Group(id) to settings.toList())
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) =
            apply { this.paddingValues = paddingValues }

        fun setAttribute(screenAttribute: List<ScreenAttribute>?) =
            apply { this.attribute = screenAttribute }

        fun setOnCloseScreen(onCloseScreen: () -> Unit) = apply { this.onCloseScreen = onCloseScreen  }
        fun setGroupedContent(settings: Map<Group, List<ComposeSetting<*>>>) =
            apply { this.settings = settings }

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

@CsbDslMarkers
class GroupScope() : SettingBuilder() {
    var groupTitle: GroupTitle? = DefaultGroupTitle
}

fun ScreenBuilder.createScreen(
    id: String,
    screenAttribute: List<ScreenAttribute>? = null,
    scope: CreateScreenScope.() -> Unit
): Screen {
    val data = CreateScreenScope().apply(scope)
    val screen = Screen.Builder(id)
        .setTitle(data.title)
        .setModifier(data.modifier)
        .setPaddingValues(data.paddingValues)
        .setGroupedContent(data.getData())
        .setOnCloseScreen(onCloseScreen = data.onCloseScreen)
        .setAttribute(screenAttribute)
        .build()
    screen.register()
    return screen
}
