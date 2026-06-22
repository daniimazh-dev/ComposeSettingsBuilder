package com.daniil.csb.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniil.csb.classes.utils.SettingBuilder

@Composable
fun rememberLocalSettingsController(
    builder: SettingBuilder = SettingBuilder(),
): LocalSettingsController {
    return remember { LocalSettingsController(builder) }
}

