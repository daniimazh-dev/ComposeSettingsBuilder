package com.daniil.csb.classes.utils

import androidx.compose.runtime.compositionLocalOf

val LocalGroupPosition = compositionLocalOf { GroupItemClip.Default }

enum class GroupItemClip {
    First,
    Default,
    Last,
    None,
}