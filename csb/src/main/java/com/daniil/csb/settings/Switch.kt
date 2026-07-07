package com.daniil.csb.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Switch internal constructor(
    override var id: String,
    override val defaultValue: Boolean,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    override var onChangeValue: (Boolean) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    val uiMode: UIMode = UIMode.Switch
) : ComposeSetting<Boolean>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    enum class UIMode {
        Switch,
        CheckBox,
        RadioButton,
        SquareRadioButton,
        OnOffState
    }

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Boolean) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class SwitchBuilderScope() {
        var defaultValue = false
        var title: String? = null
        var description: String? = null
        var enabled = true
        var onChangeValue: (Boolean) -> Unit = {}
        var isSaveSetting = true
        var uiMode = UIMode.Switch
    }

    class Builder(
        val id: String,
        builderScope: SwitchBuilderScope.() -> Unit = {}
    ) {
        val scope = SwitchBuilderScope().apply(builderScope)
        fun create(): Switch = with(scope) {
            return Switch(id, defaultValue, title ?: id, description, enabled, onChangeValue,  isSaveSetting, uiMode)
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
                when (uiMode) {
                    UIMode.Switch -> {
                        Switch(
                            checked = value,
                            onCheckedChange = { if (enabled) changeValue(it) },
                            colors = SwitchDefaults.colors().copy(
                                checkedTrackColor = style.activeColor
                            ),
                            enabled = enabled
                        )
                    }
                    UIMode.CheckBox -> {
                        val shape = RoundedCornerShape(6.dp)
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(28.dp)
                                .border(2.dp, style.activeColor, shape)
                                .clip(shape)
                                .clickable { if (enabled) changeValue(!value) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row() {
                                AnimatedVisibility(
                                    visible = value,
                                    exit = scaleOut(),
                                    enter = scaleIn()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.check),
                                        contentDescription = "Check",
                                        tint = style.activeColor
                                    )
                                }
                            }
                        }
                    }
                    UIMode.RadioButton, UIMode.SquareRadioButton -> {
                        val shape = if (uiMode == UIMode.RadioButton) CircleShape else RoundedCornerShape(6.dp)
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(28.dp)
                                .border(2.dp, style.activeColor, shape)
                                .clip(shape)
                                .clickable { if (enabled) changeValue(!value) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row() {
                                AnimatedVisibility(
                                    visible = value,
                                    exit = scaleOut(),
                                    enter = scaleIn()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(17.dp)
                                            .background(style.activeColor,
                                                if (uiMode == UIMode.RadioButton) CircleShape else RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                    UIMode.OnOffState -> {
                        val animateColor by animateColorAsState(
                            targetValue = if (value) style.activeColor else style.containerColor,
                            animationSpec = tween(400)
                        )
                        Box(
                            modifier = Modifier
                            .clip(RoundedCornerShape(style.containerCornerShape))
                            .background(color = animateColor)
                            .clickable { if (enabled) changeValue(!value) },
                        ) {
                            AnimatedContent(
                                targetState = value
                            ) { state ->
                                Text(
                                    modifier = Modifier
                                        .widthIn(min = 28.dp)
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    text = if (state) "ON" else "OFF",
                                    color = if (value) Color.Black else Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            },
            onClick = {
                changeValue(!value)
            }
        )
    }
}

