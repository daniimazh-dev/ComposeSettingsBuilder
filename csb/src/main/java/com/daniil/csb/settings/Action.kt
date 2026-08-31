package com.daniil.csb.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Action internal constructor(
    override var id: String,
    var requestAlert: Boolean,
    var onAction: (Boolean) -> Unit,
    val alertTitle: String?,
    val icon: @Composable (() -> Unit)?,
    val text: String?,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow(Unit)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override var isSaveSetting: Boolean = false

    override val onChangeValue: (Unit) -> Unit = {}

    override fun changeValue(newValue: Unit) {}

    override val defaultValue: Unit = Unit

    @CsbDslMarkers
    class ActionBuilderScope(): SettingDefaultScope() {
        var requestAlert = false
        var onAction: (result: Boolean) -> Unit = {}
        var icon: (@Composable () -> Unit)? = null
        var alertText: String? = null
        var alertTitle: String? = null
        var title: String? = null
        var description: String? = null
        @Deprecated("The Action setting dose not store any data. Changing the value to true is not necessary", level = DeprecationLevel.HIDDEN)
        override var isSaveSetting: Boolean = false
    }
    companion object : ComposeSettingInterface.Factory<Action, ActionBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: ActionBuilderScope.() -> Unit
        ): SettingToken<Action> = with(ActionBuilderScope().apply(scope)) {
            return Action(
                id,
                requestAlert,
                onAction,
                alertTitle,
                icon,
                alertText,
                title ?: id,
                description,
                enabled,
                customGrouping
            ).register()
        }
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(CSB.translator(title)) },
            description = { description?.let { Text(CSB.translator(it)) } },
            display = { icon?.invoke() },
            onClick = {
                if (this@Action.requestAlert) {
                    alertOpen = true
                } else {
                    onAction(true)
                }
            }
        )
        if (alertOpen && this@Action.requestAlert) {
            AlertDialog(
                title = {
                    if (alertTitle?.isBlank() == false) Text(CSB.translator(alertTitle))
                },
                text = {
                    Text(CSB.translator(text.orEmpty()))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAction(true)
                            alertOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onAction(false)
                            alertOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                onDismissRequest = {
                    onAction(false)
                    alertOpen = false
                }
            )

        }
    }
}

