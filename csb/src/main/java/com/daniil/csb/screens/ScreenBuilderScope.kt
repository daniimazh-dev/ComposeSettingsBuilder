package com.daniil.csb.screens

import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder
import java.util.UUID

abstract class ScreenBuilderScope(): SettingBuilder() {
    val groupsHeap: MutableList<Screen.Group> = mutableListOf()
    fun group(
        id: String,
        hide: Boolean,
        groupScope: GroupScope.() -> Unit,
    ) {
        val data = GroupScope().apply(groupScope)
        val groupTitle = if (data.groupTitle is DefaultGroupTitle) GroupTitle(id) else data.groupTitle
        val group = Screen.Group(id, groupTitle,hide)
        groupsHeap.add(group)
        data.settings.forEach { (_, setting) -> super.addToHeap(setting, id) }
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
        group(UUID.randomUUID().toString(), false, groupScope)
    }


    internal fun getData(): Map<Screen.Group, List<ComposeSetting<*>>> {
        val result = LinkedHashMap<Screen.Group, MutableList<ComposeSetting<*>>>()
        val groupsUseCount = mutableMapOf<String, Int>()

        var lastGroupId: String? = "!initial_value!"
        var currentGroup: Screen.Group? = null

        super.settings.forEach { (groupId, setting) ->
            if (groupId != lastGroupId || currentGroup == null) {
                currentGroup = if (groupId == null) {
                    Screen.Group("default_" + UUID.randomUUID().toString())
                } else {
                    val count = groupsUseCount.getOrDefault(groupId, 0)
                    val instances = groupsHeap.filter { it.id == groupId }
                    val group = instances.getOrNull(count) ?: Screen.Group(groupId)
                    groupsUseCount[groupId] = count + 1
                    group
                }
                result[currentGroup] = mutableListOf(setting)
                lastGroupId = groupId
            } else {
                result[currentGroup]?.add(setting)
            }
        }

        return result
    }
}