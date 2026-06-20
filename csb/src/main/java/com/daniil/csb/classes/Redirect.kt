package com.daniil.csb.classes

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.daniil.csb.CSB
import com.daniil.csb.R
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.screens.Screen
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Redirect(
    override var id: String,
    val redirectToId: String,
    val focus: String? = null,
    var showArrow: Boolean = true,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    var onRedirect: () -> Unit = {},
    val navigationModel: SettingsNavigationModel = CSB.navigationModel,
    override var isSaveSetting: Boolean
): ComposeSetting<Screen>() {
    constructor(
        id: String,
        redirectTo: Screen,
        focus: String? = null,
        showArrow: Boolean = true,
        title: String,
        description: String,
        enabled: Boolean = true,
        onRedirect: () -> Unit = {},
        navigationModel: SettingsNavigationModel = CSB.navigationModel,
        isSaveSetting: Boolean
    ): this(id,redirectTo.id,  focus, showArrow, title, description, enabled, onRedirect, navigationModel, isSaveSetting)
    override val value = MutableStateFlow(Screen(id = "", settings = mapOf(),))

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }
    override fun changeValue(newValue: Screen) { error("Cannot change redirect value") }
    override fun fetchValue(): StateFlow<Screen> { error("Cannot get redirect value") }
    override fun resetToDefault() {}

    override fun loadLogic(pack: SaveSettingPackage?) {
        if (pack == null) return
        enabled(pack.enable)
    }


    class RedirectBuilderScope() {
        var redirectTo: Screen? = null
        var redirectToId: String? = null
        var focus: String? = null
        var showArrow: Boolean = true
        var onRedirect: () -> Unit = {}
        var navigationModel: SettingsNavigationModel = CSB.navigationModel
        var title = "Redirect"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }
    class Builder(
        val id: String,
        builderScope: RedirectBuilderScope.() -> Unit
    ) {
        val scope = RedirectBuilderScope().apply(builderScope)
        fun create(): Redirect = with(scope) {
            val res = when {
                redirectToId != null -> Redirect(id, redirectToId!!, focus, showArrow, title, description, enabled, onRedirect, navigationModel, isSaveSetting)
                redirectTo != null -> Redirect(id, redirectTo!!, focus, showArrow, title, description, enabled, onRedirect, navigationModel, isSaveSetting)
                else -> error("Not found redirect parameter")
            }
            return res
        }
    }


    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        fun execute() {
            if (focus != null) {
                navigationModel.navigateToSetting(focus)
            } else {
                navigationModel.goToScreen(navigationModel.findScreenById(redirectToId))
            }
            onRedirect()
        }
        DefaultSettingUI(
            modifier = Modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(title) },
            description = { if(!title.isBlank()) Text(description) },
            display = {
                if (showArrow) {
                    FilledIconButton(
                        enabled = enabled,
                        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
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

fun SettingBuilder.createRedirect(
    id: String,
    builder: Redirect.RedirectBuilderScope.() -> Unit
): Redirect {
    return Redirect.Builder(id, builder).create()
}