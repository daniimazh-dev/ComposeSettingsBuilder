package com.daniil.csb.settings


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settings.settingcore.clippedShape
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import com.daniil.csb.styles.SettingComposer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasswordField internal constructor(
    override var id: String,
    override val defaultValue: String,
    override val title: String,
    override val description: String?,
    val label: (@Composable () -> Unit)?,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    override val onChangeValue: (String) -> Unit = {},
    val isError: (String) -> Boolean = { false },
    val onFocusChange: (Boolean) -> Unit = {},
    override var isSaveSetting: Boolean = false,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList(),
    val customComposer: SettingComposer? = null
) : ComposeSetting<String>(
    depends = depends,
    initialEnabled = enabled,
    initialVisible = visible
) {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    override fun changeValue(newValue: String) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class PasswordFieldBuilderScope() : SettingDefaultScope<PasswordField>() {
        var defaultValue: String = ""
        var title: String? = null
        var label: (@Composable () -> Unit)? = null
        var onChangeValue: (String) -> Unit = {}
        var onFocusChange: (Boolean) -> Unit = { }
        var isError: (String) -> Boolean = { false }
        var description: String? = null

        @Deprecated(
            "The PasswordField setting is not recommended to save",
            level = DeprecationLevel.WARNING
        )
        override var isSaveSetting: Boolean = false
    }

    companion object : ComposeSettingInterface.Factory<PasswordField, PasswordFieldBuilderScope> {
        context(context: SettingDslInterface)
        override fun create(
            id: String,
            scope: PasswordFieldBuilderScope.() -> Unit
        ): SettingToken<PasswordField> = with(PasswordFieldBuilderScope().apply(scope)) {
            return with(context) {
                PasswordField(
                    id = id,
                    defaultValue = defaultValue,
                    title = title ?: id,
                    description = description,
                    label = label,
                    enabled = enabled,
                    visible = visible,
                    icon = icon,
                    badge = badge,
                    onChangeValue = onChangeValue,
                    isError = isError,
                    onFocusChange = onFocusChange,
                    isSaveSetting =  @Suppress("DEPRECATION") isSaveSetting,
                    customGrouping = customGrouping,
                    depends = depends,
                    customComposer = composer
                ).register()
            }
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val groupPosition = LocalGroupPosition.current
        val value by this.value.collectAsState()
        val translator = LocalCSBTranslator.current
        val focusRequest = remember { FocusRequester() }
        var isPasswordVisible by retain { mutableStateOf(false) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            composer = customComposer,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            icon = icon,
            badge = badge,
            title = { if (title.isNotBlank()) Text(text = translator.translate(title)) },
            description = { description?.let { Text(text = translator.translate(it)) } },
            action = {},
            display = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequest)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                    value = value,
                    shape = (position ?: groupPosition).clippedShape(),
                    singleLine = true,
                    onValueChange = { changeValue(it) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = isError(value),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(painter =
                                if (isPasswordVisible) painterResource(R.drawable.visibility)
                                else painterResource(R.drawable.visibility_off),
                                contentDescription = "Password visibility"
                            )
                        }
                    },
                    label = label
                )
            },
            onClick = null
        )
    }
}
