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
import com.daniil.csb.group.AbstractGroup
import com.daniil.csb.group.Group
import com.daniil.csb.isInFlag
import com.daniil.csb.screens.CustomScreen.CustomContentScreenScope
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingBuilder
import com.daniil.csb.settingui.LocalDebugData
import com.daniil.csb.settingui.LocalSettingsStyle

open class CustomScreen internal constructor(
    id: String,
) : Screen(id) {
     internal constructor(
         id: String,
         registeredSettings: List<ComposeSetting<*>>,
         modifier: Modifier,
         paddingValues: PaddingValues,
         attribute: List<ScreenAttribute>,
         onCloseScreen: () -> Unit,
         topBar: TopScreenBar?,
         content:  @Composable CustomContentScreenScope.() -> Unit
     ): this(id) {
         this.modifier = modifier
         this.paddingValues = paddingValues
         this.topBar = topBar
         this.attribute = attribute
         this.registeredSettings = registeredSettings
         this.settings = if ("allowDisplayAbstractScreen".isInFlag())
            listOf(Group(id, null, true, registeredSettings))
            else listOf(AbstractGroup(id, registeredSettings))
         this.onCloseScreen = onCloseScreen
         this.content = content
     }

    internal var content: @Composable CustomContentScreenScope.() -> Unit = {}
    private var registeredSettings: List<ComposeSetting<*>> = emptyList()

    @CsbDslMarkers
    inner class CustomContentScreenScope {
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
            ).takeIf { attribute.contains(ScreenAttribute.Debag) }
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
        private val settings = mutableListOf<ComposeSetting<*>>()
        private lateinit var content: @Composable CustomContentScreenScope.() -> Unit
        private var paddingValues = PaddingValues.Zero
        private var attribute: List<ScreenAttribute> = emptyList()
        private var modifier: Modifier = Modifier
        private var onCloseScreen: () -> Unit = {}
        private var topBar: TopScreenBar? = null

        fun registerSettings(vararg items: ComposeSetting<*>) = apply {
            this.settings.addAll(items)
        }
        fun setTopBar(topBar: TopScreenBar?) = apply { this.topBar = topBar }
        fun setModifier(modifier: Modifier) = apply { this.modifier = modifier }
        fun setOnCloseScreen(onCloseScreen: () -> Unit) = apply { this.onCloseScreen = onCloseScreen }
        fun setContent(content: @Composable CustomContentScreenScope.() -> Unit) = apply {
            this.content = content
        }
        fun setAttribute(screenAttribute: List<ScreenAttribute>) = apply { this.attribute = screenAttribute }
        fun build(): CustomScreen {
            return CustomScreen(id, settings, modifier, paddingValues, attribute, onCloseScreen, topBar, content)
        }
    }

    @Composable
    internal fun Render() {
        val scope = remember { CustomContentScreenScope() }
        if (attribute.contains(ScreenAttribute.Debag)) {
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
open class CustomBuilderScreenScope internal constructor(id: String): SettingBuilder() {
    var modifier: Modifier = Modifier
    var onCloseScreen: () -> Unit = {}
    var topBar: TopScreenBar? = null
    internal var content: @Composable CustomContentScreenScope.() -> Unit  = { AllSettings() }
        private set

    fun setContent(content: @Composable (CustomContentScreenScope.() -> Unit)): ContentConfiguredToken {
        this.content = content
        return ContentConfiguredToken()
    }
    fun useEmptyContent(): ContentConfiguredToken {
        this.content = {}
        return ContentConfiguredToken()
    }
}
