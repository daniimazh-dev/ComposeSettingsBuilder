package com.daniil.csb.settings

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Info internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var onClicked: () -> Unit = {},
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false
    override val onChangeValue: (Unit) -> Unit = {}
    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }

    override fun changeValue(newValue: Unit) {}
    @CsbDslMarkers
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
            return Info(id, title.orEmpty(), description, enabled, onClick)
        }
    }

    override val defaultValue: Unit = Unit

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val customStyle = if (title.isBlank()) style.copy(
            verticalPadding = style.verticalPadding / 2,
            minHeight = style.minHeight / 1.5f
        ) else style
        CompositionLocalProvider(LocalSettingsStyle provides customStyle) {
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
}

