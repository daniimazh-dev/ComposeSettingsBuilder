package com.daniil.csb.styles

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/** More rounded version of [Material3] */

@SuppressLint("ComposableNaming")
@Composable
fun CSBStyle.Bobble() = SettingsStyle(
    containerCornerShape = 12.dp,
    edgeGroupCorner = MaterialTheme.shapes.extraLarge,
    cardElevation = 4.dp,
    itemSpacing = 6.dp,
    horizontalPadding = 18.dp,
    verticalPadding = 14.dp,
    titleStyle = MaterialTheme.typography.titleMedium,
    labelStyle = MaterialTheme.typography.labelSmall,
    activeColor = MaterialTheme.colorScheme.primary,
    descriptionStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.outline
    ),
    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    focusColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    minHeight = 52.dp,
)