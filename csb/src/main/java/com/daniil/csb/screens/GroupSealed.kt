package com.daniil.csb.screens

import com.daniil.csb.settings.utils.ComposeSetting
import kotlinx.coroutines.flow.StateFlow

sealed class GroupSealed() {
    abstract val settings: List<ComposeSetting<*>>
    abstract val groupTitle: GroupTitle?
    abstract val id: String
    abstract val hide: StateFlow<Boolean>
    abstract fun hide()
    abstract fun show()
}
