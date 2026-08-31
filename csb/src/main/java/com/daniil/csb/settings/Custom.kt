package com.daniil.csb.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingConfiguredToken
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class Custom<T : Any> internal constructor(
    override var id: String,
    override val defaultValue: T,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean,
    val onClick: () -> Unit,
    val content: (@Composable CustomContentScope.() -> Unit)?,
    val contentMode: ContentMode = ContentMode.WithContainer,
    val contentWithArrangement: (CustomContentWithArrangementScope.() -> Unit)?,
    override val onChangeValue: (T) -> Unit,
    val serializer: KSerializer<T>? = null,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<T>() {
    private var _value = MutableStateFlow(this@Custom.defaultValue)
    override val value = _value.asStateFlow()
    override val title: String = ""
    override val description: String? = ""

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

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
        internal var contentWithArrangement: (CustomContentWithArrangementScope.() -> Unit)? = null
        var onChangeValue: (T) -> Unit = {}
        var onClick: () -> Unit = {}
        var serializer: KSerializer<T>? = null
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
            this.contentWithArrangement = contentWithArrangement
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
                return Custom(id, defaultValue!!, enabled, isSaveSetting,onClick, content, contentMode, contentWithArrangement, onChangeValue, serializer, customGrouping).register()
            }
        }
    }

    override val focusState = MutableStateFlow(false)
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
                    DefaultContainer(
                        modifier = modifier.fillMaxWidth(),
                        isFocused = focusState,
                        enabled = enabled,
                        groupItemClip = position ?: customGrouping,
                        onClick = onClick
                    ) {
                        CustomContentScope().content()
                    }
                }
            }
            ContentMode.WithArrangement -> {
                if (contentWithArrangement != null) {
                    val inContent = remember { CustomContentWithArrangementScope().apply(contentWithArrangement) }
                    DefaultSettingUI(
                        title = inContent.title,
                        modifier = inContent.modifier,
                        isFocused = focusState,
                        groupItemClip = customGrouping ?: position,
                        enabled = enabled,
                        icon = inContent.icon,
                        description = inContent.description,
                        display = inContent.display,
                        onClick = onClick,
                    )
                }
            }
        }

    }
}


