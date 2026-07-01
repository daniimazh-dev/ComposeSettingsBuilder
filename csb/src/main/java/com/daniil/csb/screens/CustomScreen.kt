package com.daniil.csb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.DebugData
import com.daniil.csb.LocalDebugData
import com.daniil.csb.R
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.classes.utils.ScreenBuilder
import com.daniil.csb.screens.CustomScreen.CustomScreenScope
import com.daniil.csb.settingui.styles.LocalSettingsStyle

open class CustomScreen internal constructor(
    override var id: String,
    override var title: String?,
    settings: List<ComposeSetting<*>>,
    override var modifier: Modifier,
    override var paddingValues: PaddingValues,
    var content: @Composable CustomScreenScope.() -> Unit,
    override var attribute: List<ScreenAttribute>? = null,
) : Screen(id, title, modifier, paddingValues) {


    private var registeredSettings: MutableList<ComposeSetting<*>> = settings.toMutableList()

    override var settings: Map<Group, List<ComposeSetting<*>>>
        get() = mapOf(Group(id, false) to registeredSettings)
        set(value) {}

    inner class CustomScreenScope {
        @Composable
        fun AllSetting() {
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

        fun registerSettings(vararg items: ComposeSetting<*>) = apply {
            this.builderSettings.addAll(items)
        }

        fun setTitle(title: String?) = apply { this.title = title }
        fun setModifier(modifier: Modifier) = apply { this.modifier = modifier }
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


open class CreateCustomScreenScope() {
    var modifier: Modifier = Modifier
    var title: String? = null
    val registeredSettings = mutableListOf<ComposeSetting<*>>()
    var content: @Composable CustomScreenScope.() -> Unit = { AllSetting() }

    fun register(registerScope: RegisterScope.() -> Unit) {
        val data = RegisterScope().apply(registerScope)
        this.registeredSettings.addAll(data.settings)
    }
}

class RegisterScope() : SettingBuilder()

fun ScreenBuilder.createCustomScreen(
    id: String,
    screenAttribute: List<ScreenAttribute>? = null,
    scope: CreateCustomScreenScope.() -> Unit
): CustomScreen {
    val data = CreateCustomScreenScope().apply(scope)

    val screen =
        CustomScreen.Builder(id).setTitle(data.title)
            .setModifier(data.modifier)
            .registerSettings(*data.registeredSettings.toTypedArray())
            .setContent(data.content)
            .setAttribute(screenAttribute)
            .build()
    screen.addToHeap()
    return screen
}
