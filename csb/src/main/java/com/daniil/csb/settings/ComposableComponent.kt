package com.daniil.csb.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class ComposableComponent internal constructor(
    id: String?,
    val content: @Composable ComposableComponentScope.() -> Unit,
    override val depends: List<Depends> = emptyList(),
    enabled: Boolean = true,
    visible: Boolean = true
): ComposeSetting<Unit>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    override val id: String = id ?: UUID.randomUUID().toString()
    override val title: String = ""
    override val description: String? = null
    override val defaultValue: Unit = Unit
    override val value: StateFlow<Unit> = MutableStateFlow(Unit)
    override var isSaveSetting: Boolean = false
    override val customGrouping: GroupItemClip? = null
    override val onChangeValue: (Unit) -> Unit = {}
    override fun changeValue(newValue: Unit) {}

    internal lateinit var globalRegisteredSettingsProvider: (id: String) -> ComposeSetting<*>?

    internal fun setGlobalProvider(provider: (id: String) -> ComposeSetting<*>?)
    { globalRegisteredSettingsProvider = provider }

    @CsbDslMarkers
    inner class ComposableComponentScope {
        private fun getSetting(id: String): ComposeSetting<*>? {
            return globalRegisteredSettingsProvider(id)
        }
        @Composable
        fun RegisteredSetting(
            id: String,
            groupItemClip: GroupItemClip = GroupItemClip.Full
        ) {
            RegisteredSetting(getSetting(id) ?: return, groupItemClip)
        }
        @Composable
        fun RegisteredSetting(
            setting: ComposeSetting<*>,
            groupItemClip: GroupItemClip = GroupItemClip.Full
        ) {
            setting.UI(position = groupItemClip)
        }
    }

    companion object {
        fun SettingDslInterface.create(
            id: String?,
            enabled: Boolean = true,
            visible: Boolean = true,
            content: @Composable ComposableComponentScope.() -> Unit
        ): SettingToken<ComposableComponent> {
            return ComposableComponent(id, content, emptyList(), enabled, visible).register()
        }
    }

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?
    ) {
        val enabled by this.enabled.collectAsState()
        if (enabled) content(ComposableComponentScope())
    }

}
