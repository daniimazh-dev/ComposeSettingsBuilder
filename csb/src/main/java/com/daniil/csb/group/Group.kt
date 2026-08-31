package com.daniil.csb.group

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Group(
    override val id: String,
    override var groupTitle: GroupTitle? = null,
    isHide: Boolean = false,
    override val settings: List<ComposeSetting<*>> = emptyList()
) : GroupSealed() {
    private val _hide = MutableStateFlow(isHide)
    override val hide = _hide.asStateFlow()
    override fun hide() {
        _hide.value = true
    }

    override fun show() {
        _hide.value = false
    }
}

@CsbDslMarkers
open class GroupScope(id: String) : SettingBuilder() {
    var isHide: Boolean = false
    open var groupTitle: GroupTitle? = GroupTitle.setText(id)
}
