package com.daniil.csb.screens

import com.daniil.csb.classes.SettingsSealed

open class ScreenInstance() {
    lateinit var id: String
    lateinit var title: String
    open lateinit var settings: Map<Group, List<SettingsSealed<*>>>
    constructor(
        id: String,
        title: String = "Example screen",
        settings: Map<Group, List<SettingsSealed<*>>>,

        ): this() {
        this.id = id
        this.title = title
        this.settings = settings
    }

    class SettingsContentScope() {
        val groups = mutableMapOf<Group, List<SettingsSealed<*>>>()

        fun newGroup(
            id: String,
            name: String,
            hide: Boolean = false,
            vararg settings: SettingsSealed<*>
        ) = apply {
            groups[Group(id, name, hide)] = settings.toList()
        }
    }

    class Group(
        val id: String,
        val name: String,
        val hide: Boolean = false
    )
    
    class Builder(
        val id: String
    ) {
        private var title: String = id
        private lateinit var settings: Map<Group, List<SettingsSealed<*>>>
        fun setTitle(title: String) = apply { this.title = title }
        fun setContent(vararg settings: SettingsSealed<*>) = apply {
            this.settings = mapOf(Group(id, title, true) to settings.toList())
        }
        fun setGroupedContent(scope: SettingsContentScope.() -> Unit) = apply {
            val settings = SettingsContentScope().apply(scope)
            this.settings = settings.groups
        }
        fun build() = ScreenInstance(id, title, settings)
    }
}