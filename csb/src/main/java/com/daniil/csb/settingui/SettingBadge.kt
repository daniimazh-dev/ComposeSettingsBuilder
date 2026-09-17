package com.daniil.csb.settingui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
class SettingBadge(
    val content: @Composable () -> Unit
) {
    companion object {
        fun chip(text: String) = SettingBadge(
            content = {
                val style = LocalSettingsStyle.current
                val translator = LocalCSBTranslator.current
                Box(
                    modifier = Modifier
                        .clip(style.edgeGroupShape)
                        .background(style.activeColor)
                ) {
                    CompositionLocalProvider(LocalTextStyle provides style.labelStyle) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            text = translator.translate(text)
                        )
                    }
                }
            }
        )
        fun point(size: Dp = 8.dp) = SettingBadge(
            content = {
                val style = LocalSettingsStyle.current
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(style.activeColor)
                )
            }
        )

        fun icon(@DrawableRes res: Int, tint: Color = Color.Unspecified) = SettingBadge(
            content = {
                Box(
                    modifier = Modifier
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(res),
                        contentDescription = null,
                        tint = if (tint == Color.Unspecified) LocalContentColor.current else tint
                    )
                }
            }
        )
    }
}