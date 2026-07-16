package com.daniil.csb.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.isInFlag
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
    var icon: InfoIcon,
    var onClicked: () -> Unit = {},
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
    class InfoBuilderScope() {
        var title: String? = null
        var description: String? = null
        var icon: InfoIcon = InfoIconDefault.Massage
        var enabled = true
        var onClick: () -> Unit = {}
    }

    object InfoIconDefault {
        val None = InfoIcon(null)
        val Massage = InfoIcon(R.drawable.info_icon)
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


    class Builder(
        val id: String,
        builderScope: InfoBuilderScope.() -> Unit = {}
    ) {
        val scope = InfoBuilderScope().apply(builderScope)
        fun create(): Info = with(scope) {
            return Info(id, title.orEmpty(), description, enabled, icon, onClick)
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



        val baseShape = style.edgeGroupCorner as RoundedCornerShape
        val gcs = style.containerCornerShape
        val entries = if ("disableContainerGroupRound".isInFlag()) GroupItemClip.None else position

        val groupClip = when (entries) {

            GroupItemClip.First -> baseShape.copy(
                bottomEnd = CornerSize(gcs),
                bottomStart = CornerSize(gcs),
            )

            GroupItemClip.None -> baseShape.copy(
                topStart = CornerSize(gcs),
                topEnd = CornerSize(gcs),
                bottomEnd = CornerSize(gcs),
                bottomStart = CornerSize(gcs),
            )

            GroupItemClip.Last -> baseShape.copy(
                topStart = CornerSize(gcs),
                topEnd = CornerSize(gcs),
            )

            GroupItemClip.Full -> baseShape
            else -> baseShape
        }
        CompositionLocalProvider(LocalSettingsStyle provides customStyle) {
            DefaultSettingUI(
                modifier = modifier.border(2.dp, this@Info.icon.borderLight, groupClip),
                isFocused = focusState,
                groupItemClip = position,
                enabled = enabled,
                title = { if (!title.isBlank()) Text(title) },
                description = { description?.let { Text(it) } },
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

