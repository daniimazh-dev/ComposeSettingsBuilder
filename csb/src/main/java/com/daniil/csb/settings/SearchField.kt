package com.daniil.csb.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.depend.Depends
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ComposeSettingInterface
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.SettingDefaultScope
import com.daniil.csb.settings.settingcore.SettingDslInterface
import com.daniil.csb.settings.settingcore.SettingToken
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.settingui.SettingBadge
import com.daniil.csb.settingui.SettingIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SearchField internal constructor(
    override var id: String,
    override val defaultValue: String,
    override val title: String,
    enabled: Boolean = true,
    visible: Boolean = true,
    val icon: SettingIcon? = null,
    val badge: SettingBadge? = null,
    override val customGrouping: GroupItemClip? = null,
    override var onChangeValue: (String) -> Unit = {},
    val onFocusChange: (Boolean) -> Unit = {},
    override val depends: List<Depends> = emptyList(),
) : ComposeSetting<String>(independentObject =  true, depends = depends, initialEnabled = enabled, initialVisible = visible) {
    private var _value = MutableStateFlow(this@SearchField.defaultValue)
    override val value = _value.asStateFlow()
    override var isSaveSetting: Boolean = false
    override val description: String? = null

    override fun changeValue(newValue: String) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class SearchFieldBuilderScope(): SettingDefaultScope() {
        var defaultValue: String? = null
        var title: String? = null
        var onChangeValue: (String) -> Unit = {}
        var onFocusChange: (Boolean) -> Unit = { }
        @Deprecated("The SearchField setting dose not store any data. Changing the value to true is not necessary", level = DeprecationLevel.HIDDEN)
        override var isSaveSetting: Boolean = false
    }

    companion object : ComposeSettingInterface.Factory<SearchField, SearchFieldBuilderScope> {
        override fun SettingDslInterface.create(id: String, scope: SearchFieldBuilderScope.() -> Unit): SettingToken<SearchField> {
            val data = SearchFieldBuilderScope().apply(scope)
            return with(data) {
                SearchField(
                    id,
                    defaultValue = defaultValue ?: "",
                    title = title ?: id,
                    enabled = enabled,
                    visible = visible,
                    icon = icon,
                    badge = badge,
                    customGrouping = customGrouping,
                    onChangeValue = onChangeValue,
                    onFocusChange = onFocusChange,
                    depends = depends
                ).register()
            }
        }
    }

    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        val translator = LocalCSBTranslator.current
        val focusRequest = remember { FocusRequester() }

        var searchExpanded by remember { mutableStateOf(false) }

        val onCancel = {
            searchExpanded = false
            changeValue("")
        }

        BackHandler(searchExpanded) {
            onCancel()
        }

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(searchExpanded) {
            if (searchExpanded) {
                focusRequester.requestFocus()
            } else focusRequest.captureFocus()
        }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = customGrouping ?: position,
            enabled = enabled,
            icon = icon,
            badge = badge,
            paddingValues = PaddingValues(4.dp),
            minHeight = style.minHeight / 2,
            title = { 
               Box(modifier = Modifier.fillMaxWidth()) {
                    Row {
                        AnimatedVisibility(
                            modifier = Modifier.padding(horizontal = style.horizontalPadding),
                            visible = !searchExpanded
                        ) {
                            Text(
                                text = translator.translate(title),
                                style = style.titleStyle
                            )
                        }
                    }

                    if (searchExpanded) {
                        val textColor = MaterialTheme.colorScheme.onSurface
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusable()
                                .focusRequester(focusRequester)
                                .onFocusChanged { onFocusChange(it.isFocused) },
                            visualTransformation = VisualTransformation { text ->
                                val builder = AnnotatedString.Builder(text.text)
                                builder.addStyle(SpanStyle(textColor), 0, text.text.length)
                                TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
                            },
                            value = value,
                            singleLine = true,
                            cursorBrush = SolidColor(style.activeColor),
                            onValueChange = { changeValue(it) },
                            textStyle = style.titleStyle
                        )

                    }
               }
            },
            action = {
                IconButton(onClick = {
                    if (searchExpanded) onCancel() else searchExpanded = true
                }) {
                    Icon(
                        painter = if (searchExpanded) painterResource(R.drawable.close) else painterResource(R.drawable.search),
                        contentDescription = "search_icon"
                    )
                }
            },
            onClick = { searchExpanded = true }
        )
    }

}
