package com.daniil.csb.classes

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.apply

class Switch(
    id: String,
    innitValue: Boolean,
    override val title: String,
    override val description: String,
    enabled: Boolean = true
) : SettingsSealed<Boolean>() {
    private var _value = MutableStateFlow(innitValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Boolean) {
        _value.value = newValue
    }

    override fun fetchValue(): StateFlow<Boolean> = value

    override var id: String = id

    class SwitchBuilderScope() {
        var innitValue = false
        var title = "Switch"
        var description = ""
        var enabled = true
    }

    class Builder(
        val id: String,
        builderScope: SwitchBuilderScope.() -> Unit
    ) {
        val scope = SwitchBuilderScope().apply(builderScope)
        fun create(): Switch = with(scope) {
            return Switch(id, innitValue, title, description, enabled)
        }
    }

    @Composable
    override fun UI() {
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        DefaultContainer(
            modifier = Modifier,
            enabled = enabled,
            title = { Text(title) },
            description = { Text(description) },
            display = {
                Switch(
                    checked = value,
                    onCheckedChange = {
                        changeValue(it)
                    },
                    enabled = enabled
                )
            },
            onClick = { changeValue(!value) }
        )
    }
}