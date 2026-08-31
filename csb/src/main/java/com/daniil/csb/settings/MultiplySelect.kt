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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.toMutableStateList
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
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable


class MultiplySelect internal constructor(
    override var id: String,
    val options: List<Option>,
    override val defaultValue: List<Option>,
    override val title: String,
    val alertTitle: String,
    override val description: String?,
    val uiMode: UIMode,
    enabled: Boolean = true,
    override var onChangeValue: (List<Option>) -> Unit = {},
    override var isSaveSetting: Boolean,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<List<MultiplySelect.Option>>() {
    private var _value = MutableStateFlow(this@MultiplySelect.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: List<Option>) {
        if (!newValue.all { options.contains(it) }) return
        onChangeValue(newValue)
        _value.value = newValue
    }

    @JvmName(name = "ChangeValueWithId")
    fun changeValue(optionIds: List<String>) {
        val option = options.filter { it.id in optionIds }
        onChangeValue(option)
        _value.value = option
    }

    fun changeValue(optionId: String) {
        val option = options.find { it.id == optionId } ?: return
        val newValue = value.value + listOf(option)
        onChangeValue(newValue)
        _value.value = newValue
    }


    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringListPackage(
            id = id,
            enable = enabled.value,
            value = value.value.map { it.id }
        )
    }

    override fun loadLogic(pack: SaveSettingPackage) {
        if (isSaveSetting) changeValue(options.filter { it.id in pack.value as List<*> })
        enabled(pack.enable)
    }

    enum class UIMode {
        Dropdown,
        Alert,
        Chip,
        List
    }

    @Serializable
    data class Option(
        val id: String,
        val title: String,
    ) {
        override fun toString(): String = id
    }

    @CsbDslMarkers
    class MultiplySelectBuilderScope() : SettingDefaultScope() {
        var defaultValue: List<String> = emptyList()
        var options = mutableListOf<Option>()
        var onChangeValue: (List<Option>) -> Unit = {}
        var title: String? = null
        var alertTitle = "Select multiple"
        var description: String? = null
        var uiMode = UIMode.Alert
        fun option(id: String, title: String) {
            +Option(id, title)
        }

        operator fun Option.unaryPlus() {
            options.add(this)
        }
    }

    companion object : ComposeSettingInterface.Factory<MultiplySelect, MultiplySelectBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: MultiplySelectBuilderScope.() -> Unit
        ): SettingToken<MultiplySelect> {
            val data = MultiplySelectBuilderScope().apply(scope)
            return with(data) {
                MultiplySelect(
                    id,
                    options,
                    defaultValue = options.filter { it.id in defaultValue },
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
        val selectList = retain { value.value.toMutableStateList() }
        val isOpenMode = uiMode == UIMode.List || uiMode == UIMode.Chip
        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            title = {
                if (!title.isBlank()) Text(CSB.translator(title))
                if (isOpenMode) description?.let {
                    Text(text = CSB.translator(it), style = style.descriptionStyle)
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
                            options.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            if (option in selectList) selectList.remove(option)
                                            else selectList.add(option)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(22.dp)
                                            .border(
                                                2.dp,
                                                style.activeColor,
                                                MaterialTheme.shapes.small
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            AnimatedVisibility(
                                                visible = option.id in selectList.map { it.id },
                                                exit = scaleOut(),
                                                enter = scaleIn()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .background(
                                                            style.activeColor,
                                                            RoundedCornerShape(3.dp)
                                                        )
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = CSB.translator(option.title),
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
                            options.forEach { option ->
                                val isSelected = option.id in selectList.map { it.id }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (option in selectList) selectList.remove(option)
                                        else selectList.add(option)
                                    },
                                    label = {
                                        Text(
                                            text = CSB.translator(option.title),
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
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        trailingIcon = {
                                            if (option in selectList) {
                                                Icon(
                                                    painter = painterResource(R.drawable.check),
                                                    contentDescription = "Checked"
                                                )
                                            }
                                        },
                                        text = {
                                            Text(
                                                text = CSB.translator(option.title),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        onClick = {
                                            if (option in selectList) {
                                                selectList.remove(option)
                                            } else {
                                                selectList.add(option)
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            },
            onClick =  if (!isOpenMode) { { alertOpen = true } } else null
        )
        if (alertOpen && uiMode == UIMode.Alert) {
            AlertDialog(
                title = {
                    if (!alertTitle.isBlank()) Text(CSB.translator(alertTitle))
                },
                text = {

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        options.forEach { option ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        if (option in selectList) {
                                            selectList.remove(option)
                                        } else {
                                            selectList.add(option)
                                        }

                                    },
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(22.dp)
                                        .border(
                                            2.dp,
                                            style.activeColor,
                                            MaterialTheme.shapes.small
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row() {
                                        AnimatedVisibility(
                                            visible = option.id in selectList.map { it.id },
                                            exit = scaleOut(),
                                            enter = scaleIn()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(
                                                        style.activeColor,
                                                        RoundedCornerShape(3.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = CSB.translator(option.title),
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
                            changeValue(selectList)
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

