package com.daniil.csb.styles

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
/** Default for CSB */
@SuppressLint("ComposableNaming")
@Composable
fun CSBStyle.Material3() = SettingsStyle(
    titleStyle = MaterialTheme.typography.titleMedium,
    labelStyle = MaterialTheme.typography.labelSmall,
    descriptionStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.outline
    ),
    edgeGroupCorner = MaterialTheme.shapes.medium,
    containerCornerShape = 4.dp,
    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    activeColor = MaterialTheme.colorScheme.primary,
    focusColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    horizontalPadding = 16.dp,
    verticalPadding = 12.dp,
    minHeight = 68.dp,
    itemSpacing = 4.dp,
    cardElevation = 2.dp,
)
