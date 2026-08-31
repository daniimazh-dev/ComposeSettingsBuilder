package com.daniil.csb.settings

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.screens.Screen
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingConfiguredToken
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Redirect internal constructor(
    override var id: String,
    val redirectToId: String,
    val focus: String? = null,
    var showArrow: Boolean = true,
    override val title: String,
    override val description: String?,
    val labelIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    val onRedirect: (Screen) -> Unit = {},
    val navigationModel: SettingsNavigationModel = CSB.navigationModel,
    override val customGrouping: GroupItemClip? = null
): ComposeSetting<String>() {
    override val defaultValue: String = redirectToId

    private val _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()


    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()
    override val onChangeValue: (String) -> Unit
        get() = { onRedirect(navigationModel.findScreenById(value.value)) }
    override var isSaveSetting: Boolean = false

    override fun enabled(state: Boolean) { _enable.value = state }
    override fun changeValue(newValue: String) { _value.value = newValue }
    fun changeValue(newValue: Screen) { _value.value = newValue.id }

    @CsbDslMarkers
    class RedirectBuilderScope(): SettingDefaultScope() {
        var redirectToId: String? = null
            private set
        var focus: String? = null
        var showArrow: Boolean = true
        var onRedirect: (Screen) -> Unit = {}
        var navigationModel: SettingsNavigationModel = CSB.navigationModel
        var title: String? = null
        var description: String? = null
        var labelIcon: (@Composable () -> Unit)? = null
        @Deprecated("The Redirect setting dose not store any data. Changing the value to true is not necessary", level = DeprecationLevel.HIDDEN)
        override var isSaveSetting: Boolean = false
        fun setRedirect(redirectToId: String): InitRedirectToken {
            this.redirectToId = redirectToId
            return InitRedirectToken()
        }
        fun setRedirect(redirectToScreen: Screen): InitRedirectToken {
            this.redirectToId = redirectToScreen.id
            return InitRedirectToken()
        }
        fun setEmptyRedirect(): InitRedirectToken {
            redirectToId = ""
            return InitRedirectToken()
        }
    }
    class InitRedirectToken: SettingConfiguredToken()

    companion object : ComposeSettingInterface.FactoryWithToken<Redirect, RedirectBuilderScope, InitRedirectToken> {
        override fun SettingDslInterface.create(id: String, scope: RedirectBuilderScope.() -> InitRedirectToken): SettingToken<Redirect> {
            val data = RedirectBuilderScope()
            data.scope()
            return with(data) {
                Redirect(id, redirectToId!!, focus, showArrow, title ?: id, description, labelIcon, enabled, onRedirect, navigationModel, customGrouping)
            }.register()
        }
    }


    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        fun execute() {
            val targetScreen = navigationModel.findScreenById(redirectToId)
            if (focus != null) {
                navigationModel.navigateToSetting(focus)
            } else {
                navigationModel.goToScreen(targetScreen)
            }
            onRedirect(targetScreen)
        }
        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(CSB.translator(title)) },
            description = { description?.let { Text(CSB.translator(it)) } },
            icon = labelIcon,
            display = {
                if (showArrow) {
                    FilledIconButton(
                        enabled = enabled,
                        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = style.containerColor),
                        onClick = { execute() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_icon),
                            contentDescription = "Go to $redirectToId"
                        )
                    }
                }
            },
            onClick = { execute() }
        )
    }
}

