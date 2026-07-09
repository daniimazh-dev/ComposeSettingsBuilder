package com.daniil.csb.screens

import com.daniil.csb.settings.utils.SettingBuilder

interface ScreenDslInterface {
    fun createScreen(
        id: String,
        vararg screenAttribute: ScreenAttribute,
        scope: CreateScreenScope.() -> Unit
    ): Screen {
        val data = CreateScreenScope().apply(scope)
        val screen = Screen.Builder(id)
            .setTitle(data.title)
            .setModifier(data.modifier)
            .setPaddingValues(data.paddingValues)
            .setGroupedContent(data.getData())
            .setOnCloseScreen(onCloseScreen = data.onCloseScreen)
            .setAttribute(screenAttribute.toList())
            .build()
        screen.register()
        return screen
    }
    fun createCustomScreen(
        id: String,
        vararg screenAttribute: ScreenAttribute,
        scope: CreateCustomScreenScope.() -> ContentConfiguredToken
    ): CustomScreen {
        val data = CreateCustomScreenScope()
        data.scope()
        val screen =
            CustomScreen.Builder(id).setTitle(data.title)
                .setModifier(data.modifier)
                .registerSettings(*data.settings.map { it.second }.toTypedArray())
                .setContent(data.content)
                .setOnCloseScreen(onCloseScreen = data.onCloseScreen)
                .setAttribute(screenAttribute.toList())
                .build()
        screen.register()
        return screen
    }

    fun createAbstractScreen(
        id: String,
        scope: CreateAbstractScreenScope.() -> Unit,
    ): AbstractScreen {
        val data = CreateAbstractScreenScope().apply(scope)
        val screen = AbstractScreen.Builder(id).setContent(*data.settings.map { it.second }.toTypedArray()).build()
        screen.register()
        return screen
    }


    fun Screen.register()
}