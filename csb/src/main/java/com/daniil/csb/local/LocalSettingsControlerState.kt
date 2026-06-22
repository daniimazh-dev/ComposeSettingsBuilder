package com.daniil.csb.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniil.csb.classes.utils.SettingBuilder

@Composable
fun rememberLocalSettingsController(localScreenBuilder: LocalScreenBuilderScope.() -> Unit): LocalSettingsController {
    return remember { LocalSettingsController(localScreenBuilder) }
}

