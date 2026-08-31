package com.daniil.csb.screens

interface ScreenDslInterface {
    fun createScreen(
        id: String,
        vararg screenAttribute: ScreenAttribute,
        scope: ScreenBuilderScope.() -> Unit
    ): Screen {
        val data = ScreenBuilderScope(id).apply(scope)
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
        scope: CustomBuilderScreenScope.() -> ContentConfiguredToken
    ): CustomScreen {
        val data = CustomBuilderScreenScope(id)
        data.scope()
        val screen =
            CustomScreen.Builder(id).setTitle(data.title)
                .setModifier(data.modifier)
                .registerSettings(*data.settings.toTypedArray())
                .setContent(data.content)
                .setOnCloseScreen(onCloseScreen = data.onCloseScreen)
                .setAttribute(screenAttribute.toList())
                .build()
        screen.register()
        return screen
    }

    fun createAbstractScreen(
        id: String,
        scope: AbstractScreenBuilderScope.() -> Unit,
    ): AbstractScreen {
        val data = AbstractScreenBuilderScope().apply(scope)
        val screen = AbstractScreen.Builder(id).setContent(*data.settings.toTypedArray()).build()
        screen.register()
        return screen
    }


    fun Screen.register()
}