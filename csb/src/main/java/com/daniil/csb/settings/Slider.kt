package com.daniil.csb.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
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
    override var onChangeValue: (Float) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Float>() {

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

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

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
    class SliderBuilderScope() : SettingDefaultScope() {
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
                    id,
                    defaultValue,
                    range,
                    steps,
                    startPointRange ?: range.start.toString(),
                    endPointRange ?: range.endInclusive.toString(),
                    title ?: id,
                    description,
                    enabled,
                    onChangeValue,
                    isSaveSetting,
                    customGrouping
                ).register()
            }
        }
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()

        DefaultContainer(
            modifier = modifier,
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = position,
            paddingValues =
                PaddingValues(
                    horizontal = style.horizontalPadding,
                    vertical = style.verticalPadding
                ),
            onClick = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = style.minHeight)
            ) {


                if (!title.isBlank()) Text(text = CSB.translator(title), style = style.titleStyle)
                val descriptionStyle = style.labelStyle
                    .copy(color = MaterialTheme.colorScheme.outline)
                description?.let { Text(text = CSB.translator(it), style = descriptionStyle) }

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
                        Text(text = CSB.translator(startPointRange), style = labelStile)
                        Text(text = CSB.translator(endPointRange), style = labelStile)
                    }
                }

            }
        }

    }

}

