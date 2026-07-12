package com.daniil.csb.screens

import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
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
open class GroupScope() : SettingBuilder() {
    var groupTitle: GroupTitle? = DefaultGroupTitle
}

class GroupController internal constructor(
    private val group: Group,
) {
    val id = group.id
    fun isShow(state: Boolean) { if (state) group.show() else group.hide() }
    fun isDisable(state: Boolean) { group.settings.forEach { it.enabled(state) }  }
    fun resetToDefault() {  group.settings.forEach { it.resetToDefault() } }
}