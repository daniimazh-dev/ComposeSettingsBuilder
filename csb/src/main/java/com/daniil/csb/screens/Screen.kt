package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.SettingsScreenModel
import com.daniil.csb.group.AbstractGroup
import com.daniil.csb.group.AbstractGroupScope
import com.daniil.csb.group.FragmentedScopeBuilder
import com.daniil.csb.group.Group
import com.daniil.csb.group.GroupScope
import com.daniil.csb.group.GroupSealed
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.isInFlag
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.SettingBuilder
import java.util.UUID

open class Screen internal constructor(
    open var id: String,
) {
    internal constructor(
        id: String,
        modifier: Modifier = Modifier,
        paddingValues: PaddingValues,
        topBar: TopScreenBar?,
        attribute: List<ScreenAttribute>,
        settings: List<GroupSealed>,
        onCloseScreen: () -> Unit,
    ): this(id) {
        this.modifier = modifier
        this.paddingValues = paddingValues
        this.topBar = topBar
        this.attribute = attribute
        this.settings = settings
        this.onCloseScreen = onCloseScreen
    }
    open var modifier: Modifier = Modifier
    open var paddingValues: PaddingValues = PaddingValues.Zero
    open var topBar: TopScreenBar? = TopScreenBar()
    open var attribute: List<ScreenAttribute> = emptyList()
    open var settings: List<GroupSealed> = emptyList()
    open var onCloseScreen: () -> Unit = {}

    internal open val settingsScreenModel: SettingsScreenModel by lazy { SettingsScreenModel(this) }

    class Builder(val id: String) {
        private var modifier: Modifier? = null
        private var paddingValues: PaddingValues? = null
        private var attribute: List<ScreenAttribute> = emptyList()
        private lateinit var settings: List<GroupSealed>
        private var onCloseScreen: () -> Unit = {}
        private var topBar: TopScreenBar? = null


        fun setContent(vararg settings: ComposeSetting<*>) = apply {
            this.settings = listOf(Group(id, settings = settings.toList()))
        }
        fun setTopBar(topBar: TopScreenBar?) = apply { this.topBar = topBar }
        fun setModifier(modifier: Modifier?) = apply { this.modifier = modifier }
        fun setPaddingValues(paddingValues: PaddingValues?) =
            apply { this.paddingValues = paddingValues }

        fun setAttribute(screenAttribute: List<ScreenAttribute>) =
            apply { this.attribute = screenAttribute }

        fun setOnCloseScreen(onCloseScreen: () -> Unit) =
            apply { this.onCloseScreen = onCloseScreen }

        fun setGroupedContent(settings: List<GroupSealed>) = apply { this.settings = settings }

        fun build() = Screen(
            id,
            modifier ?: Modifier,
            paddingValues ?: PaddingValues.Zero,
            topBar,
            attribute,
            settings,
            onCloseScreen
        )
    }
}

@CsbDslMarkers
open class ScreenBuilderScope internal constructor(id: String): SettingBuilder() {
    var modifier: Modifier? = Modifier
    var paddingValues: PaddingValues? = null
    var onCloseScreen: () -> Unit = {}
    var topBar: TopScreenBar? = TopScreenBar(id)
    internal val groupsHeap: MutableList<GroupSealed> = mutableListOf()
    fun group(
        id: String,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope(id).apply(groupScope)
        createNullableGroup()
        val group = Group(id, data.groupTitle, data.visible, data.settings)
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

    fun abstractGroup(id: String? = null, abstractGroupScope: AbstractGroupScope.() -> Unit) {
        val id = id ?: UUID.randomUUID().toString()
        val data = AbstractGroupScope(id).apply(abstractGroupScope)
        createNullableGroup()
        if ("allowDisplayAbstractGroup".isInFlag()) {
            groupsHeap.add(Group(id, GroupTitle.text(id), true, data.settings))
        } else {
            groupsHeap.add(AbstractGroup(id, data.settings))
        }
    }

    fun fragmentedGroup(id: String, fragmentScope: FragmentedScopeBuilder.() -> Unit) {
        val data = FragmentedScopeBuilder(id).apply(fragmentScope)
        createNullableGroup()
        groupsHeap.add(data.build(id))
    }

    private fun createNullableGroup() {
        if (settings.isNotEmpty()) {
            groupsHeap.add(Group(UUID.randomUUID().toString(), null, true, super.settings.toList()))
            super.settings.clear()
        }
    }

    internal fun getData(): List<GroupSealed>  {
        createNullableGroup()
        return groupsHeap
    }
}

