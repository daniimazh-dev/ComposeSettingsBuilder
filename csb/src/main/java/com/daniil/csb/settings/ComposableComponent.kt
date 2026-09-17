package com.daniil.csb.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.depend.DependsScope
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    internal lateinit var globalRegistered: (id: String) -> ComposeSetting<*>?

    internal fun setGlobalProvider(provider: (id: String) -> ComposeSetting<*>?)
    { globalRegistered = provider }

    @CsbDslMarkers
    inner class ComposableComponentScope {
        val isEnabled = this@ComposableComponent.enabled
        internal lateinit var globalRegistered: (id: String) -> ComposeSetting<*>?
        private fun getSetting(id: String): ComposeSetting<*>? {
            return globalRegistered(id)
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
    @CsbDslMarkers
    class ComposableComponentConfigScope() {
        var enabled: Boolean = true
        var visible: Boolean = true
        internal var depends = emptyList<Depends>()
        fun depends(dependsScope: DependsScope<ComposableComponent>.() -> Unit) {
            val data = DependsScope<ComposableComponent>().apply(dependsScope)
            depends = data.getDepends()
        }
    }

    companion object {
        fun SettingDslInterface.create(
            id: String?,
            config: ComposableComponentConfigScope.() -> Unit,
            content: @Composable ComposableComponentScope.() -> Unit
        ): SettingToken<ComposableComponent> {
            val config = ComposableComponentConfigScope().apply(config)
            return ComposableComponent(id, content, config.depends, config.enabled, config.visible).register()
        }
    }

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?
    ) {
        val enabled by this.enabled.collectAsState()
        Box(
            modifier = Modifier
                .then(
                    if (enabled) Modifier else Modifier.alpha(0.5f).pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event  = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                )
        ) {
            content(ComposableComponentScope())
        }
    }

}
