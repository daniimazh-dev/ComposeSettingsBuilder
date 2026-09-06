package com.daniil.csb.settings.settingcore

import com.daniil.csb.settings.depend.Depends
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ComposeSetting<T>(
    val independentObject: Boolean = false,
    override val depends: List<Depends> = emptyList(),
    initialEnabled: Boolean = true,
    initialVisible: Boolean = true
): ComposeSettingInterface<T> {
    private val _visible = MutableStateFlow(initialVisible)
    override val visible = _visible.asStateFlow()

    private val _enabled = MutableStateFlow(initialEnabled)
    override val enabled = _enabled.asStateFlow()

    override val focusState = MutableStateFlow(false)

    override fun show(state: Boolean) {
        _visible.value = state
    }

    override fun enabled(state: Boolean) {
        _enabled.value = state
    }
}
