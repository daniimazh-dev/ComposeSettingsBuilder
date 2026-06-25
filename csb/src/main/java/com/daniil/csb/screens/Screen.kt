package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.classes.utils.ScreenBuilder
import com.daniil.csb.classes.utils.SettingBuilder
import java.util.UUID

open class Screen internal constructor(
    open var id: String,
    open var title: String? = id,
    open var modifier: Modifier,
    open var paddingValues: PaddingValues,
    open var attribute: List<ScreenAttribute>? = null,
    open val settings: Map<Group, List<ComposeSetting<*>>> = emptyMap(),
) {
    internal val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }
    class SettingsContentScope() {
        val groups = mutableMapOf<Group, List<ComposeSetting<*>>>()
        fun newGroup(
            id: String,
            hide: Boolean = false,
            groupScope: GroupScope.() -> Unit,
        ) = apply {
            val data = GroupScope().apply(groupScope)
            groups[Group(id, hide)] = data.settings
        }
    }

    class Group(
        val id: String,
        var _hide: Boolean = false
    ) {
        var hide by mutableStateOf(_hide)
        fun hide() { hide = true }
        fun show() { hide = false }
    }

    class Builder(val id: String) {
        private var title: String? = id
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private var attribute: List<ScreenAttribute>? = null
        private lateinit var settings: Map<Group, List<ComposeSetting<*>>>


        fun setTitle(title: String?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = mapOf(Group(id) to settings.toList())
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) = apply { this.paddingValues = paddingValues }

        fun setAttribute(screenAttribute: List<ScreenAttribute>?) = apply { this.attribute = screenAttribute }

        fun setGroupedContent(scope: SettingsContentScope.() -> Unit) = apply {
            val settings = SettingsContentScope().apply(scope)
            this.settings = settings.groups
        }

        fun setGroupedContent(settings: Map<Group, List<ComposeSetting<*>>>) = apply {
            this.settings = settings
        }

        fun build() = Screen(id, title, modifier ?: Modifier, paddingValues ?: PaddingValues.Zero, attribute, settings)
    }
}



class CreateScreenScope() {
    private val groups: MutableMap<Screen.Group, List<ComposeSetting<*>>> = mutableMapOf()
    var title: String? = null
    var modifier: Modifier? = null

    var innerPadding = PaddingValues.Zero
    var paddingValues: PaddingValues? = null

    private var groupContent = Screen.SettingsContentScope()

    fun group(
        id: String,
        hide: Boolean,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        groups[Screen.Group(id,  hide)] = data.settings
    }

    fun group(
        hide: Boolean,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        groups[Screen.Group(UUID.randomUUID().toString(),hide)] = data.settings
    }

    fun group(
        id: String,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        groups[Screen.Group(id)] = data.settings
    }


    fun group(
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        groups[Screen.Group(UUID.randomUUID().toString())] = data.settings
    }

    internal fun getData(): Screen.SettingsContentScope {
        groupContent.groups.putAll(groups)
        return groupContent
    }
}

class GroupScope(): SettingBuilder()

fun ScreenBuilder.createScreen(
    id: String,
    screenAttribute: List<ScreenAttribute>? = null,
    scope: CreateScreenScope.() -> Unit
): Screen {
    val data = CreateScreenScope().apply(scope)
    val content = data.getData()
    val screen = Screen.Builder(id)
        .setTitle(data.title)
        .setModifier(data.modifier)
        .setPaddingValues(data.paddingValues)
        .setGroupedContent(content.groups)
        .setAttribute(screenAttribute)
        .build()
    screen.addToHeap()
    return screen
}
