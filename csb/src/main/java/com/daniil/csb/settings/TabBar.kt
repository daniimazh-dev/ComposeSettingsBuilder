package com.daniil.csb.settings


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.screens.FragmentController
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.utils.FancyTabBar
import com.daniil.csb.utils.FancyTabBarData
import com.daniil.csb.utils.FancyTabBarDefaults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TabBar internal constructor(
    override var id: String,
    override var defaultValue: String,
    var tabs: List<Tab> = emptyList(),
    enabled: Boolean = true,
    val controller: FragmentController? = null,
    override var onChangeValue: (String) -> Unit = {},
    override var isSaveSetting: Boolean = true,
) : ComposeSetting<String>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()
    override fun enabled(state: Boolean) {
        _enable.value = state
    }
    override val title: String = ""
    override val description: String = ""
    override fun changeValue(newValue: String) {
       controller?.changeFragment(newValue)
        onChangeValue(newValue)
        _value.value = newValue
    }

    data class Tab(
        val id: String,
        val content: @Composable () -> Unit
    )

    @CsbDslMarkers
    class TabBarBuilderScope() {
        var controller: FragmentController? = null
        var defaultValue: String? = null
        val tabs = mutableListOf<Tab>()
        var enabled = true
        var onChangeValue: (String) -> Unit = {}
        fun tab(id: String, content: @Composable () -> Unit = { Text(id) }) {
            tabs.add(Tab(id, content))
        }
        operator fun Tab.unaryPlus() {
            tab(this.id, this.content)
        }
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: TabBarBuilderScope.() -> Unit = {}
    ) {
        val scope = TabBarBuilderScope().apply(builderScope)
        var controller: FragmentController? = scope.controller
        constructor(id: String, controller: FragmentController) : this(id, builderScope = {}
        ) { this.controller = controller }

        fun create(): TabBar = with(scope) {
            return TabBar(id, defaultValue ?: tabs.firstOrNull()?.id ?: "", tabs, enabled, this@Builder.controller, onChangeValue,  isSaveSetting)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?,
    ) {
        val style = LocalSettingsStyle.current
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()
        DefaultContainer(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            onClick = null,
            content = {
                if (controller == null) {
                    FancyTabBar(
                        modifier = modifier,
                        colors = FancyTabBarDefaults.colors().copy(
                            bgColor = Color.Transparent,
                            indicatorColor = style.activeColor
                        ),
                        selectedIndex = tabs.indexOfFirst { it.id == value },
                        entries = tabs.map { FancyTabBarData(it.id, it.content) },
                        onSelected = { changeValue(it) }
                    )
                } else {
                    FancyTabBar(
                        modifier = modifier,
                        colors = FancyTabBarDefaults.colors().copy(
                            bgColor = Color.Transparent,
                            indicatorColor = style.activeColor
                        ),
                        selectedIndex = controller.groups.collectAsState().value.values.indexOfFirst { it.id == value.ifBlank { controller.initialValue } } ?: 0,
                        entries = controller.groups.collectAsState().value.map { (string, group) -> Tab(string) { group.groupTitle?.UI() } }.map { FancyTabBarData(it.id, it.content) },
                        onSelected = { changeValue(it) }
                    )
                }
            }
        )

    }
}

