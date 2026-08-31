package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.group.Group
import com.daniil.csb.group.GroupSealed
import com.daniil.csb.screens.title.ScreenTitle
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.group.FragmentedScopeBuilder
import com.daniil.csb.group.GroupScope
import com.daniil.csb.settings.utils.SettingBuilder
import java.util.UUID

open class Screen internal constructor(
    open var id: String,
    open var title: ScreenTitle?,
    open var modifier: Modifier,
    open var paddingValues: PaddingValues,
    open var attribute: List<ScreenAttribute>? = null,
    open val settings: List<GroupSealed> = emptyList(),
    open val onCloseScreen: () -> Unit = {},
) {
    internal open val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }

    class Builder(val id: String) {
        private var title: ScreenTitle? = ScreenTitle.setText(id)
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private var attribute: List<ScreenAttribute>? = null
        private lateinit var settings: List<GroupSealed>
        private var onCloseScreen: () -> Unit = {}

        fun setTitle(title: ScreenTitle?) = apply { this.title = title }

        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = listOf(Group(id, settings = settings.toList()))
        }

        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) =
            apply { this.paddingValues = paddingValues }

        fun setAttribute(screenAttribute: List<ScreenAttribute>?) =
            apply { this.attribute = screenAttribute }

        fun setOnCloseScreen(onCloseScreen: () -> Unit) =
            apply { this.onCloseScreen = onCloseScreen }

        fun setGroupedContent(settings: List<GroupSealed>) = apply { this.settings = settings }

        fun build() = Screen(
            id,
            title,
            modifier ?: Modifier,
            paddingValues ?: PaddingValues.Zero,
            attribute,
            settings
        )
    }
}

@CsbDslMarkers
open class ScreenBuilderScope(val id: String): SettingBuilder() {
    var title: ScreenTitle? = ScreenTitle.setText(id)
    var modifier: Modifier? = Modifier
    var paddingValues: PaddingValues? = null
    var onCloseScreen: () -> Unit = {}
    val groupsHeap: MutableList<GroupSealed> = mutableListOf()
    fun group(
        id: String,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope(id).apply(groupScope)
        createNullableGroup()
        val group = Group(id, data.groupTitle, data.isHide, data.settings)
        groupsHeap.add(group)
    }


    fun group(
        groupScope: GroupScope.() -> Unit,
    ) {
        group(UUID.randomUUID().toString()) {
            groupScope()
            groupTitle = null
        }
    }

    fun fragmentedGroup(id: String, fragmentScope: FragmentedScopeBuilder.() -> Unit) {
        val data = FragmentedScopeBuilder(id).apply(fragmentScope)
        createNullableGroup()
        groupsHeap.add(data.build(id))
    }

    private fun createNullableGroup() {
        val settings = super.settings
        if (settings.isNotEmpty()) {
            groupsHeap.add(Group(UUID.randomUUID().toString(), null, false, settings.toList()))
            super.settings.clear()
        }
    }

    internal fun getData(): List<GroupSealed>  {
        createNullableGroup()
        return groupsHeap
    }
}

