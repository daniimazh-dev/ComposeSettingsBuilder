package com.daniil.csb.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingConfiguredToken
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

class Select(
    override var id: String,
    val options: List<Option>,
    override val defaultValue: Option,
    override val title: String,
    val alertTitle: String,
    override val description: String?,
    val uiMode: UIMode,
    enabled: Boolean = true,
    override var onChangeValue: (Option) -> Unit = {},
    override var isSaveSetting: Boolean,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Select.Option>() {
    private var _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Option) {
        if (!options.contains(newValue)) return
        onChangeValue(newValue)
        _value.value = newValue
    }

    fun changeValue(optionId: String) {
        val option = options.find { it.id == optionId }
        onChangeValue(option ?: return)
        _value.value = option
    }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringPackage(
            id = id,
            enable = enabled.value,
            value = value.value.id
        )
    }

    override fun loadLogic(pack: SaveSettingPackage) {
        if (isSaveSetting) changeValue(pack.value as String)
        enabled(pack.enable)
    }

    enum class UIMode {
        Alert,
        List,
        Chip,
        Dropdown,
    }

    @Serializable
    data class Option(
        val id: String,
        val title: String,
    ) {
        override fun toString(): String = id
    }

    @CsbDslMarkers
    class SelectBuilderScope() : SettingDefaultScope() {
        var options = mutableListOf<Option>()
        var defaultValueId: String? = null
        var title: String? = null
        var onChangeValue: (Option) -> Unit = {}
        var alertTitle = "Select item"
        var description: String? = null
        var uiMode = UIMode.Alert
        fun option(id: String, title: String): MoreThenZeroOptionToken {
            options.add(Option(id, title))
            return MoreThenZeroOptionToken()
        }
    }
    class MoreThenZeroOptionToken: SettingConfiguredToken()
    companion object : ComposeSettingInterface.FactoryWithToken<Select, SelectBuilderScope, MoreThenZeroOptionToken> {
        override fun SettingDslInterface.create(
            id: String,
            scope: SelectBuilderScope.() -> MoreThenZeroOptionToken
        ): SettingToken<Select> {
            val data = SelectBuilderScope()
            data.scope()
            return with(data) {
                Select(
                    id,
                    options,
                    defaultValue = options.first { it.id == (defaultValueId ?: options[0].id) },
                    title ?: id,
                    alertTitle,
                    description,
                    uiMode,
                    enabled,
                    onChangeValue,
                    isSaveSetting,
                    customGrouping
                ).register()
            }
        }
    }

    override val focusState = MutableStateFlow(false)

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        var selectId by retain(alertOpen) { mutableStateOf<String>(value.id) }

        val isOpenMode = uiMode == UIMode.List || uiMode == UIMode.Chip

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = {
                Column {
                    if (!title.isBlank()) Text(CSB.translator(title))
                    if (isOpenMode) description?.let {
                        Text(CSB.translator(it), style = style.descriptionStyle)
                    }
                }
            },
            description = {
                when (uiMode) {
                    UIMode.List -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            options.forEach {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { selectId = it.id },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(22.dp)
                                            .border(2.dp, style.activeColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row() {
                                            AnimatedVisibility(
                                                visible = it.id == selectId,
                                                exit = scaleOut(),
                                                enter = scaleIn()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .background(style.activeColor, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = CSB.translator(it.title),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                            }
                        }
                    }
                    UIMode.Chip -> {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            options.forEach {
                                val isSelected = it.id == selectId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectId = it.id },
                                    label = {
                                        Text(
                                            text = CSB.translator(it.title),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                painter = painterResource(R.drawable.check),
                                                contentDescription = "Checked",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                    else -> description?.let { Text(CSB.translator(it)) }
                }
            },
            display = {
                if (isOpenMode) return@DefaultSettingUI
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .widthIn(max = 112.dp),
                        text = CSB.translator(value.title),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = style.titleStyle
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilledIconButton(
                        enabled = enabled,
                        colors = IconButtonDefaults.iconButtonColors()
                            .copy(style.containerColor),
                        onClick = {
                            alertOpen = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_down),
                            contentDescription = "dropdown arrow"
                        )
                    }
                    if (alertOpen && uiMode == UIMode.Dropdown) {
                        DropdownMenu(
                            expanded = alertOpen,
                            shape = style.edgeGroupCorner,
                            onDismissRequest = { alertOpen = false },
                            content = {
                                options.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = CSB.translator(it.title),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        onClick = {
                                            changeValue(it.id)
                                            alertOpen = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            },
            onClick = if (!isOpenMode) { { alertOpen = true } } else null
        )

        if (alertOpen && uiMode == UIMode.Alert) {
            AlertDialog(
                title = {
                    if (!alertTitle.isBlank()) Text(CSB.translator(alertTitle))
                },
                text = {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        options.forEach {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        selectId = it.id
                                    },
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(22.dp)
                                        .border(2.dp, style.activeColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row() {
                                        AnimatedVisibility(
                                            visible = it.id == selectId,
                                            exit = scaleOut(),
                                            enter = scaleIn()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(style.activeColor, CircleShape)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = CSB.translator(it.title),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )


                            }

                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            changeValue(selectId)
                            alertOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            alertOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                onDismissRequest = {
                    alertOpen = false
                }
            )

        }
    }
}


