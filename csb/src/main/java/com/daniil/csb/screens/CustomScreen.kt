package com.daniil.csb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.DebugData
import com.daniil.csb.screens.CustomScreen.CustomScreenScope
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingBuilder
import com.daniil.csb.settingui.LocalDebugData
import com.daniil.csb.settingui.LocalSettingsStyle

open class CustomScreen internal constructor(
    override var id: String,
    override var title: String?,
    settings: List<ComposeSetting<*>>,
    override var modifier: Modifier,
    override var paddingValues: PaddingValues,
    var content: @Composable CustomScreenScope.() -> Unit,
    override var attribute: List<ScreenAttribute>? = null,
    override var onCloseScreen: () -> Unit = {},
) : Screen(id, title, modifier, paddingValues) {


    private var registeredSettings: MutableList<ComposeSetting<*>> = settings.toMutableList()

    override var settings: Map<Group, List<ComposeSetting<*>>>
        get() = mapOf(Group(id, null, false) to registeredSettings)
        set(value) {}

    inner class CustomScreenScope {
        @Composable
        fun AllSettings() {
            val style = LocalSettingsStyle.current
            Column(
                verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
            ) {
                val first = registeredSettings.firstOrNull()?.id ?: return
                val last = registeredSettings.last().id
                registeredSettings.forEach { setting ->
                    val groupPosition = when {
                        first == last -> GroupItemClip.Full
                        setting.id == first -> GroupItemClip.First
                        setting.id == last -> GroupItemClip.Last
                        else -> GroupItemClip.None
                    }
                    RegisteredSetting(setting, groupPosition)
                }
            }

        }

        @Composable
        fun RegisteredSetting(
            index: Int,
            groupItemClip: GroupItemClip = GroupItemClip.Full
        ) {
            val setting = registeredSettings.getOrNull(index) ?: return
            RegisteredSetting(setting, groupItemClip)
        }

        @Composable
        fun RegisteredSetting(
            setting: ComposeSetting<*>,
            groupItemClip: GroupItemClip = GroupItemClip.Full
        ) {
            val debagData = DebugData(
                settingSimpleName = setting::class.simpleName,
                settingId = setting.id,
                currentValue = setting.value
            ).takeIf { attribute?.contains(ScreenAttribute.Debag) == true }
            CompositionLocalProvider(LocalDebugData provides debagData) {
                setting.UI(position = groupItemClip)
            }

        }

        @Composable
        fun RegisteredSetting(
            id: String,
            groupItemClip: GroupItemClip = GroupItemClip.Full
        ) {
            RegisteredSetting(registeredSettings.find { it.id == id } ?: return, groupItemClip)
        }
    }


    class Builder(val id: String) {
        private val builderSettings = mutableListOf<ComposeSetting<*>>()
        private lateinit var content: @Composable CustomScreenScope.() -> Unit
        private var paddingValues = PaddingValues.Zero
        private var attribute: List<ScreenAttribute>? = null
        private var title: String? = null
        private var modifier: Modifier = Modifier
        private var onCloseScreen: () -> Unit = {}

        fun registerSettings(vararg items: ComposeSetting<*>) = apply {
            this.builderSettings.addAll(items)
        }

        fun setTitle(title: String?) = apply { this.title = title }
        fun setModifier(modifier: Modifier) = apply { this.modifier = modifier }
        fun setOnCloseScreen(onCloseScreen: () -> Unit) = apply{ this.onCloseScreen = onCloseScreen }
        fun setContent(content: @Composable CustomScreenScope.() -> Unit) = apply {
            this.content = content
        }
        fun setAttribute(screenAttribute: List<ScreenAttribute>?) = apply { this.attribute = screenAttribute }
        fun build(): CustomScreen {
            val instance =
                CustomScreen(id, title, builderSettings, modifier, paddingValues, content, attribute)
            return instance
        }
    }

    @Composable
    internal fun Render() {
        val scope = remember { CustomScreenScope() }
        if (attribute?.contains(ScreenAttribute.Debag) == true) {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Custom screen id: $id",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        scope.content()
    }
}

class ContentConfiguredToken internal constructor()
@CsbDslMarkers
open class CreateCustomScreenScope(): SettingBuilder() {
    var modifier: Modifier = Modifier
    var title: String? = null
    var onCloseScreen: () -> Unit = {}
    var content: @Composable CustomScreenScope.() -> Unit  = { AllSettings() }
        private set

    fun setContent(content: @Composable (CustomScreenScope.() -> Unit)): ContentConfiguredToken {
        this.content = content
        return ContentConfiguredToken()
    }
    fun useDefaultContent(): ContentConfiguredToken = ContentConfiguredToken()
}
fun ScreenBuilder.createCustomScreen(
    id: String,
    screenAttribute: List<ScreenAttribute>? = null,
    scope: CreateCustomScreenScope.() -> ContentConfiguredToken
): CustomScreen {
    val data = CreateCustomScreenScope()
    data.scope()

    val screen =
        CustomScreen.Builder(id).setTitle(data.title)
            .setModifier(data.modifier)
            .registerSettings(*data.settings.map { it.second }.toTypedArray())
            .setContent(data.content)
            .setOnCloseScreen(onCloseScreen = data.onCloseScreen)
            .setAttribute(screenAttribute)
            .build()
    screen.register()
    return screen
}
