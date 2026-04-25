package com.daniil.csb.classes

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import com.daniil.csb.R
import com.daniil.csb.ScreenInstance
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Info(
    id: String,
    override val title: String,
    override val description: String,
    enabled: Boolean = true
) : SettingsSealed<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Unit) {
        _value.value = newValue
    }

    override fun fetchValue(): StateFlow<Unit> = value

    override var id: String = id

    class InfoBuilderScope() {
        var title = "Info"
        var description = ""
        var enabled = true
    }

    class Builder(
        val id: String,
        builderScope: InfoBuilderScope.() -> Unit
    ) {
        val scope = InfoBuilderScope().apply(builderScope)
        fun create(): Info = with(scope) {
            return Info(id, title, description, enabled)
        }
    }

    @Composable
    override fun UI(group: ScreenInstance.Group, position: ItemGroupPosition) {
        val enabled by this.enabled.collectAsState()

        DefaultSettingUI(
            modifier = Modifier,
            itemGroupPosition = position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(title) },
            description = { if(!description.isBlank()) Text(description) },
            display = {
                Icon(
                    modifier = Modifier.alpha(0.7f),
                    painter = painterResource(R.drawable.info),
                    contentDescription = "Info"
                )
            },
            onClick = {  }
        )
    }
}