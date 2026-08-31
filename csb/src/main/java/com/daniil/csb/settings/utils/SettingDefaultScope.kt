package com.daniil.csb.settings.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.daniil.csb.CSBTranslator
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.TranslatableScope

@CsbDslMarkers
open class SettingDefaultScope: TranslatableScope {
    var enabled: Boolean = true
    open var isSaveSetting: Boolean = true
    var customGrouping: GroupItemClip? = null
}