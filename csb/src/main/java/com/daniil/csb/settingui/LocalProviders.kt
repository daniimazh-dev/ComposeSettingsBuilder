package com.daniil.csb.settingui

import androidx.compose.runtime.compositionLocalOf
import com.daniil.csb.DebugData
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.styles.SettingsStyle

val LocalGroupPosition = compositionLocalOf { GroupItemClip.None }

internal val LocalDebugData = compositionLocalOf<DebugData?> { null }

val LocalSettingsStyle = compositionLocalOf { SettingsStyle() }
