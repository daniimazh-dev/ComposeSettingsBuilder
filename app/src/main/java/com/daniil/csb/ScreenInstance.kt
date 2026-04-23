package com.daniil.csb

import com.daniil.csb.classes.SettingInterface
import com.daniil.csb.classes.SettingsSealed


open class ScreenInstance() {
    lateinit var id: String
    lateinit var title: String
    lateinit var settings: List<SettingsSealed<*>>
    constructor(
        id: String,
        title: String = "Example screen",
        settings: List<SettingsSealed<*>>,

        ): this() {
        this.id = id
        this.title = title
        this.settings = settings
    }


    class Builder(
        val id: String
    ) {
        private var title: String = id
        private lateinit var settings: List<SettingsSealed<*>>
        fun setTitle(title: String) = apply { this.title = title }
        fun setContent(vararg settings: SettingsSealed<*>) = apply { this.settings = settings.toList() }
        fun build() = ScreenInstance(id, title, settings)
    }
}
