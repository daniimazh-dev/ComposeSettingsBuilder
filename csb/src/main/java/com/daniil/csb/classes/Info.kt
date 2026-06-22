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
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var onClicked: () -> Unit = {},
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }

    override fun changeValue(newValue: Unit) {}
    override fun resetToDefault() {}

    class InfoBuilderScope() {
        var title: String? = null
        var description: String? = null
        var enabled = true
        var onClick: () -> Unit = {}
    }

    class Builder(
        val id: String,
        builderScope: InfoBuilderScope.() -> Unit = {}
    ) {
        val scope = InfoBuilderScope().apply(builderScope)
        fun create(): Info = with(scope) {
            return Info(id, title ?: id, description, enabled, onClick)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
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
    builder: Info.InfoBuilderScope.() -> Unit = {}
): Info {
    val setting = Info.Builder(id, builder).create()
    setting.addToHeap()
    return setting
}