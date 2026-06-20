package com.daniil.csb.classes.utils

import androidx.compose.runtime.compositionLocalOf

val LocalGroupPosition = compositionLocalOf { GroupItemClip.None }

enum class GroupItemClip {
    First,
    None,
    Last,
    Full,
}