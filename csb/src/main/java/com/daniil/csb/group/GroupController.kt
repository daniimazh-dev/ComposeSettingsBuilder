package com.daniil.csb.group


class GroupController internal constructor(
    private val group: Group,
) {
    val id = group.id
    fun isShow(state: Boolean) { if (state) group.show() else group.hide() }
    fun isDisable(state: Boolean) { getSetting().forEach { it.enabled(!state) }  }
    fun resetToDefault(vararg ignoreId: String) {
        getSetting().forEach { if (it.id !in ignoreId) it.resetToDefault() }
    }
    fun getSetting() = group.settings
}