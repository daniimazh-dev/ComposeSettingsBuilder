package com.daniil.csb.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniil.csb.screens.ContentConfiguredToken

@Composable
fun rememberCustomLocalSettingsController(
    localSave: LocalSave? = null,
    localScreenBuilder: LocalCustomScreenBuilderScope.() -> ContentConfiguredToken
): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setCustomScreen(localScreenBuilder)
    localSave?.let { localSettingsController.loadLocalSave(localSave) }
    return localSettingsController
}

@Composable
fun rememberLocalSettingsController(
    localSave: LocalSave? = null,
    localScreenBuilder: LocalScreenBuilderScope.() -> Unit
): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setScreen(localScreenBuilder)
    localSave?.let { localSettingsController.loadLocalSave(localSave) }
    return localSettingsController
}

@Composable
fun rememberLocalSettingsController(): LocalSettingsController {
    val localSettingsController = remember { LocalSettingsController() }
    localSettingsController.setEmptyScreen()
    return localSettingsController
}