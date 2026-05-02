package com.daniil.csb.screens

import androidx.compose.ui.Modifier
import com.daniil.csb.classes.ComposeSetting
import java.util.UUID

open class ScreenInstance internal constructor(
    var id: String,
    var title: String? = null,
    var modifier: Modifier? = null,
    open val settings: Map<Group, List<ComposeSetting<*>>> = emptyMap(),
) {
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
        private lateinit var settings: Map<Group, List<ComposeSetting<*>>>

        fun setTitle(title: String?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = mapOf(Group(id, title ?: id, true) to settings.toList())
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }

        fun setGroupedContent(scope: SettingsContentScope.() -> Unit) = apply {
            val settings = SettingsContentScope().apply(scope)
            this.settings = settings.groups
        }

        fun setGroupedContent(settings: Map<Group, List<ComposeSetting<*>>>) = apply {
            this.settings = settings
        }

        fun build() = ScreenInstance(id, title, modifier, settings)
    }
}

class CreateScreenScope() {
    private val groups: MutableMap<ScreenInstance.Group, List<ComposeSetting<*>>> = mutableMapOf()
    var title: String? = null
    var modifier: Modifier? = null
    private var groupContent = ScreenInstance.SettingsContentScope()

    fun newGroup(
        id: String,
        name: String,
        vararg settings: ComposeSetting<*>
    ) {
        groups[ScreenInstance.Group(id, name)] = settings.toList()
    }

    fun newGroup(
        vararg settings: ComposeSetting<*>
    ) {
        groups[ScreenInstance.Group(UUID.randomUUID().toString(), "", true)] = settings.toList()
    }

    internal fun getData(): ScreenInstance.SettingsContentScope {
        groupContent.groups.putAll(groups)
        return groupContent
    }
}

fun createScreen(
    id: String,
    scope: CreateScreenScope.() -> Unit
): ScreenInstance {
    val data = CreateScreenScope().apply(scope)
    val content = data.getData()
    val screen = ScreenInstance.Builder(id)
        .setTitle(data.title)
        .setModifier(data.modifier)
        .setGroupedContent(content.groups)
        .build()
    return screen
}
