package com.daniil.csb.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniil.csb.R
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class StringData internal constructor(
    override var id: String,
    override val defaultValue: String,
    override val title: String,
    val alertTitle: String,
    val label: (@Composable () -> Unit)?,
    override val description: String?,
    enabled: Boolean = true,
    var onChangeValue: (String) -> Unit = {},
    override var isSaveSetting: Boolean
) : ComposeSetting<String>() {
    private var _value = MutableStateFlow(this@StringData.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: String) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringPackage(
            id = id,
            enable = enabled.value,
            value = value.value
        )
    }



    class StringDataBuilderScope() {
        var defaultValue: String = ""
        var title: String? = null
        var label: (@Composable () -> Unit)? = null
        var onChangeValue: (String) -> Unit = {}
        var alertTitle = "Edit value"
        var description: String? = null
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: StringDataBuilderScope.() -> Unit
    ) {
        val scope = StringDataBuilderScope().apply(builderScope)
        fun create(): StringData = with(scope) {
            return StringData(
                id,
                defaultValue,
                title ?: id,
                alertTitle,
                label,
                description,
                enabled,
                onChangeValue,
                isSaveSetting
            )
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        var text by retain { mutableStateOf(value.value) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
            display = {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .widthIn(max = 112.dp),
                        text = value.collectAsState().value,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilledIconButton(
                        enabled = enabled,
                        colors = IconButtonDefaults.iconButtonColors()
                            .copy(containerColor = style.containerColor),
                        onClick = {
                            alertOpen = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_icon),
                            contentDescription = "Edit"
                        )
                    }
                }
            },
            onClick = { alertOpen = true }
        )
        if (alertOpen) {
            AlertDialog(
                title = {
                    if (!alertTitle.isBlank()) Text(alertTitle)
                },
                text = {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = text,
                            singleLine = true,
                            onValueChange = { text = it },
                            label = label
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            changeValue(text)
                            alertOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            alertOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                onDismissRequest = {
                    alertOpen = false
                }
            )

        }
    }
}

fun SettingBuilder.createStringData(
    id: String,
    builder: StringData.StringDataBuilderScope.() -> Unit = {}
): StringData {
    val setting = StringData.Builder(id, builder).create()
    setting.addToHeap()
    return setting
}