package com.daniil.csb.classes

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.screens.ScreenInstance
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.apply

class Switch(
    override var id: String,
    innitValue: Boolean,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean = true
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

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.BooleanPackage(
            id = id,
            enable = enabled.value,
            value = value.value
        )
    }

    class SwitchBuilderScope() {
        var innitValue = false
        var title = "Switch"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: SwitchBuilderScope.() -> Unit = {}
    ) {
        val scope = SwitchBuilderScope().apply(builderScope)
        fun create(): Switch = with(scope) {
            return Switch(id, innitValue, title, description, enabled, isSaveSetting)
        }
    }

    @Composable
    override fun UI(screen: ScreenInstance, position: ItemGroupPosition) {
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()

        DefaultSettingUI(
            modifier = Modifier,
            itemGroupPosition = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { if (!description.isBlank()) Text(description) },
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