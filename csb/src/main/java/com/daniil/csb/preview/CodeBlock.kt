package com.daniil.csb.preview

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CodeBlock internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var language: Language = Language.Unspecified,
    var code: String = "",
    var onClicked: () -> Unit = {},
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow<Unit>(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false
    override val onChangeValue: (Unit) -> Unit = {}
    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }

    enum class Language(val color: Color) {
        Unspecified(Color.White),
        Kotlin(Color.Magenta),
        Java(Color.Yellow),
    }

    override fun changeValue(newValue: Unit) {}
    @CsbDslMarkers
    class CodeBlockBuilderScope() {
        var title: String? = null
        var description: String? = null
        var language: Language = Language.Unspecified
        var code: String = ""
        var enabled = true
        var onClick: () -> Unit = {}
    }

    class Builder(
        val id: String,
        builderScope: CodeBlockBuilderScope.() -> Unit = {}
    ) {
        val scope = CodeBlockBuilderScope().apply(builderScope)
        fun create(): CodeBlock = with(scope) {
            return CodeBlock(id, title.orEmpty(), description, enabled, language, code, onClick)
        }
    }

    override val defaultValue: Unit = Unit

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val style = LocalSettingsStyle.current
        val clipboard = LocalClipboard.current
        val coroutine = rememberCoroutineScope()
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val customStyle = if (title.isBlank()) style.copy(
            verticalPadding = style.verticalPadding / 2,
            minHeight = style.minHeight / 1.5f
        ) else style
        CompositionLocalProvider(LocalSettingsStyle provides customStyle) {
            DefaultContainer(
                modifier = modifier,
                isFocused = focusState,
                groupItemClip = position,
                enabled = enabled,
                paddingValues = PaddingValues(16.dp),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = style.minHeight),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!title.isBlank()) Text(text = title, style = style.titleStyle)
                                val descriptionStyle = style.labelStyle
                                    .copy(color = MaterialTheme.colorScheme.outline)
                                description?.let { Text(text = it, style = descriptionStyle) }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(style.edgeGroupCorner)
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (language != Language.Unspecified) {
                                        Text(
                                            text = language.name,
                                            color = language.color,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        modifier = Modifier.size(18.dp),
                                        onClick = { coroutine.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Code", code))) }
                                    }) {
                                        Icon(painter = painterResource(R.drawable.copy), contentDescription = "copy")
                                    }
                                }
                                Text(
                                    text = code,
                                    style = style.descriptionStyle.copy(fontSize = 10.sp).copy(
                                        fontFamily = FontFamily(Font(R.font.jetbrainsmono_regular))
                                    )
                                )
                            }
                        }
                    }
                },
                onClick = { onClicked() }
            )
        }
    }
}

fun SettingDslInterface.createCodeBlock(
    id: String,
    scope: CodeBlock.CodeBlockBuilderScope.() -> Unit = {}
) = CodeBlock.Builder(id, scope).create().register()

