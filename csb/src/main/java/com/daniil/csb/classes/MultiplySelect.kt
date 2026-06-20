package com.daniil.csb.classes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilledIconButton
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
import com.daniil.csb.R
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable


class MultiplySelect(
    override var id: String,
    val options: List<Option>,
    val defaultValue: List<Option>,
    override val title: String,
    val alertTitle: String,
    override val description: String,
    enabled: Boolean = true,
    var onChangeValue: (List<MultiplySelect.Option>) -> Unit = {},
    override var isSaveSetting: Boolean
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
    @JvmName(name = "ChangeValueWitchId")
    fun changeValue(optionIds: List<String>) {
        val option = options.filter { it.id in optionIds }
        _value.value = option
    }

    fun changeValue(optionId: String) {
        val option = options.find { it.id == optionId } ?: return
        _value.value = value.value + listOf(option)
    }


    override fun resetToDefault() { changeValue(this@MultiplySelect.defaultValue) }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringListPackage(
            id = id,
            enable = enabled.value,
            value = value.value.map { it.id }
        )
    }

    override fun loadLogic(pack: SaveSettingPackage?) {
        if (pack == null) return
        changeValue(options.filter { it.id in pack.value as List<*> })
        enabled(pack.enable)
    }



    @Serializable
    data class Option(
        val id: String,
        val title: String,
    )

    class MultiplySelectBuilderScope() {
        lateinit var defaultValue: List<MultiplySelect.Option>
        lateinit var options: List<MultiplySelect.Option>
        var onChangeValue: (List<MultiplySelect.Option>) -> Unit = {}
        var title = "Multiply Select"
        var alertTitle = "Select multiple"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: MultiplySelectBuilderScope.() -> Unit
    ) {
        val scope = MultiplySelectBuilderScope().apply(builderScope)
        fun create(): MultiplySelect = with(scope) {
            return MultiplySelect(
                id,
                options,
                defaultValue,
                title,
                alertTitle,
                description,
                enabled,
                onChangeValue,
                isSaveSetting
            )
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val selectList = retain { value.value.toMutableStateList() }

        DefaultSettingUI(
            modifier = Modifier,
            focusState = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { if (!description.isBlank()) Text(description) },
            display = {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        enabled = enabled,
                        colors = IconButtonDefaults.iconButtonColors()
                            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        onClick = {
                            alertOpen = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_down),
                            contentDescription = "dropdown arrow"
                        )
                    }
                }
            },
            onClick = { alertOpen = true }
        )
        if (alertOpen) {
            AlertDialog(
                title = {
                    if (!alertTitle.isBlank()) Text(alertTitle)
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
                                            MaterialTheme.colorScheme.primary,
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
                                                        MaterialTheme.colorScheme.primary,
                                                        RoundedCornerShape(3.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option.title,
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

fun SettingBuilder.createMultiplySelect(
    id: String,
    builder: MultiplySelect.MultiplySelectBuilderScope.() -> Unit = {
        defaultValue = listOf()
        options = listOf()
    }
): MultiplySelect {
    return MultiplySelect.Builder(id, builder).create()
}