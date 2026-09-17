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
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Action internal constructor(
    override var id: String,
    var requestAlert: Boolean,
    var onAction: (Boolean) -> Unit,
    val alertTitle: String?,
    val alertText: String?,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    val actionIcon: SettingIcon?,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    visible: Boolean = true,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<Unit>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false

    override val onChangeValue: (Unit) -> Unit = {}

    override fun changeValue(newValue: Unit) {}

    override val defaultValue: Unit = Unit

    @CsbDslMarkers
    class ActionBuilderScope(): SettingDefaultScope<Action>() {
        var requestAlert = false
        var onAction: (result: Boolean) -> Unit = {}
        var alertText: String? = null
        var alertTitle: String? = null
        var title: String? = null
        var actionIcon: SettingIcon? = null
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
                alertText,
                icon,
                badge,
                actionIcon,
                title ?: id,
                description,
                enabled,
                visible,
                customGrouping,
                depends
            ).register()
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val translator = LocalCSBTranslator.current
        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            icon = icon,
            badge = badge,
            title = { if (!title.isBlank()) Text(translator.translate(title)) },
            description = { description?.let { Text(translator.translate(it)) } },
            action = { actionIcon?.content() },
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
                    if (alertTitle?.isBlank() == false) Text(translator.translate(alertTitle))
                },
                text = {
                    Text(translator.translate(alertText.orEmpty()))
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
