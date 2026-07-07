package com.daniil.csb.settings

import androidx.compose.foundation.layout.PaddingValues
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
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.screens.Screen
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer

class Redirect internal constructor(
    override var id: String,
    var redirectToId: String,
    val focus: String? = null,
    var showArrow: Boolean = true,
    override val title: String,
    override val description: String?,
    val labelIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    var onRedirect: (Screen) -> Unit = {},
    val navigationModel: SettingsNavigationModel = CSB.navigationModel,
): ComposeSetting<Screen>() {
    internal constructor(
        id: String,
        redirectTo: Screen,
        focus: String? = null,
        showArrow: Boolean = true,
        title: String,
        description: String?,
        labelIcon: (@Composable () -> Unit)? = null,
        enabled: Boolean = true,
        onRedirect: (Screen) -> Unit = {},
        navigationModel: SettingsNavigationModel = CSB.navigationModel,
    ): this(id, redirectTo.id,  focus, showArrow, title, description, labelIcon, enabled, onRedirect, navigationModel)

    override val defaultValue: Screen = Screen(
        id = "", settings = mapOf(),
        title = "",
        modifier = Modifier,
        paddingValues = PaddingValues.Zero,
    )
    private val _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()


    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()
    override val onChangeValue: (Screen) -> Unit
        get() = { onRedirect(value.value) }
    override var isSaveSetting: Boolean = false

    override fun enabled(state: Boolean) { _enable.value = state }
    override fun changeValue(newValue: Screen) { redirectToId = newValue.id }
    fun changeValue(newValue: String) { redirectToId = newValue }
    override fun fetchValue(): StateFlow<Screen> = MutableStateFlow(navigationModel.findScreenById(redirectToId))
    override fun resetToDefault() {}

    override fun saveLogic(serializer: KSerializer<Screen>?): SaveSettingPackage {
        return SaveSettingPackage.UnitPackage(id, enabled.value)
    }



    @CsbDslMarkers
    class RedirectBuilderScope() {
        var redirectTo: Screen? = null
        var redirectToId: String? = null
        var focus: String? = null
        var showArrow: Boolean = true
        var onRedirect: (Screen) -> Unit = {}
        var navigationModel: SettingsNavigationModel = CSB.navigationModel
        var title: String? = null
        var description: String? = null
        var labelIcon: (@Composable () -> Unit)? = null
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
                redirectToId != null -> Redirect(id, redirectToId!!, focus, showArrow, title ?: id, description, labelIcon, enabled, onRedirect, navigationModel)
                redirectTo != null -> Redirect(id, redirectTo!!, focus, showArrow, title ?: id, description, labelIcon, enabled, onRedirect, navigationModel)
                else -> error("Not found redirect parameter")
            }
            return res
        }
    }


    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        fun execute() {
            if (focus != null) {
                navigationModel.navigateToSetting(focus)
            } else {
                navigationModel.goToScreen(navigationModel.findScreenById(redirectToId))
            }
            onRedirect(value.value)
        }
        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if(!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
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

