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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

class PasswordField internal constructor(
    override var id: String,
    override val defaultValue: String,
    override val title: String,
    override val description: String?,
    val label: (@Composable () -> Unit)?,
    enabled: Boolean = true,
    override var onChangeValue: (String) -> Unit = {},
    val isError: (String) -> Boolean = { false },
    val onFocusChange: (Boolean) -> Unit = {},
    override var isSaveSetting: Boolean = false,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<String>() {
    private var _value = MutableStateFlow(defaultValue)
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
    class PasswordFieldBuilderScope() : SettingDefaultScope() {
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
        override fun SettingDslInterface.create(
            id: String,
            scope: PasswordFieldBuilderScope.() -> Unit
        ): SettingToken<PasswordField> {
            val data = PasswordFieldBuilderScope().apply(scope)
            return with(data) {
                PasswordField(
                    id,
                    defaultValue,
                    title ?: id,
                    description,
                    label,
                    enabled,
                    onChangeValue,
                    isError,
                    onFocusChange,
                    @Suppress("DEPRECATION")
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
        val groupPosition = LocalGroupPosition.current
        val value by this.value.collectAsState()
        val focusRequest = remember { FocusRequester() }
        var isPasswordVisible by retain { mutableStateOf(false) }

        DefaultContainer(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            paddingValues = PaddingValues(2.dp),
            onClick = null
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .height(style.minHeight)
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
        }
    }
}