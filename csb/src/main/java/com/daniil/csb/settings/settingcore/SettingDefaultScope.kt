package com.daniil.csb.settings.settingcore

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.ExperimentalCSBFeathers
import com.daniil.csb.TranslatableScope
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.depend.DependsScope
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import com.daniil.csb.styles.SettingComposer

@CsbDslMarkers
open class SettingDefaultScope<S: ComposeSetting<*>> internal constructor() : TranslatableScope {
    open var enabled: Boolean = true
    open var visible: Boolean = true
    open var isSaveSetting: Boolean = true
    open var customGrouping: GroupItemClip? = null
    internal var depends = emptyList<Depends>()
    open var badge: SettingBadge? = null
    open var icon: SettingIcon? = null
    open fun depends(dependsScope: DependsScope<S>.() -> Unit) {
        val data = DependsScope<S>().apply(dependsScope)
        depends = data.getDepends()
    }
    @ExperimentalCSBFeathers
    open var composer: SettingComposer? = null

}