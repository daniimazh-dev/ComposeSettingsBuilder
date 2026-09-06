package com.daniil.csb.settingui

import androidx.compose.runtime.compositionLocalOf
import com.daniil.csb.CSB
import com.daniil.csb.CSBTranslator
import com.daniil.csb.DebugData
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.styles.DefaultSettingStyle
import com.daniil.csb.styles.SettingStyle

val LocalGroupPosition = compositionLocalOf { GroupItemClip.None }

internal val LocalDebugData = compositionLocalOf<DebugData?> { null }

val LocalSettingsStyle = compositionLocalOf<SettingStyle> { DefaultSettingStyle() }

internal val LocalCSBTranslator = compositionLocalOf<CSBTranslator> { CSB.DefaultCSBTranslator() }
