package com.daniil.csb.settingui.styles

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle

data class SettingsStyle(
    val backgroundColor: Color = Color.Transparent,
    val focusColor: Color = Color.Transparent,
    val titleStyle: TextStyle = TextStyle.Default,
    val descriptionStyle: TextStyle = TextStyle.Default,
    val containerShape: Shape = RoundedCornerShape(12.dp),
    val groupCornerShape: Dp = 4.dp,
    val horizontalPadding: Dp = 16.dp,
    val verticalPadding: Dp = 12.dp,
    val minHeight: Dp = 52.dp,
    val itemSpacing: Dp = 8.dp,
    val cardElevation: Dp = 2.dp,
)

val LocalSettingsStyle = staticCompositionLocalOf { SettingsStyle() }

