package com.daniil.csb.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.ScreenInstance
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
class Slider(
    override var id: String,
    innitValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int,
    val startPointRange: String? = range.start.toString(),
    val endPointRange: String? = range.endInclusive.toString(),
    override val title: String,
    override val description: String,
    enabled: Boolean = true
) : SettingsSealed<Float>() {

    private var _value = MutableStateFlow(innitValue)
    override val value = _value.asStateFlow()


    var sliderState: MutableState<SliderState> = mutableStateOf(
        SliderState(
            value = innitValue,
            steps = steps,
            valueRange = range,
            onValueChangeFinished = { _value.value = sliderState.value.value }
        )
    )

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Float) {
        _value.value = newValue
        sliderState.value = SliderState(
            value = newValue,
            steps = steps,
            valueRange = range,
            onValueChangeFinished = { _value.value = sliderState.value.value }
        )
    }


    override fun fetchValue(): StateFlow<Float> = value


    class SliderBuilderScope() {
        var innitValue = 0f
        var range: ClosedFloatingPointRange<Float> = 0f..1f
        var steps = 0
        var title = "Slider"
        var description = ""
        var startPointRange: String? = range.start.toString()
        var endPointRange: String? = range.endInclusive.toString()
        var enabled = true
    }

    class Builder(
        val id: String,
        builderScope: SliderBuilderScope.() -> Unit
    ) {
        val scope = SliderBuilderScope().apply(builderScope)
        fun create(): Slider = with(scope) {
            return Slider(id, innitValue, range, steps, startPointRange, endPointRange,  title, description, enabled)
        }
    }


    @Composable
    override fun UI(group: ScreenInstance.Group, position: ItemGroupPosition) {
        val value by this.value.collectAsState()
        val enabled by this.enabled.collectAsState()
        DefaultContainer(
            modifier = Modifier,
            enabled = enabled,
            itemGroupPosition = position,
            onClick = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),

                ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val titleStyle = MaterialTheme.typography.titleMedium
                        if (!title.isBlank()) Text(text = title, style = titleStyle)
                        val descriptionStyle = MaterialTheme.typography.labelSmall
                            .copy(color = MaterialTheme.colorScheme.outline)
                        if (!description.isBlank()) Text(
                            text = description,
                            style = descriptionStyle
                        )
                    }
                }

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    state = sliderState.value,
                    enabled = enabled,
                )
                if (startPointRange != null && endPointRange != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val descriptionStyle = MaterialTheme.typography.labelSmall
                            .copy(color = MaterialTheme.colorScheme.outline)
                        Text(text = startPointRange.orEmpty(), style = descriptionStyle)
                        Text(text = endPointRange.orEmpty(), style = descriptionStyle)
                    }
                }

            }
        }

    }

}