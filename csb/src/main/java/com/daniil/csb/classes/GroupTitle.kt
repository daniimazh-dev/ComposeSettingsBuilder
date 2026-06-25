package com.daniil.csb.classes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.screens.GroupScope
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GroupTitle internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    val content: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) : ComposeSetting<Unit>(independentObject = true) {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()
    override var isSaveSetting: Boolean = true
    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Unit) {}
    override fun resetToDefault() {}
    class GroupTitleBuilderScope() {
        var title: String? = null
        var description: String? = null
        var content: (@Composable () -> Unit)? = null
        var isSaveSetting = false
    }

    class Builder(
        val id: String,
        builderScope: GroupTitleBuilderScope.() -> Unit = {}
    ) {
        val scope = GroupTitleBuilderScope().apply(builderScope)
        fun create(): GroupTitle = with(scope) {
            return GroupTitle(id, title ?: id, description, content)
        }
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        val style = LocalSettingsStyle.current

        if (content == null) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val textStyle = style.titleStyle.copy(fontWeight = FontWeight.Bold)
                Text(
                    text = title,
                    maxLines = 1,
                    style = if (focusState) textStyle.copy(color = style.focusColor) else textStyle
                )
            }
        } else {
            content()
        }
    }
}

fun GroupScope.createGroupTitle(
    id: String,
    builder: GroupTitle.GroupTitleBuilderScope.() -> Unit = {}
): GroupTitle {
    val setting = GroupTitle.Builder(id, builder).create()
    setting.addToHeap()
    return setting
}