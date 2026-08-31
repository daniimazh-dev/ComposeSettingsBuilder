package com.daniil.csb.settings

import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settings.utils.clippedShape
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Info internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var icon: InfoIcon,
    var onClicked: () -> Unit = {},
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false
    override val onChangeValue: (Unit) -> Unit = {}
    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Unit) {}

    @CsbDslMarkers
    class InfoBuilderScope(): SettingDefaultScope() {
        var title: String? = null
        var description: String? = null
        var icon: InfoIcon = InfoIconDefault.Message
        var onClick: () -> Unit = {}

        @Deprecated("The Info setting dose not store any data. Changing the value to true is not necessary", level = DeprecationLevel.HIDDEN)
        override var isSaveSetting: Boolean = false
    }

    object InfoIconDefault {
        val None = InfoIcon(null)
        val Message = InfoIcon(R.drawable.info_icon)
        val Warning = InfoIcon(R.drawable.warning_icon, Color.Yellow, Color.Yellow)
        val Error = InfoIcon(R.drawable.error_icon, Color.Red, Color.Red)
        fun custom(res: Int?, tint: Color = Color.Unspecified, borderLight: Color = Color.Unspecified): InfoIcon {
            return Info.InfoIcon(res, tint, borderLight)
        }
    }

    class InfoIcon(
        val res: Int?,
        val tint: Color = Color.Unspecified,
        val borderLight: Color = Color.Unspecified
    )


    companion object : ComposeSettingInterface.Factory<Info, InfoBuilderScope> {
        override fun SettingDslInterface.create(id: String, scope: InfoBuilderScope.() -> Unit): SettingToken<Info> {
            val data = InfoBuilderScope(); data.scope()
            return with(data) {
                Info(id, title.orEmpty(), description, enabled, icon, onClick, customGrouping).register()
            }
        }
    }

    override val defaultValue: Unit = Unit

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val groupPosition = LocalGroupPosition.current
        val customStyle = if (title.isBlank()) style.copy(
//            verticalPadding = style.verticalPadding / 2,
            minHeight = style.minHeight / 2f
        ) else style


        CompositionLocalProvider(LocalSettingsStyle provides customStyle) {
            DefaultSettingUI(
                modifier = modifier
                    then(if (icon.borderLight == Color.Unspecified) Modifier
                else Modifier.border(2.dp, this@Info.icon.borderLight, (position ?: groupPosition).clippedShape(customStyle))),
                isFocused = focusState,
                groupItemClip = customGrouping ?: position,
                enabled = enabled,
                title = { if (!title.isBlank()) Text(CSB.translator(title)) },
                description = { description?.let { Text(CSB.translator(it)) } },
                display = {
                    val res = remember { this@Info.icon.res }
                    if (res != null) {
                        Icon(
                            modifier = Modifier.alpha(0.7f),
                            painter = painterResource(res),
                            contentDescription = "Info",
                            tint = this@Info.icon.tint
                        )
                    }

                },
                onClick = { onClicked() }
            )
        }
    }
}

