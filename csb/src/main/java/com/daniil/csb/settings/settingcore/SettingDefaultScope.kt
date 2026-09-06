package com.daniil.csb.settings.settingcore

import androidx.compose.runtime.Composable
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.TranslatableScope
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.depend.DependsScope
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon

@CsbDslMarkers
open class SettingDefaultScope: TranslatableScope {
    open var enabled: Boolean = true
    open var visible: Boolean = true
    open var isSaveSetting: Boolean = true
    open var customGrouping: GroupItemClip? = null
    internal var depends = emptyList<Depends>()
    open var badge: SettingBadge? = null
    open var icon: SettingIcon? = null
    fun depends(dependsScope: DependsScope.() -> Unit) {
        val data = DependsScope().apply(dependsScope)
        depends = data.getDepends()
    }
}