package com.daniil.csb.classes

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
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.ItemGroupPosition
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.String

class Action(
    override var id: String,
    var requestAlert: Boolean,
    var action: (Boolean) -> Unit,
    val alertTitle: String?,
    val icon: @Composable (() -> Unit)?,
    val text: String?,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow(Unit)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Unit) {}

    override fun resetToDefault() {}

    class ActionBuilderScope() {
        var requestAlert = false
        var action: (Boolean) -> Unit = {}
        var icon: (@Composable () -> Unit)? = null
        val text: String? = null
        var alertTitle: String? = null
        var title: String = "Action"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: ActionBuilderScope.() -> Unit
    ) {
        val scope = ActionBuilderScope().apply(builderScope)
        fun create(): Action = with(scope) {
            return Action(
                id,
                requestAlert,
                action,
                alertTitle,
                icon,
                text,
                title,
                description,
                enabled,
                isSaveSetting
            )
        }
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(position: ItemGroupPosition?) {
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()

        DefaultSettingUI(
            modifier = Modifier,
            focusState = focusState,
            itemGroupPosition = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { if (!description.isBlank()) Text(description) },
            display = { icon?.invoke() },
            onClick = {
                if (this@Action.requestAlert) {
                    alertOpen = true
                } else {
                    action(true)
                }
            }
        )
        if (alertOpen && this@Action.requestAlert) {
            AlertDialog(
                title = {
                    if (alertTitle?.isBlank() == false) Text(alertTitle)
                },
                text = {
                    Text(text.orEmpty())
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            action(true)
                            alertOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            action(false)
                            alertOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                onDismissRequest = {
                    action(false)
                    alertOpen = false
                }
            )

        }
    }
}

fun SettingBuilder.createAction(
    id: String,
    builder: Action.ActionBuilderScope.() -> Unit = { }
): Action {
    return Action.Builder(id, builder).create()
}