package com.daniil.csb.settings

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingConfiguredToken
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class FilePicker<I, O> internal constructor(
    override var id: String,
    override val defaultValue: O,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    visible: Boolean = true,
    override var onChangeValue: (O) -> Unit = {},
    override var isSaveSetting: Boolean = false,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    val actionIcon: SettingIcon?,
    val contract: ActivityResultContract<I, O>,
    val input: I,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<O>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    override fun changeValue(newValue: O) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class FilePickerBuilderScope<I, O> : SettingDefaultScope<FilePicker<I, O>>() {
        var defaultValue: O? = null
        var title: String? = null
        var description: String? = null
        var actionIcon: SettingIcon? = null
        var onChangeValue: (O) -> Unit = {}
        var contract: ActivityResultContract<I, O>? = null
            private set
        fun setContract(contract: ActivityResultContract<I, O>?): InitContractToken {
            this.contract = contract
            return InitContractToken()
        }
        var input: I? = null

        @Deprecated(
            "The FilePicker setting does not store any data by default. Changing the value to true is not necessary",
            level = DeprecationLevel.HIDDEN
        )
        override var isSaveSetting: Boolean = false
    }
    class InitContractToken: SettingConfiguredToken()
    companion object {
        fun <I, O> SettingDslInterface.create(
            id: String,
            scope: FilePickerBuilderScope<I, O>.() -> InitContractToken
        ): SettingToken<FilePicker<I, O>> {
            val data = FilePickerBuilderScope<I, O>()
            data.scope()
            val contract = data.contract ?: error("ActivityResultContract must be specified for FilePicker $id")
            @Suppress("UNCHECKED_CAST")
            val input = data.input as I
            @Suppress("UNCHECKED_CAST")
            val defaultValue = data.defaultValue as O

            return FilePicker(
                id = id,
                defaultValue = defaultValue,
                title = data.title ?: id,
                description = data.description,
                enabled = data.enabled,
                visible = data.visible,
                onChangeValue = data.onChangeValue,
                actionIcon = data.actionIcon,
                icon = data.icon,
                badge = data.badge,
                contract = contract,
                input = input,
                customGrouping = data.customGrouping,
                depends = data.depends
            ).register()
        }
    }

    override val focusState = MutableStateFlow(false)
    lateinit var picker: ManagedActivityResultLauncher<I, O>
        private set

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?,
    ) {
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()
        picker = rememberLauncherForActivityResult(
            contract = contract
        ) { result ->
            changeValue(result)
        }

        val translator = LocalCSBTranslator.current
        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            icon = icon,
            badge = badge,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = { if (title.isNotBlank()) Text(translator.translate(title)) },
            description = { description?.let { Text(translator.translate(it)) } },
            action = {
                IconButton(onClick = { if (enabled) picker.launch(input) }) {
                    if (actionIcon != null) {
                        actionIcon.content()
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.files),
                            contentDescription = "File picker"
                        )
                    }
                }
            },
            onClick = {
                if (enabled) picker.launch(input)
            }
        )
    }
}
