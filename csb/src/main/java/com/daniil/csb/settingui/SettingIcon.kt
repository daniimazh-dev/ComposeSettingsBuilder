package com.daniil.csb.settingui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class SettingIcon(
    val content: @Composable () -> Unit
) {
    companion object {
        fun fromRes(@DrawableRes res: Int, tint: Color = Color.Unspecified, contentDescription: String? = null): SettingIcon {
            return SettingIcon { LocalSettingsStyle.current.SettingIcon(res, tint, contentDescription) }
        }
    }

}