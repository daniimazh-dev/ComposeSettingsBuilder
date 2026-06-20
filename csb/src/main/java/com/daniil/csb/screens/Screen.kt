package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.classes.utils.SettingBuilder
import java.util.UUID

open class Screen internal constructor(
    var id: String,
    var title: String? = null,
    var modifier: Modifier? = null,

    var paddingValues: PaddingValues? = null,
    open val settings: Map<Group, List<ComposeSetting<*>>> = emptyMap(),
) {
    internal val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }
    class SettingsContentScope() {
        val groups = mutableMapOf<Group, List<ComposeSetting<*>>>()
        fun newGroup(
            id: String,
            name: String,
            hide: Boolean = false,
            vararg settings: ComposeSetting<*>
        ) = apply { groups[Group(id, name, hide)] = settings.toList() }
    }

    class Group(
        val id: String,
        val name: String,
        val hide: Boolean = false
    )

    class Builder(val id: String) {
        private var title: String? = id
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private lateinit var settings: Map<Group, List<ComposeSetting<*>>>


        fun setTitle(title: String?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = mapOf(Group(id, title ?: id, true) to settings.toList())
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) = apply { this.paddingValues = paddingValues }

        fun setGroupedContent(scope: SettingsContentScope.() -> Unit) = apply {
            val settings = SettingsContentScope().apply(scope)
            this.settings = settings.groups
        }

        fun setGroupedContent(settings: Map<Group, List<ComposeSetting<*>>>) = apply {
            this.settings = settings
        }

        fun build() = Screen(id, title, modifier, paddingValues, settings)
    }
}



class CreateScreenScope() {
    private val groups: MutableMap<Screen.Group, List<ComposeSetting<*>>> = mutableMapOf()
    var title: String? = null
    var modifier: Modifier? = null

    var innerPadding = PaddingValues.Zero
    var paddingValues: PaddingValues? = null

    private var groupContent = Screen.SettingsContentScope()

    fun newGroup(
        id: String,
        name: String,
        vararg settings: ComposeSetting<*>
    ) {
        groups[Screen.Group(id, name)] = settings.toList()
    }

    fun newGroup(
        vararg settings: ComposeSetting<*>
    ) {
        groups[Screen.Group(UUID.randomUUID().toString(), "", true)] = settings.toList()
    }

    internal fun getData(): Screen.SettingsContentScope {
        groupContent.groups.putAll(groups)
        return groupContent
    }
}

fun SettingBuilder.createScreen(
    id: String,
    scope: CreateScreenScope.() -> Unit
): Screen {
    val data = CreateScreenScope().apply(scope)
    val content = data.getData()
    val screen = Screen.Builder(id)
        .setTitle(data.title)
        .setModifier(data.modifier)
        .setPaddingValues(data.paddingValues)
        .setGroupedContent(content.groups)
        .build()
    return screen
}
