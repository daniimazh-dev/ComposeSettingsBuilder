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
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Info(
    id: String,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    var onClicked: () -> Unit = {},
    override var isSaveSetting: Boolean = false
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }

    override fun changeValue(newValue: Unit) {}
    override fun resetToDefault() {}

    override var id: String = id

    class InfoBuilderScope() {
        var title = "Info"
        var description = ""
        var enabled = true
        var onClick: () -> Unit = {}
        var isSaveSetting = false
    }

    class Builder(
        val id: String,
        builderScope: InfoBuilderScope.() -> Unit = {}
    ) {
        val scope = InfoBuilderScope().apply(builderScope)
        fun create(): Info = with(scope) {
            return Info(id, title, description, enabled, onClick, isSaveSetting)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()

        DefaultSettingUI(
            modifier = Modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(title) },
            description = { if(!description.isBlank()) Text(description) },
            display = {
                Icon(
                    modifier = Modifier.alpha(0.7f),
                    painter = painterResource(R.drawable.info_icon),
                    contentDescription = "Info"
                )
            },
            onClick = { onClicked() }
        )
    }
}

fun SettingBuilder.createInfo(
    id: String,
    builder: Info.InfoBuilderScope.() -> Unit
): Info {
    return Info.Builder(id, builder).create()
}