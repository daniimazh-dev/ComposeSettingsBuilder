package com.daniil.csb.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@OptIn(ExperimentalMaterial3Api::class)
class ProgressBar internal constructor(
    override var id: String,
    override val defaultValue: Float?,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    override var onChangeValue: (Float?) -> Unit = {},
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Float?>() {

    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false


    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

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
                    onChangeValue,
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

        DefaultContainer(
            modifier = modifier,
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = position,
            paddingValues =
                PaddingValues(
                    horizontal = style.horizontalPadding,
                    vertical = style.verticalPadding
                ),
            onClick = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = style.minHeight / 2)
            ) {

                if (!title.isBlank()) Text(text = CSB.translator(title), style = style.titleStyle)
                description?.let { Text(text = CSB.translator(it), style = style.descriptionStyle) }
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
            }
        }
    }


}

