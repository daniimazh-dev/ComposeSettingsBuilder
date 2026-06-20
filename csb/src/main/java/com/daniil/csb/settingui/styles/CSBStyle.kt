package com.daniil.csb.settingui.styles

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@SuppressLint("ComposableNaming")
object CSBStyle {
    // Default
    @Composable
    fun Material3() = SettingsStyle(
        titleStyle = MaterialTheme.typography.titleMedium,
        descriptionStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.outline
        ),
        containerShape = MaterialTheme.shapes.medium,
        groupCornerShape = 4.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        focusColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        horizontalPadding = 16.dp,
        verticalPadding = 12.dp,
        minHeight = 52.dp,
        itemSpacing = 8.dp,
        cardElevation = 2.dp,
    )

    // Without material theme
    fun Unspecified(isDarkTheme: Boolean) = SettingsStyle(
        titleStyle = TextStyle.Default.copy(
            fontSize = 16.sp
        ),
        descriptionStyle = TextStyle.Default.copy(
            fontSize = 12.sp,
            color = Color.Gray
        ),
        containerShape = RoundedCornerShape(12.dp),
        groupCornerShape = 4.dp,
        backgroundColor = if (isDarkTheme) Color.Gray.copy(alpha = 0.1f) else Color.White,
        focusColor = Color.Gray.copy(alpha = 0.4f),
        horizontalPadding = 16.dp,
        verticalPadding = 12.dp,
        minHeight = 52.dp,
        itemSpacing = 8.dp,
        cardElevation = 0.dp,
    )
}