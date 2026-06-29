package com.daniil.csb.classes

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.apply

class Switch internal constructor(
    override var id: String,
    override val defaultValue: Boolean,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var onChangeValue: (Boolean) -> Unit = {},
    override var isSaveSetting: Boolean = true
) : ComposeSetting<Boolean>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()



    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Boolean) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.BooleanPackage(
            id = id,
            enable = enabled.value,
            value = value.value
        )
    }

    class SwitchBuilderScope() {
        var defaultValue = false
        var title: String? = null
        var description: String? = null
        var enabled = true
        var onChangeValue: (Boolean) -> Unit = {}
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: SwitchBuilderScope.() -> Unit = {}
    ) {
        val scope = SwitchBuilderScope().apply(builderScope)
        fun create(): Switch = with(scope) {
            return Switch(id, defaultValue, title ?: id, description, enabled, onChangeValue,  isSaveSetting)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?,
    ) {
        val style = LocalSettingsStyle.current
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
            display = {
                Switch(
                    checked = value,
                    onCheckedChange = {
                        changeValue(it)
                    },
                    colors = SwitchDefaults.colors(),
                    enabled = enabled
                )
            },
            onClick = {
                changeValue(!value)
            }
        )
    }
}

fun SettingBuilder.createSwitch(
    id: String,
    builder: Switch.SwitchBuilderScope.() -> Unit = { defaultValue = false }
): Switch {
    val setting = Switch.Builder(id, builder).create()
    setting.addToHeap()
    return setting
}