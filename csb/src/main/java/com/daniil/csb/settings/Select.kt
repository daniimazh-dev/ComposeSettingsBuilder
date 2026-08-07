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
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class Select(
    override var id: String,
    val options: List<Option>,
    override val defaultValue: Option,
    override val title: String,
    val alertTitle: String,
    override val description: String?,
    enabled: Boolean = true,
    override var onChangeValue: (Option) -> Unit = {},
    override var isSaveSetting: Boolean
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

    override fun saveLogic(serializer: KSerializer<Option>?): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.StringPackage(
            id = id,
            enable = enabled.value,
            value = value.value.id
        )
    }

    override fun loadLogic(pack: SaveSettingPackage, serializer: KSerializer<Option>?) {
        if (isSaveSetting) changeValue(pack.value as String)
        enabled(pack.enable)
    }



    @Serializable
    data class Option(
        val id: String,
        val title: String,
    ) {
        override fun toString(): String = id
    }

    @CsbDslMarkers
    class SelectBuilderScope() {
        var options = mutableListOf<Option>()
        var defaultValueId: String? = null
        var title: String? = null
        var onChangeValue: (Option) -> Unit = {}
        var alertTitle = "Select item"
        var description: String? = null
        var enabled = true
        var isSaveSetting = true
        fun option(id: String, title: String) {
            +Option(id, title)
        }
        operator fun Option.unaryPlus() {
            options.add(this)
        }
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
                defaultValue = options.first { it.id == (defaultValueId ?: options[0].id) },
                title ?: id,
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
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        var selectId by retain(alertOpen) { mutableStateOf<String>(value.id) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
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
                                        .border(2.dp,  style.activeColor, CircleShape),
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
                                                    .background( style.activeColor, CircleShape)
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


