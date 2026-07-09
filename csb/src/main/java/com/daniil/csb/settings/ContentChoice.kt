package com.daniil.csb.settings

import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContentChoice(
    override val id: String,
    override val title: String,
    override val description: String?,
    val contents: List<ChoiceOption>,
    override val defaultValue: String,
    override var isSaveSetting: Boolean,
    val contentSize: Dp,
    val uiMode: UIMode = UIMode.Grid,
    val gridCells: GridCells,
    enabled: Boolean = true,
    override val onChangeValue: (String) -> Unit
) : ComposeSetting<String>() {
    private val _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()
    private val _enabled = MutableStateFlow(enabled)
    override val enabled = _enabled.asStateFlow()

    override fun enabled(state: Boolean) {
        _enabled.value = state
    }

    override fun changeValue(newValue: String) {
        onChangeValue(newValue)
        _value.value = newValue
    }
    data class ChoiceOption(
        val id: String,
        val content:  @Composable (Boolean) -> Unit
    ) {
        override fun toString(): String = id
    }
    @CsbDslMarkers
    class ChoiceContentBuilderScope() {
        var contents = mutableListOf<ChoiceOption>()
        var defaultValueId: String? = null
        var minContentHeight: Dp = 78.dp
        var uiMode = UIMode.Grid
        var gridCells = GridCells.Fixed(2)
        var title: String? = null
        var onChangeValue: (String) -> Unit = {}
        var description: String? = null
        var enabled = true
        var isSaveSetting = true
        fun option(id: String, content: @Composable (Boolean) -> Unit) {
            +ChoiceOption(id, content)
        }
        operator fun ChoiceOption.unaryPlus() {
            contents.add(this)
        }
    }

    class Builder(
        val id: String,
        builderScope: ChoiceContentBuilderScope.() -> Unit
    ) {
        val scope = ChoiceContentBuilderScope().apply(builderScope)
        fun create(): ContentChoice = with(scope) {
            return ContentChoice(
                id, title ?: id, description, contents, defaultValueId
                    ?:contents.firstOrNull()?.id
                    ?: error("Parameter contents not set in setting id: \"$id\""),
                isSaveSetting, minContentHeight, uiMode, gridCells,enabled, onChangeValue
            )
        }
    }

    enum class UIMode {
        Row,
        Column,
        Grid
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?
    ) {
        val style = LocalSettingsStyle.current
        val focused by this.focusState.collectAsState()
        val enable by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        val spacedBy = 6.dp

        DefaultContainer(
            modifier = modifier,
            isFocused = focused,
            groupItemClip = position,
            paddingValues =
                PaddingValues(horizontal = style.horizontalPadding, vertical = style.verticalPadding),
            enabled = enable,
            onClick = null,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (!title.isBlank()) Text(text = title, style = style.titleStyle)
                        description?.let { Text(text = it, style = style.descriptionStyle) }
                    }
                    when (uiMode) {
                        UIMode.Row -> {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacedBy)
                            ) {
                                items(contents) { item ->
                                    val isSelected = value == item.id
                                    ChoiceItem(
                                        modifier = Modifier,
                                        isSelected = isSelected,
                                        contentSize = this@ContentChoice.contentSize,
                                        onClick = { if (enable) changeValue(item.id) }
                                    ) {
                                        item.content.invoke(isSelected)
                                    }
                                }
                            }
                        }
                        UIMode.Column -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(spacedBy)
                            ) {
                                items(contents) { item ->
                                    val isSelected = value == item.id
                                    ChoiceItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        isSelected = isSelected,
                                        contentSize = this@ContentChoice.contentSize,
                                        onClick = { if (enable) changeValue(item.id) }
                                    ) {
                                        item.content.invoke(isSelected)
                                    }
                                }
                            }
                        }

                        UIMode.Grid -> {
                            LazyVerticalGrid(
                                columns = gridCells,
                                horizontalArrangement = Arrangement.spacedBy(spacedBy),
                                verticalArrangement = Arrangement.spacedBy(spacedBy)
                            ) {
                                items(contents) { item ->
                                    val isSelected = value == item.id
                                    ChoiceItem(
                                        modifier = Modifier,
                                        isSelected = isSelected,
                                        contentSize = this@ContentChoice.contentSize,
                                        onClick = { if (enable) changeValue(item.id)  }
                                    ) {
                                        item.content.invoke(isSelected)
                                    }
                                }
                            }
                        }
                    }


                }
            }
        )
    }
}

@Composable
private fun ChoiceItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    contentSize: Dp,
    onClick: () -> Unit,
    content: @Composable (Boolean) -> Unit = {}
) {
    val style = LocalSettingsStyle.current
    val borderColor by animateColorAsState(
        if (isSelected) style.activeColor else style.containerColor
    )
    val animateScale by animateFloatAsState(
        if (isSelected) 1.1f else 1f
    )
    val shape = style.edgeGroupCorner
    Box(
        modifier = modifier
            .clip(shape)
            .heightIn(min = contentSize)
            .widthIn(min = contentSize)
            .border(4.dp, borderColor, shape)
            .combinedClickable(onClick = onClick)
            .graphicsLayer {
                scaleX = animateScale
                scaleY = animateScale
            }
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
        ) {
            content(isSelected)
        }
    }
}


