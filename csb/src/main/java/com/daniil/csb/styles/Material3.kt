package com.daniil.csb.styles

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/** Default for CSB */
@SuppressLint("ComposableNaming")
@Composable
fun CSBStyle.Material3(): SettingStyle {
    return object : DefaultSettingStyle() {
        override var backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer
        override var containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
        override var activeColor: Color = MaterialTheme.colorScheme.primary
        override var focusColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
        override var titleStyle: TextStyle = MaterialTheme.typography.titleMedium
        override var labelStyle: TextStyle = MaterialTheme.typography.labelSmall
        override var descriptionStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.outline
        )
        override var edgeGroupShape: Shape = MaterialTheme.shapes.medium
        override var containerCorner: Dp = 4.dp
        override var horizontalPadding: Dp = 16.dp
        override var verticalPadding: Dp = 12.dp
        override var minHeight: Dp = 72.dp
        override var itemSpacing: Dp = 4.dp
        override var groupSpacing: Dp = 6.dp
        override var slotSpacing: Dp = 6.dp
        override var cardElevation: Dp = 2.dp
        override var topBarContainerColor: Color = MaterialTheme.colorScheme.background
    }
}

