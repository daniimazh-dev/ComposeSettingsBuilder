package com.daniil.csb.classes

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.daniil.csb.R
import com.daniil.csb.ScreenInstance
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Redirect(
    id: String,
    redirectTo: ScreenInstance,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    val navigationModel: SettingsNavigationModel,
): SettingsSealed<ScreenInstance>() {

    private var _value = MutableStateFlow(redirectTo)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }
    override fun changeValue(newValue: ScreenInstance) { _value.value = newValue }

    override fun fetchValue(): StateFlow<ScreenInstance> = value

    class RedirectBuilderScope() {
        lateinit var redirectTo: ScreenInstance
        lateinit var navigationModel: SettingsNavigationModel
        var title = "Redirect"
        var description = ""
        var enabled = true
    }
    class Builder(
        val id: String,
        builderScope: RedirectBuilderScope.() -> Unit
    ) {
        val scope = RedirectBuilderScope().apply(builderScope)
        fun create(): Redirect = with(scope) {
            return Redirect(id, redirectTo, title, description, enabled, navigationModel)
        }
    }


    override var id: String = id
    @Composable
    override fun UI() {
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        DefaultContainer(
            modifier = Modifier,
            enabled = enabled,
            title = { Text(title)  },
            description = { Text(description) },
            display = {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "Go to $value"
                )
            },
            onClick = {
                navigationModel.goToScreen(value)
            }
        )
    }
}