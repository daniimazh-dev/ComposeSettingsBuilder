package com.daniil.csb.group

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.SettingBuilder
import kotlinx.coroutines.flow.MutableStateFlow


class AbstractGroup(
    override val id: String,
    override val settings: List<ComposeSetting<*>> = emptyList()
) : GroupSealed() {
    override val groupTitle: GroupTitle? = null
    override val visible = MutableStateFlow(false)
    override fun hide() {}

    override fun show() {}
}

@CsbDslMarkers
class AbstractGroupScope(id: String) : SettingBuilder()
