package com.daniil.csb.classes

import androidx.annotation.IntRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class Slider(
    id: String,
    innitValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int,
    override val title: String,
    override val description: String,
    enabled: Boolean = true
) : SettingsSealed<Float>() {
    override var id: String = id

    private var _value = MutableStateFlow(innitValue)
    override val value = _value.asStateFlow()


    var sliderState: MutableState<SliderState> =  mutableStateOf(
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
        var enabled = true
    }

    class Builder(
        val id: String,
        builderScope: SliderBuilderScope.() -> Unit
    ) {
        val scope = SliderBuilderScope().apply(builderScope)
        fun create(): Slider = with(scope) {
            return Slider(id, innitValue, range, steps, title, description, enabled)
        }
    }



    @Composable
    override fun UI() {
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        DefaultContainer(
            modifier = Modifier,
            columnMode = true,
            enabled = enabled,
            title = { Text(title) },
            description = {
                Text(description)
            },
            display = {
                Slider(
                    state = sliderState.value,
                    enabled = enabled,
                )
            },
            onClick = { }
        )
    }

}