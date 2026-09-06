package com.daniil.csb.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
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


@OptIn(ExperimentalMaterial3Api::class)
class ProgressBar internal constructor(
    override var id: String,
    override val defaultValue: Float?,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    override var onChangeValue: (Float?) -> Unit = {},
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<Float?>(depends = depends, initialEnabled = enabled, initialVisible = visible) {

    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false

    override fun changeValue(newValue: Float?) {
        _value.value = newValue?.coerceIn(0f, 1f)
        onChangeValue(newValue?.coerceIn(0f, 1f))
    }

    @CsbDslMarkers
    class ProgressBarBuilderScope() : SettingDefaultScope() {
        var defaultValue: Float? = null
        var title: String? = null
        var description: String? = null
        var onChangeValue: (Float?) -> Unit = {}

        @Deprecated(
            "The Progress setting dose not store any data. Changing the value to true is not necessary",
            level = DeprecationLevel.HIDDEN
        )
        override var isSaveSetting: Boolean = false
    }

    companion object : ComposeSettingInterface.Factory<ProgressBar, ProgressBarBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: ProgressBarBuilderScope.() -> Unit
        )
                : SettingToken<ProgressBar> {
            val data = ProgressBarBuilderScope().apply(scope)
            return with(data) {
                ProgressBar(
                    id,
                    defaultValue,
                    title ?: id,
                    description,
                    enabled,
                    visible,
                    icon,
                    badge,
                    onChangeValue,
                    customGrouping,
                    depends
                ).register()
            }
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        val translator = LocalCSBTranslator.current

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = customGrouping ?: position,
            icon = icon,
            badge = badge,
            title = { if (title.isNotBlank()) Text(text = translator.translate(title)) },
            description = { description?.let { Text(text = translator.translate(it)) } },
            action = {},
            display = {
                if (value != null) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = style.verticalPadding),
                        progress = { value!! },
                        color = style.activeColor,
                        trackColor = style.containerColor,
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = style.verticalPadding),
                        color = style.activeColor,
                        trackColor = style.containerColor,
                    )
                }
            },
            onClick = null
        )
    }


}
