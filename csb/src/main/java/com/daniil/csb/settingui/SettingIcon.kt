package com.daniil.csb.settingui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

class SettingIcon internal  constructor(
    val content: @Composable () -> Unit
) {
    companion object {
        fun fromRes(@DrawableRes res: Int, tint: Color = Color.Unspecified): SettingIcon {
            return SettingIcon {
                Icon(
                    painter = painterResource(res),
                    contentDescription = null,
                    tint = if (tint == Color.Unspecified) LocalContentColor.current else tint
                )
            }
        }
        fun custom(content: @Composable () -> Unit): SettingIcon {
            return SettingIcon(content = content)
        }
    }

}