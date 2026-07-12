package com.daniil.csb.screens

import com.daniil.csb.settings.utils.SettingBuilder
import java.util.UUID
abstract class ScreenBuilderScope(): SettingBuilder() {
    val groupsHeap: MutableList<GroupSealed> = mutableListOf()
    fun group(
        id: String,
        hide: Boolean,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        createNullableGroup()
        val groupTitle = if (data.groupTitle is DefaultGroupTitle) GroupTitle(id) else data.groupTitle
        val group = Group(id, groupTitle, isHide = hide, data.settings)
        groupsHeap.add(group)
    }

    fun group(
        id: String,
        groupScope: GroupScope.() -> Unit,
    ) {
        group(id, false, groupScope)
    }

    fun group(
        groupScope: GroupScope.() -> Unit,
    ) {
        group(UUID.randomUUID().toString(), false) {
            groupScope()
            groupTitle = null
        }
    }

    fun fragmentedGroup(id: String, isHide: Boolean, fragmentScope: FragmentedScopeBuilder.() -> Unit) {
        val data = FragmentedScopeBuilder().apply(fragmentScope)
        createNullableGroup()
        groupsHeap.add(data.build(id, isHide))
    }

    fun fragmentedGroup(id: String, fragmentScope: FragmentedScopeBuilder.() -> Unit) {
        fragmentedGroup(id, false, fragmentScope)
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