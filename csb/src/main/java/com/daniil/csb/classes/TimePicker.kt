package com.daniil.csb.classes

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalTime = LocalTime.parse(decoder.decodeString())
}

class TimePicker internal constructor(
    override var id: String,
    override val defaultValue: LocalTime,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    val alertTitle: String,
    override var onChangeValue: (LocalTime) -> Unit = {},
    override var isSaveSetting: Boolean = true
) : ComposeSetting<LocalTime>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()


    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: LocalTime) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    override fun saveLogic(serializer: KSerializer<LocalTime>?): SaveSettingPackage? {
        return super.saveLogic(LocalTimeSerializer)
    }

    override fun loadLogic(pack: SaveSettingPackage, serializer: KSerializer<LocalTime>?) {
        super.loadLogic(pack, LocalTimeSerializer)
    }

    class TimePickerBuilderScope() {
        var defaultValue = LocalTime.now()
        var title: String? = null
        var description: String? = null
        var alertTitle = "Select time"
        var enabled = true
        var onChangeValue: (LocalTime) -> Unit = {}
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: TimePickerBuilderScope.() -> Unit = {}
    ) {
        val scope = TimePickerBuilderScope().apply(builderScope)
        fun create(): TimePicker = with(scope) {
            return TimePicker(
                id,
                defaultValue,
                title ?: id,
                description,
                enabled,
                alertTitle,
                onChangeValue,
                isSaveSetting
            )
        }
    }

    override val focusState = MutableStateFlow(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?,
    ) {
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()
        val state = rememberTimePickerState(
            initialHour = value.hour,
            initialMinute = value.minute,
        )
        var isAlertOpen by retain { mutableStateOf(false) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
            display = {
                TimePreview(value, is24Format = DateFormat.is24HourFormat(LocalContext.current))
            },
            onClick = {
                if (enabled) isAlertOpen = true
            }
        )
        if (isAlertOpen) {
            AlertDialog(
                title = {
                    Text(alertTitle)
                },
                text = {
                    TimePicker(
                        state = state,
                        modifier = Modifier,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isAlertOpen = false
                            changeValue(LocalTime.of(state.hour, state.minute))
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isAlertOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                onDismissRequest = {
                    isAlertOpen = false
                }
            )
        }
    }
}

@Composable
private fun TimePreview(
    time: LocalTime,
    modifier: Modifier = Modifier,
    is24Format: Boolean = true,
) {
    val style = LocalSettingsStyle.current
    val shape = RoundedCornerShape(style.containerCornerShape)

    val hour = if (is24Format) {
        time.hour
    } else {
        when {
            time.hour == 0 -> 12
            time.hour > 12 -> time.hour - 12
            else -> time.hour
        }
    }

    val amPm = if (time.hour < 12) "AM" else "PM"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(style.containerColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .widthIn(min = 28.dp)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                text = hour.toString().padStart(2, '0'),
                style = style.titleStyle
            )
        }
        Text(":")
        Box(
            modifier = Modifier
                .background(style.containerColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .widthIn(min = 28.dp)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                text = time.minute.toString().padStart(2, '0'),
                style = style.titleStyle
            )
        }
        if (!is24Format) {
            Box(
                modifier = Modifier
                    .background(style.containerColor, shape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .widthIn(min = 28.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    text = amPm,
                    style = style.titleStyle
                )
            }
        }
    }
}

fun SettingBuilder.createTimePicker(
    id: String,
    builder: TimePicker.TimePickerBuilderScope.() -> Unit = {}
): TimePicker {
    val setting = TimePicker.Builder(id, builder).create()
    setting.addToHeap()
    return setting
}