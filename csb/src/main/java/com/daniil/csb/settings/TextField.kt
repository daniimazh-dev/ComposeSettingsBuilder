package com.daniil.csb.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settings.utils.clippedShape
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TextField internal constructor(
    override var id: String,
    override val defaultValue: String,
    override val title: String,
    val alertTitle: String,
    val openAlert: Boolean,
    val label: (@Composable () -> Unit)?,
    override val description: String?,
    enabled: Boolean = true,
    override var onChangeValue: (String) -> Unit = {},
    val onFocusChange: (Boolean) -> Unit = {},
    override var isSaveSetting: Boolean,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<String>() {
    private var _value = MutableStateFlow(this@TextField.defaultValue)
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

    @CsbDslMarkers
    class TextFieldBuilderScope(): SettingDefaultScope() {
        var defaultValue: String = ""
        var title: String? = null
        var label: (@Composable () -> Unit)? = null
        var onChangeValue: (String) -> Unit = {}
        var onFocusChange: (Boolean) -> Unit = { }
        var description: String? = null
    }

    companion object : ComposeSettingInterface.Factory<TextField, TextFieldBuilderScope> {
        override fun SettingDslInterface.create(id: String, scope: TextFieldBuilderScope.() -> Unit): SettingToken<TextField> {
            val data = TextFieldBuilderScope().apply(scope)
            return with(data) {
                TextField(
                    id,
                    defaultValue,
                    title ?: id,
                    "Set text",
                    false,
                    label,
                    description,
                    enabled,
                    onChangeValue,
                    onFocusChange,
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
        var isAlertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val groupPosition = LocalGroupPosition.current
        var text by retain { mutableStateOf(value.value) }
        val focusRequest = remember { FocusRequester() }
        if (isAlertOpen && !openAlert) {
            DefaultContainer(
                modifier = modifier,
                isFocused = focusState,
                groupItemClip = position,
                enabled = enabled,
                paddingValues = PaddingValues(2.dp),
                onClick = null
            ) {
                LaunchedEffect(Unit) {
                    focusRequest.requestFocus()
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .height(style.minHeight)
                        .focusRequester(focusRequest)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                    value = text,
                    shape = (position ?: groupPosition).clippedShape(),
                    singleLine = true,
                    onValueChange = { text = it },
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { isAlertOpen = false }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Dismiss"
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        isAlertOpen = false
                                        changeValue(text)
                                    }
                            ) {
                                Icon(painter = painterResource(R.drawable.check), contentDescription = "Confirm")
                            }
                        }
                    },
                    label = label
                )
            }
        }  else {
            DefaultSettingUI(
                modifier = modifier,
                isFocused = focusState,
                groupItemClip = customGrouping ?: position,
                enabled = enabled,
                title = { if (!title.isBlank()) Text(CSB.translator(title)) },
                description = { description?.let { Text(CSB.translator(it)) } },
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
                                isAlertOpen = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit_icon),
                                contentDescription = "Edit"
                            )
                        }
                    }

                },
                onClick = { isAlertOpen = true }
            )
        }
        if (isAlertOpen && openAlert) {
            AlertDialog(
                title = {
                    if (!alertTitle.isBlank()) Text(CSB.translator(alertTitle))
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
                            isAlertOpen = false
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

