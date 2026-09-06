package com.daniil.csb.settings

import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settings.settingcore.clippedShape
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Info internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon?,
    val badge: SettingBadge?,
    var infoIcon: InfoIcon,
    var onClicked: () -> Unit = {},
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<Unit>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false
    override val onChangeValue: (Unit) -> Unit = {}

    override fun changeValue(newValue: Unit) {}

    @CsbDslMarkers
    class InfoBuilderScope() : SettingDefaultScope() {
        var title: String? = null
        var description: String? = null
        var infoIcon: InfoIcon = InfoIconDefault.Message
        var onClick: () -> Unit = {}

        @Deprecated(
            "The Info setting dose not store any data. Changing the value to true is not necessary",
            level = DeprecationLevel.HIDDEN
        )
        override var isSaveSetting: Boolean = false
    }

    object InfoIconDefault {
        val None = InfoIcon(null)
        val Message = InfoIcon(R.drawable.info_icon)
        val Warning = InfoIcon(R.drawable.warning_icon, Color.Yellow, Color.Yellow)
        val Error = InfoIcon(R.drawable.error_icon, Color.Red, Color.Red)
        fun custom(
            res: Int?,
            tint: Color = Color.Unspecified,
            borderLight: Color = Color.Unspecified
        ): InfoIcon {
            return Info.InfoIcon(res, tint, borderLight)
        }
    }

    class InfoIcon(
        val res: Int?,
        val tint: Color = Color.Unspecified,
        val borderLight: Color = Color.Unspecified
    )


    companion object : ComposeSettingInterface.Factory<Info, InfoBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: InfoBuilderScope.() -> Unit
        ): SettingToken<Info> {
            val data = InfoBuilderScope(); data.scope()
            return with(data) {
                Info(
                    id,
                    title.orEmpty(),
                    description,
                    enabled,
                    visible,
                    icon,
                    badge,
                    infoIcon,
                    onClick,
                    customGrouping,
                    depends
                ).register()
            }
        }
    }

    override val defaultValue: Unit = Unit

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val translator = LocalCSBTranslator.current
        val groupPosition = LocalGroupPosition.current


        DefaultSettingUI(
            modifier = modifier
                    then (if (infoIcon.borderLight == Color.Unspecified) Modifier
            else Modifier.border(
                2.dp,
                this@Info.infoIcon.borderLight,
                (position ?: groupPosition).clippedShape()
            )),
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            minHeight = style.minHeight / 2,
            enabled = enabled,
            icon = icon,
            badge = badge,
            title = { if (!title.isBlank()) Text(translator.translate(title)) },
            description = { description?.let { Text(translator.translate(it)) } },
            action = {
                val res = remember { this@Info.infoIcon.res }
                if (res != null) {
                    Icon(
                        modifier = Modifier.alpha(0.7f),
                        painter = painterResource(res),
                        contentDescription = "Info",
                        tint = if (infoIcon.tint == Color.Unspecified) LocalContentColor.current else infoIcon.tint
                    )
                }

            },
            onClick = { onClicked() }
        )

    }
}
