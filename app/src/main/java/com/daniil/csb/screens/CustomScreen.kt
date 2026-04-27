package com.daniil.csb.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.daniil.csb.classes.SettingsSealed

open class CustomScreen(): ScreenInstance() {
    lateinit var content: @Composable () -> Unit
    override var settings: Map<Group, List<SettingsSealed<*>>> = mapOf()

    constructor(
        id: String,
        title: String = "Custom Screen",
        content: @Composable () -> Unit
        ): this() {
        this.id = id
        this.title = title
        this.content = content
    }

    class Builder(
        val id: String
    ) {
        private var title: String = id
        private lateinit var content: @Composable () -> Unit
        fun setTitle(title: String) = apply { this.title = title }
        fun setContent(content: @Composable () -> Unit) = apply { this.content = content }
        fun build() = CustomScreen(id, title, content)
    }
}