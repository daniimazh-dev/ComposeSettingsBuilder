package com.daniil.csb.classes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.ScreenInstance
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Custom<T>(
    override var id: String,
    innitValue: T?,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    val ignoreGroupClip: Boolean = false,
    val onClick: () -> Unit,
    val content: @Composable () -> Unit
) : SettingsSealed<T?>() {
    private var _value = MutableStateFlow(innitValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()



    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: T?) {
        _value.value = newValue
    }

    override fun fetchValue(): StateFlow<T?> = value

    class SwitchBuilderScope<T>() {
        var innitValue: T? = null
        lateinit var content: @Composable () -> Unit
        var ignoreGroupClip: Boolean = false
        var onClick: () -> Unit = {}
        var title = "Custom"
        var description = ""
        var enabled = true
    }

    class Builder<T>(
        val id: String,
        builderScope: SwitchBuilderScope<T>.() -> Unit
    ) {
        val scope = SwitchBuilderScope<T>().apply(builderScope)
        fun create(): Custom<T> = with(scope) {
            return Custom(id, innitValue, title, description, enabled, ignoreGroupClip, onClick = { onClick() }, content)
        }
    }

    @Composable
    override fun UI(group: ScreenInstance.Group, position: ItemGroupPosition) {
        val enabled by this.enabled.collectAsState()

        DefaultContainer(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            itemGroupPosition = if (ignoreGroupClip) ItemGroupPosition.None else position,
            onClick = { onClick() }
        ) {
            content()
        }
    }
}