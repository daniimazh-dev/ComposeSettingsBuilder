package com.daniil.csb.group

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.SettingBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Group(
    override val id: String,
    override var groupTitle: GroupTitle? = null,
    visible: Boolean = true,
    override val settings: List<ComposeSetting<*>> = emptyList()
) : GroupSealed() {
    private val _visible = MutableStateFlow(visible)
    override val visible = _visible.asStateFlow()
    override fun hide() {
        _visible.value = false
    }

    override fun show() {
        _visible.value = true
    }
}

@CsbDslMarkers
open class GroupScope(id: String) : SettingBuilder() {
    open var visible: Boolean = true
    open var groupTitle: GroupTitle? = GroupTitle.text(id)
}
