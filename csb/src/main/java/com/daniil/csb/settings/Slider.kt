package com.daniil.csb.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
class Slider internal constructor(
    override var id: String,
    override val defaultValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int,
    val startPointRange: String? = range.start.toString(),
    val endPointRange: String? = range.endInclusive.toString(),
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    override var onChangeValue: (Float) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<Float>(depends = depends, initialEnabled = enabled, initialVisible = visible) {

    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()


    var sliderState: MutableState<SliderState> = mutableStateOf(
        SliderState(
            value = defaultValue,
            steps = steps,
            valueRange = range,
            onValueChangeFinished = {
                val newValue = sliderState.value.value
                _value.value = newValue
                onChangeValue(newValue)
            }
        )
    )

    override fun changeValue(newValue: Float) {
        _value.value = newValue
        onChangeValue(newValue)
        sliderState.value = SliderState(
            value = newValue,
            steps = steps,
            valueRange = range,
            onValueChangeFinished = {
                val updatedValue = sliderState.value.value
                _value.value = updatedValue
                onChangeValue(updatedValue)
            }
        )
    }


    @CsbDslMarkers
    class SliderBuilderScope() : SettingDefaultScope<Slider>() {
        var defaultValue = 0f
        var range: ClosedFloatingPointRange<Float> = 0f..1f
        var steps = 0
        var title: String? = null
        var description: String? = null

        var onChangeValue: (Float) -> Unit = {}
        var startPointRange: String? = null
        var endPointRange: String? = null
    }

    companion object : ComposeSettingInterface.Factory<Slider, SliderBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: SliderBuilderScope.() -> Unit
        ): SettingToken<Slider> {
            val data = SliderBuilderScope().apply(scope)
            return with(data) {
                Slider(
                    id = id,
                    defaultValue = defaultValue,
                    range = range,
                    steps = steps,
                    startPointRange = startPointRange ?: range.start.toString(),
                    endPointRange = endPointRange ?: range.endInclusive.toString(),
                    title = title ?: id,
                    description = description,
                    enabled = enabled,
                    visible = visible,
                    icon = icon,
                    badge = badge,
                    onChangeValue = onChangeValue,
                    isSaveSetting = isSaveSetting,
                    customGrouping = customGrouping,
                    depends = depends
                ).register()
            }
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val translator = LocalCSBTranslator.current

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = customGrouping ?: position,
            icon = icon,
            badge = badge,
            title = { if (title.isNotBlank()) Text(text = translator.translate(title)) },
            description = { description?.let { Text(text = translator.translate(it)) } },
            action = {},
            display = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        state = sliderState.value,
                        colors = SliderDefaults.colors().copy(
                            activeTrackColor = style.activeColor,
                            thumbColor = style.activeColor
                        ),
                        enabled = enabled,
                    )
                    if (startPointRange != null && endPointRange != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val labelStile = MaterialTheme.typography.labelSmall
                                .copy(color = MaterialTheme.colorScheme.outline)
                            Text(text = translator.translate(startPointRange), style = labelStile)
                            Text(text = translator.translate(endPointRange), style = labelStile)
                        }
                    }
                }
            },
            onClick = null
        )
    }

}
