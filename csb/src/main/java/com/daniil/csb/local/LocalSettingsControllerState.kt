package com.daniil.csb.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniil.csb.screens.ContentConfiguredToken
import com.daniil.csb.settings.ComposableComponent

@Composable
fun rememberCustomLocalSettingsController(
    localSave: LocalSave? = null,
    localScreenBuilder: LocalCustomScreenBuilderScope.() -> ContentConfiguredToken
): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setCustomScreen(localScreenBuilder)
    localSave?.also { localSettingsController.loadLocalSave(localSave) }
    setGlobalProvider(localSettingsController)
    return localSettingsController
}

@Composable
fun rememberLocalSettingsController(
    localSave: LocalSave? = null,
    localScreenBuilder: LocalScreenBuilderScope.() -> Unit
): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setScreen(localScreenBuilder)
    localSave?.also { localSettingsController.loadLocalSave(localSave) }
    setGlobalProvider(localSettingsController)
    return localSettingsController
}

@Composable
fun rememberLocalSettingsController(): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setEmptyScreen()
    setGlobalProvider(localSettingsController)
    return localSettingsController
}

private fun setGlobalProvider(localSettingsController: LocalSettingsController) {
    localSettingsController.getAllSettings().filterIsInstance<ComposableComponent>()
        .forEach {
            it.setGlobalProvider { id ->
                val setting = localSettingsController.findSettingById(id)
                if (setting is ComposableComponent) error(
                    """
                        Cannot call ComposableComponent (id: "$id") recursively, 
                        otherwise there will be StackOverflow error
                    """.trimIndent()
                )
                setting
            }
        }
}