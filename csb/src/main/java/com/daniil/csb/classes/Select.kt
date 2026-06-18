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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniil.csb.R
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.CSBCreator
import com.daniil.csb.classes.utils.ItemGroupPosition
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

class Select(
    override var id: String,
    val options: List<Option>,
    val defaultValue: Option,
    override val title: String,
    val alertTitle: String,
    override val description: String,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean
) : ComposeSetting<Select.Option>() {
    private var _value = MutableStateFlow(this@Select.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Option) {
        if (!options.contains(newValue)) return
        _value.value = newValue
    }

    fun changeValue(optionId: String) {
        val option = options.find { it.id == optionId }
        _value.value = option ?: return
    }

    override fun resetToDefault() { changeValue(this@Select.defaultValue) }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringPackage(
            id = id,
            enable = enabled.value,
            value = value.value.id
        )
    }

    override fun loadLogic(pack: SaveSettingPackage?) {
        if (pack == null) return
        changeValue(pack.value as String)
        enabled(pack.enable)
    }



    @Serializable
    data class Option(
        val id: String,
        val title: String,
    )

    class SelectBuilderScope() {
        lateinit var defaultValue: Select.Option
        lateinit var options: List<Select.Option>
        var title = "Select"
        var alertTitle = "Select item"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: SelectBuilderScope.() -> Unit
    ) {
        val scope = SelectBuilderScope().apply(builderScope)
        fun create(): Select = with(scope) {
            return Select(
                id,
                options,
                defaultValue,
                title,
                alertTitle,
                description,
                enabled,
                isSaveSetting
            )
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(position: ItemGroupPosition?) {
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        var selectId by retain(alertOpen) { mutableStateOf<String>(value.id) }

        DefaultSettingUI(
            modifier = Modifier,
            focusState = focusState,
            itemGroupPosition = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { if (!description.isBlank()) Text(description) },
            display = {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .widthIn(max = 112.dp),
                        text = value.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(6.dp))
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
                                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
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
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = it.title,
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


fun CSBCreator.createSelect(
    id: String,
    builder: Select.SelectBuilderScope.() -> Unit = {
        defaultValue = Select.Option("", "")
        options = listOf()
    }
): Select {
    return Select.Builder(id, builder).create()
}