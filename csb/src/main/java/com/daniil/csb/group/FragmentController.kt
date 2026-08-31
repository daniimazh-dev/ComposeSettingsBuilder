package com.daniil.csb.group

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class FragmentController() {
    private var _fragmentedGroup: FragmentedGroup? = null

    private val _groups = MutableStateFlow<Map<String, Group>>(emptyMap())
    val groups: StateFlow<Map<String, Group>> = _groups.asStateFlow()

    private val _currentFragmentId = MutableStateFlow("")
    val currentFragmentId: StateFlow<String> = _currentFragmentId.asStateFlow()

    internal val initialValue: String get() = _fragmentedGroup?.initialActive ?: ""
    private var onChangeFragmentListener: MutableList<(key: String) -> Unit> = mutableListOf()
    fun changeFragmentListener(onChange: (key: String) -> Unit) {
        onChangeFragmentListener.add(onChange)
    }
    fun changeFragment(key: String) {
        if (key !in groups.value.keys) error("Not found Fragment with id: \"$key\" in FragmentedGroup id: \"${_fragmentedGroup?.id}\"")
        _fragmentedGroup?.changeFragment(key)
        _currentFragmentId.value = key
    }

    fun isShow(state: Boolean) {
        if (state) _fragmentedGroup?.show() else _fragmentedGroup?.hide()
    }

    fun isDisable(state: Boolean) {
        _fragmentedGroup?.settings?.forEach { it.enabled(!state) }
    }

    internal fun bind(fragmentedGroup: FragmentedGroup) {
        this._fragmentedGroup = fragmentedGroup
        this._groups.value = fragmentedGroup.groups
        this._currentFragmentId.value = fragmentedGroup.activeFragmentId.value
    }

    internal fun updateCurrentFragmentId(key: String) {
        if (key !in groups.value.keys) error("Not found Fragment with id: \"$key\" in FragmentedGroup id: \"${_fragmentedGroup?.id}\"")
        _currentFragmentId.value = key
        onChangeFragmentListener.forEach { it(key) }
    }
}
