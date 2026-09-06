package com.daniil.csb.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingConfiguredToken
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContentChoice(
    override val id: String,
    override val title: String,
    override val description: String?,
    val contents: List<ChoiceOption>,
    override val onChangeValue: (String) -> Unit,
    override val defaultValue: String,
    val contentSize: Dp,
    val uiMode: UIMode = UIMode.Grid,
    val gridCells: GridCells,
    val girdHeight: Dp,
    enabled: Boolean = true,
    visible: Boolean = true,
    override var isSaveSetting: Boolean,
    override val customGrouping: GroupItemClip?,
    override val depends: List<Depends> = emptyList()
) : ComposeSetting<String>(depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private val _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    override fun changeValue(newValue: String) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    data class ChoiceOption(
        val id: String,
        val content: @Composable (Boolean) -> Unit
    ) {
        override fun toString(): String = id
    }

    @CsbDslMarkers
    class ChoiceContentBuilderScope() : SettingDefaultScope() {
        var contents = mutableListOf<ChoiceOption>()
        var defaultValueId: String? = null
        var minContentHeight: Dp = 78.dp
        var uiMode = UIMode.Row
        var gridCells = GridCells.Fixed(4)
        var girdHeight = 86.dp
        var title: String? = null
        var onChangeValue: (String) -> Unit = {}
        var description: String? = null
        fun option(id: String, content: @Composable (Boolean) -> Unit): MoreThenZeroToken {
            contents.add(ChoiceOption(id, content))
            return MoreThenZeroToken()
        }
    }

    class MoreThenZeroToken internal constructor(): SettingConfiguredToken()
    companion object :
        ComposeSettingInterface.FactoryWithToken<ContentChoice, ChoiceContentBuilderScope, MoreThenZeroToken> {
        override fun SettingDslInterface.create(
            id: String,
            scope: ChoiceContentBuilderScope.() -> MoreThenZeroToken
        ): SettingToken<ContentChoice> {
            val data = ChoiceContentBuilderScope()
            data.scope()
            return with(data) {
                ContentChoice(
                    id,
                    title ?: id,
                    description,
                    contents,
                    onChangeValue,
                    defaultValueId
                        ?: contents.first().id,
                    minContentHeight,
                    uiMode,
                    gridCells,
                    girdHeight,
                    enabled,
                    visible,
                    isSaveSetting,
                    customGrouping,
                    depends
                ).register()
            }
        }
    }

    enum class UIMode {
        Row,
        Column,
        Grid
    }

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
        val translator = LocalCSBTranslator.current

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focused,
            groupItemClip = customGrouping ?: position,
            enabled = enable,
            title = { if (!title.isBlank()) Text(text = translator.translate(title)) },
            description = { description?.let { Text(text = translator.translate(it)) } },
            action = {},
            display = {
                when (uiMode) {
                    UIMode.Row -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacedBy)
                        ) {
                            contents.forEach { item ->
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacedBy)
                        ) {
                            contents.forEach { item ->
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
                            modifier = Modifier.height(girdHeight),
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
                                    onClick = { if (enable) changeValue(item.id) }
                                ) {
                                    item.content.invoke(isSelected)
                                }
                            }
                        }
                    }
                }
            },
            onClick = null
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
        if (isSelected) style.activeColor else style.containerColor, label = ""
    )
    val animateScale by animateFloatAsState(
        if (isSelected) 1.1f else 1f, label = ""
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
