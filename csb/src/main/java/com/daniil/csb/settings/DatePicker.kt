package com.daniil.csb.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.utils.LocalDateSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatePicker internal constructor(
    override var id: String,
    override val defaultValue: LocalDate,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    val alertTitle: String,
    val formatter: DateTimeFormatter,
    override var onChangeValue: (LocalDate) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<LocalDate>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()


    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: LocalDate) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    override fun saveLogic(): SaveSettingPackage? {
        return saveJson(LocalDateSerializer)
    }

    override fun loadLogic(pack: SaveSettingPackage) {
        loadJson(pack, LocalDateSerializer)
    }

    @CsbDslMarkers
    class DatePickerBuilderScope(): SettingDefaultScope() {
        var defaultValue = LocalDate.now()
        var title: String? = null
        var description: String? = null
        var alertTitle = "Select date"
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE
        var onChangeValue: (LocalDate) -> Unit = {}
    }

    companion object : ComposeSettingInterface.Factory<DatePicker, DatePickerBuilderScope> {
        override fun SettingDslInterface.create(id: String, scope: DatePickerBuilderScope.() -> Unit): SettingToken<DatePicker> {
            val data = DatePickerBuilderScope().apply(scope)
            return with(data) {
                DatePicker(
                    id,
                    defaultValue,
                    title ?: id,
                    description,
                    enabled,
                    alertTitle,
                    formatter,
                    onChangeValue,
                    isSaveSetting,
                    customGrouping
                ).register()
            }
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
        val state = rememberDatePickerState(
            initialSelectedDate = LocalDate.of(value.year, value.month, value.dayOfMonth)
        )
        var isAlertOpen by retain { mutableStateOf(false) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(CSB.translator(title)) },
            description = { description?.let { Text(CSB.translator(it)) } },
            display = {
                DatePreview(value, formatter)
            },
            onClick = {
                if (enabled) isAlertOpen = true
            }
        )
        if (isAlertOpen) {
            AlertDialog(
                title = {
                    Text(CSB.translator(alertTitle))
                },
                text = {
                    DatePicker(
                        state = state,
                        modifier = Modifier,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isAlertOpen = false
                            state.getSelectedDate()?.let { changeValue(it) }
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
private fun DatePreview(
    time: LocalDate,
    formatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    val style = LocalSettingsStyle.current
    val shape = RoundedCornerShape(style.containerCornerShape)

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
                text = time.format(formatter),
                style = style.titleStyle
            )
        }
    }
}

