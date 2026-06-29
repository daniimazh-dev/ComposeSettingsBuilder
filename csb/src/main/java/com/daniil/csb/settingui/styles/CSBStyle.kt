package com.daniil.csb.settingui.styles

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


object CSBStyle

// Default
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
    focusColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    horizontalPadding = 16.dp,
    verticalPadding = 12.dp,
    minHeight = 68.dp,
    itemSpacing = 4.dp,
    cardElevation = 2.dp,
)


// Without material theme
val CSBStyle.ClassicLight: SettingsStyle
    get() = SettingsStyle(
        titleStyle = TextStyle.Default.copy(
            fontSize = 16.sp
        ),
        labelStyle = TextStyle.Default.copy(
            fontSize = 11.sp
        ),
        descriptionStyle = TextStyle.Default.copy(
            fontSize = 12.sp,
            color = Color.Gray
        ),
        edgeGroupCorner = RoundedCornerShape(6.dp),
        containerCornerShape = 0.dp,
        backgroundColor = Color.White,
        focusColor = Color.Gray.copy(alpha = 0.4f),
        horizontalPadding = 12.dp,
        verticalPadding = 10.dp,
        minHeight = 52.dp,
        itemSpacing = 2.dp,
        cardElevation = 0.dp,
    )

val CSBStyle.ClassicDark
    get() = ClassicLight.copy(
        backgroundColor = Color.DarkGray
    )
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
    descriptionStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.outline
    ),
    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    focusColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    minHeight = 52.dp,
)