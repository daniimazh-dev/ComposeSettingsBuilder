package com.daniil.csb.settings


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.FragmentController
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingConfiguredToken
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
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
    visible: Boolean = true,
    val icon: com.daniil.csb.settingui.SettingIcon? = null,
    val badge: SettingBadge? = null,
    val controller: FragmentController? = null,
    override var onChangeValue: (String) -> Unit = {},
    override var isSaveSetting: Boolean = true,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<String>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

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


    object Default {
        @Composable
        fun DefaultTabContent(text: String) {
            val translator = LocalCSBTranslator.current
            Text(
                text = translator.translate(text),
                style = LocalSettingsStyle.current.titleStyle,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    @CsbDslMarkers
    class TabBarBuilderScope() : SettingDefaultScope<TabBar>() {
        internal var controller: FragmentController? = null
            private set
        var defaultValue: String? = null
        val tabs = mutableListOf<Tab>()
        var onChangeValue: (String) -> Unit = {}
        fun tab(
            id: String, content: @Composable () -> Unit
            = { Default.DefaultTabContent(id) }
        ): MoreThenZeroTabToken {
            tabs.add(Tab(id, content))
            return MoreThenZeroTabToken()
        }

        fun setController(controller: FragmentController): InitControllerToken {
            this.controller = controller
            return InitControllerToken()
        }
    }

    class MoreThenZeroTabToken : TabBarConfiguredToken()
    class InitControllerToken : TabBarConfiguredToken()
    open class TabBarConfiguredToken() : SettingConfiguredToken()

    companion object :
        ComposeSettingInterface.FactoryWithToken<TabBar, TabBarBuilderScope, TabBarConfiguredToken> {
        override fun SettingDslInterface.create(
            id: String,
            scope: TabBarBuilderScope.() -> TabBarConfiguredToken
        ): SettingToken<TabBar> {
            val data = TabBarBuilderScope()
            data.scope()
            return with(data) {
                TabBar(
                    id,
                    defaultValue ?: controller?.initialValue ?: tabs.firstOrNull()?.id.orEmpty(),
                    tabs,
                    enabled,
                    visible,
                    icon,
                    badge,
                    controller,
                    onChangeValue,
                    isSaveSetting,
                    customGrouping,
                    depends
                ).register()
            }
        }

        fun SettingDslInterface.create(
            id: String,
            controller: FragmentController
        ): SettingToken<TabBar> {
            val groups = controller.groups.value.values
            val tabs = groups.map { g ->
                Tab(g.id) {
                    g.groupTitle?.content?.let {
                        it(GroupTitle.GroupTitleContentScope())
                    } ?: Default.DefaultTabContent(g.id)
                }
            }
            return TabBar(id, controller.initialValue, tabs, true, true, null, null, controller).register()
        }
    }

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?,
    ) {
        val style = LocalSettingsStyle.current
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            icon = icon,
            badge = badge,
            paddingValues = PaddingValues.Zero,
            minHeight = 0.dp,
            title = {},
            action = {},
            display = {
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
                    val groups by controller.groups.collectAsState()
                    var selectedIndex by retain { mutableIntStateOf(0) }
                    val currentFragmentId by controller.currentFragmentId.collectAsState()
                    LaunchedEffect(value) {
                        selectedIndex =
                            groups.keys.indexOfFirst { it == value.ifBlank { controller.initialValue } }
                    }
                    LaunchedEffect(currentFragmentId) {
                        selectedIndex =
                            groups.keys.indexOfFirst { it == currentFragmentId.ifBlank { groups.keys.firstOrNull() } }
                    }
                    FancyTabBar(
                        modifier = modifier,
                        colors = FancyTabBarDefaults.colors().copy(
                            bgColor = Color.Transparent,
                            indicatorColor = style.activeColor
                        ),
                        selectedIndex = selectedIndex,
                        entries = controller.groups.collectAsState().value.map { (string, group) ->
                            FancyTabBarData(string) {
                                group.groupTitle?.content?.let { it(GroupTitle.GroupTitleContentScope()) }
                                    ?: Default.DefaultTabContent(string)
                            }
                        },
                        onSelected = { changeValue(it) }
                    )
                }
            },
            onClick = null
        )
    }


}
