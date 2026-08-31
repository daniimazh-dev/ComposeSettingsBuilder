package com.daniil.csb.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.milliseconds


class Counter internal constructor(
    override var id: String,
    override val defaultValue: Int,
    val range: IntRange,
    val steps: Int,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    override var onChangeValue: (Int) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Int>() {

    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Int) {
        val coercedValue = newValue.coerceIn(range)
        _value.value = coercedValue
        onChangeValue(coercedValue)
    }

    @CsbDslMarkers
    class CounterBuilderScope(): SettingDefaultScope() {

        var range: IntRange = 0..10
        var defaultValue = range.first
        var steps = 1
        var title: String? = null
        var description: String? = null

        var onChangeValue: (Int) -> Unit = {}
    }

    companion object : ComposeSettingInterface.Factory<Counter, CounterBuilderScope> {
        override fun SettingDslInterface.create(id: String, scope: CounterBuilderScope.() -> Unit): SettingToken<Counter> {
            val data = CounterBuilderScope().apply(scope)
            return with(data) {
                Counter(
                    id,
                    defaultValue,
                    range,
                    steps,
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
        val value by this.value.collectAsState()

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(CSB.translator(title)) },
            description = { description?.let { Text(CSB.translator(it)) } },
            display = {
                CounterImpl(value) {
                    if (enabled) {
                        val newValue = if (it) value + steps else value - steps
                        changeValue(newValue)
                    }
                }
            },
            onClick = null
        )

    }

}

@Composable
private fun CounterImpl(
    value: Int,
    modifier: Modifier = Modifier,
    onChangeValue: (Boolean) -> Unit
) {
    val style = LocalSettingsStyle.current
    val shape = RoundedCornerShape(style.containerCornerShape)
    val removeInteraction = remember { MutableInteractionSource() }
    val addInteraction = remember { MutableInteractionSource() }

    val isAddPress = addInteraction.collectIsPressedAsState()
    val isRemovePress = removeInteraction.collectIsPressedAsState()

    val currentOnChangeValue by rememberUpdatedState(onChangeValue)
    val currentValue by rememberUpdatedState(value)

    var addButtonClicked by remember { mutableStateOf(false) }
    var removeButtonClicked by remember { mutableStateOf(false) }

    val animateAddScale by animateFloatAsState(
        if (addButtonClicked) 0.8f else 1f
    )
    val animateRemoveScale by animateFloatAsState(
        if (removeButtonClicked) 0.8f else 1f
    )
    LaunchedEffect(addButtonClicked, removeButtonClicked) {
        if (addButtonClicked) {
            delay(100.milliseconds)
            addButtonClicked = false
        }
        if (removeButtonClicked) {
            delay(100.milliseconds)
            removeButtonClicked = false
        }

    }

    LaunchedEffect(isAddPress.value) {
        if (isAddPress.value) {
            delay(400.milliseconds)
            var millis = 200
            while (isAddPress.value) {
                currentOnChangeValue(true)
                delay(millis.milliseconds)
                millis -= 20
                if (millis < 50) millis = 50
            }
        }
    }
    LaunchedEffect(isRemovePress.value) {
        if (isRemovePress.value) {
            delay(400.milliseconds)
            var millis = 200
            while (isRemovePress.value) {
                currentOnChangeValue(false)
                delay(millis.milliseconds)
                millis -= 20
                if (millis < 50) millis = 50
            }
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(style.containerColor)
                .combinedClickable(
                    indication = ripple(color = ColorProducer { style.activeColor }),
                    interactionSource = removeInteraction,
                    onClick = {
                        removeButtonClicked = true
                        onChangeValue(false)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = animateRemoveScale
                        scaleY = animateRemoveScale
                    }
                    .padding(4.dp),
                painter = painterResource(R.drawable.remove),
                contentDescription = "Remove"
            )
        }
        Box(
            modifier = Modifier
                .background(style.containerColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .widthIn(min = 28.dp)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                text = value.toString(), textAlign = TextAlign.Center,
                style = style.titleStyle
            )
        }
        Box(
            modifier = Modifier
                .clip(shape)
                .background(style.containerColor)
                .combinedClickable(
                    indication = ripple(color = ColorProducer { style.activeColor }),
                    interactionSource = addInteraction,
                    onClick = {
                        addButtonClicked = true
                        onChangeValue(true)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = animateAddScale
                        scaleY = animateAddScale
                    }
                    .padding(4.dp),
                painter = painterResource(R.drawable.add),
                contentDescription = "Add"
            )
        }

    }
}

