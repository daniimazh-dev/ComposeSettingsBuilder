package com.daniil.csb.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingConfiguredToken
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settings.settingcore.clippedShape
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class Custom<T : Any> internal constructor(
    override var id: String,
    override val defaultValue: T,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    override var isSaveSetting: Boolean,
    val onClick: () -> Unit,
    val content: (@Composable CustomContentScope.() -> Unit)?,
    val contentMode: ContentMode = ContentMode.WithContainer,
    val contentWithArrangement: CustomContentWithArrangementScope?,
    override val onChangeValue: (T) -> Unit,
    val serializer: KSerializer<T>? = null,
    override val customGrouping: GroupItemClip? = null,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<T>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow(this@Custom.defaultValue)
    override val value = _value.asStateFlow()
    override val title: String = ""
    override val description: String? = null

    override fun loadLogic(pack: SaveSettingPackage) {
        val data = Json.decodeFromString(serializer as DeserializationStrategy<T>, (pack as SaveSettingPackage.JsonPackage).value)
        enabled(pack.enable)
        changeValue(data)
    }

    override fun saveLogic(): SaveSettingPackage? {
        return saveJson(serializer)
    }

    override fun changeValue(newValue: T) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class CustomBuilderScope<T>(): SettingDefaultScope() {
        var defaultValue: T? = null
        internal var content: (@Composable CustomContentScope.() -> Unit)? = null // Nullable for use default UI method
        internal var contentWithArrangement: CustomContentWithArrangementScope? = null
        var onChangeValue: (T) -> Unit = {}
        var onClick: () -> Unit = {}
        var serializer: KSerializer<T>? = null


        override var icon: SettingIcon? = null
        override var badge: SettingBadge? = null

        internal var contentMode: ContentMode = ContentMode.WithContainer
        fun setContent(
            content: @Composable CustomContentScope.() -> Unit
        ): SetContentToken {
            contentMode = ContentMode.Nothing
            this.content = content
            return SetContentToken()
        }
        fun setIntoContainer(
            content: @Composable CustomContentScope.() -> Unit
        ): SetContentToken {
            contentMode = ContentMode.WithContainer
            this.content = content
            return SetContentToken()
        }
        fun setWithArrangement(
            contentWithArrangement: CustomContentWithArrangementScope.() -> Unit
        ): SetContentToken {
            contentMode = ContentMode.WithArrangement
            val data = CustomContentWithArrangementScope().apply {
                badge = { this@CustomBuilderScope.badge?.content() }
                icon =  { this@CustomBuilderScope.icon?.content() }
                contentWithArrangement()
            }
            this.contentWithArrangement = data
            return SetContentToken()
        }
        fun useEmptyContent(): SetContentToken {
            contentMode = ContentMode.Nothing
            this.content = {}
            return SetContentToken()
        }
    }

    enum class ContentMode {
        Nothing,
        WithContainer,
        WithArrangement
    }

    open class CustomContentScope internal constructor()

    class CustomContentWithArrangementScope: CustomContentScope() {
        var modifier = Modifier
        var title: @Composable () -> Unit = {}
        var icon: (@Composable () -> Unit)? = null
        var description: @Composable () -> Unit = {}
        var badge: (@Composable () -> Unit)? = null
        var action: @Composable () -> Unit = {}
        var display: @Composable () -> Unit = {}
    }

    class SetContentToken: SettingConfiguredToken()
    companion object  {
        fun <T : Any> SettingDslInterface.create(
            id: String,
            scope: CustomBuilderScope<T>.() -> SetContentToken
        ): SettingToken<Custom<T>> {
            val data = CustomBuilderScope<T>().apply { scope() }
            with(data) {
                defaultValue ?: error("Default value must be not null in custom setting $id")
                return Custom(id, defaultValue!!, enabled, visible, icon, badge, isSaveSetting,onClick, content, contentMode, contentWithArrangement, onChangeValue, serializer, customGrouping, depends).register()
            }
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {

        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        when (contentMode) {
            ContentMode.Nothing -> {
                content?.let { CustomContentScope().content() }
            }
            ContentMode.WithContainer -> {
                if (content != null) {
                    LocalSettingsStyle.current.ContainerSlot(
                        modifier = modifier.fillMaxWidth(),
                        isFocused = focusState,
                        shape = (position ?: customGrouping ?: LocalGroupPosition.current).clippedShape(),
                        enabled = enabled,
                        minHeight = LocalSettingsStyle.current.minHeight,
                        onClick = onClick,
                        content = {  CustomContentScope().content() }
                    )
                }
            }
            ContentMode.WithArrangement -> {
                if (contentWithArrangement != null) {
                    val inContent = contentWithArrangement
                    DefaultSettingUI(
                        title = inContent.title,
                        modifier = inContent.modifier,
                        isFocused = focusState,
                        groupItemClip = customGrouping ?: position,
                        enabled = enabled,
                        icon = SettingIcon.custom { inContent.icon?.invoke() },
                        badge = SettingBadge.custom { inContent.badge?.invoke() },
                        description = inContent.description,
                        action = inContent.action,
                        display = inContent.display,
                        onClick = onClick,
                    )
                }
            }
        }

    }
}
