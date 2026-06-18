package com.daniil.csb.classes.utils

import androidx.compose.runtime.compositionLocalOf

val LocalGroupPosition = compositionLocalOf { ItemGroupPosition.None }

enum class ItemGroupPosition {
    First,
    Default,
    Last,
    None,
}