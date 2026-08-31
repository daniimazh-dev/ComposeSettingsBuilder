package com.daniil.csb.settings

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.CSB
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CodePreview internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var language: Language = Language.Unspecified,
    var code: String = "",
    val fontSize: TextUnit,
    var onClicked: () -> Unit = {},
    val keyWords: List<String>,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Unit>() {
    private var _value = MutableStateFlow(Unit)
    override val value = _value.asStateFlow()

    override var isSaveSetting: Boolean = false
    override val onChangeValue: (Unit) -> Unit = {}
    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) { _enable.value = state }

    enum class Language(val color: Color) {
        Unspecified(Color.Gray),
        Kotlin(Color(0xFF7F52FF)),
        Java(Color(0xFF007396)),
        Python(Color(0xFF3776AB)),
        JavaScript(Color(0xFFF7DF1E)),
        TypeScript(Color(0xFF3178C6)),
        Cpp(Color(0xFF00599C)),
        CSharp(Color(0xFF239120)),
        PHP(Color(0xFF777BB4)),
        Swift(Color(0xFFF05138)),
        Go(Color(0xFF00ADD8)),
        Rust(Color(0xFFDEA584)),
        Ruby(Color(0xFFCC342D)),
        Lua(Color(0xFF066794)),
        Dart(Color(0xFF37E7C3)),
        SQL(Color(0xFF4479A1)),
        HTML(Color(0xFFE34F26)),
        CSS(Color(0xFF1572B6)),
        Shell(Color(0xFF89E051)),
        C(Color(0xFFA8B9CC)),
        R(Color(0xFF565656)),
        Scala(Color(0xFFCE6868)),
        XML(Color(0xFF0060AC)),
        JSON(Color(0xFF808080)),
        Markdown(Color(0xFF000000))
    }

    override fun changeValue(newValue: Unit) {}
    @CsbDslMarkers
    class CodePreviewBuilderScope : SettingDefaultScope() {
        var title: String? = null
        var description: String? = null
        var language: Language = Language.Unspecified
        var code: String = ""
        var keyWords = emptyList<String>()
        var fontSize = 12.sp
        var onClick: () -> Unit = {}
    }
    companion object: ComposeSettingInterface.Factory<CodePreview, CodePreviewBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: CodePreviewBuilderScope.() -> Unit
        ): SettingToken<CodePreview> {
            val data = CodePreviewBuilderScope().apply(scope)
            return with(data) {
                CodePreview(id, title.orEmpty(), description, enabled, language, code, fontSize,onClick, keyWords)
            }.register()
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
                                if (!title.isBlank()) Text(text = CSB.translator(title), style = style.titleStyle)
                                val descriptionStyle = style.labelStyle
                                    .copy(color = MaterialTheme.colorScheme.outline)
                                description?.let { Text(text = CSB.translator(it), style = descriptionStyle) }
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
                                    var isCopied by remember { mutableStateOf(false) }
                                    LaunchedEffect(isCopied) {
                                        if (isCopied) {
                                            kotlinx.coroutines.delay(2000)
                                            isCopied = false
                                        }
                                    }
                                    IconButton(
                                        modifier = Modifier.size(18.dp),
                                        onClick = {
                                            coroutine.launch {
                                                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Code", code)))
                                                isCopied = true
                                            }
                                        }
                                    ) {
                                        if (isCopied) {
                                            Icon(
                                                modifier = Modifier.size(14.dp),
                                                painter = painterResource(R.drawable.check),
                                                contentDescription = "copied",
                                                tint = Color.Green
                                            )
                                        } else {
                                            Icon(
                                                modifier = Modifier.size(14.dp),
                                                painter = painterResource(R.drawable.copy),
                                                contentDescription = "copy"
                                            )
                                        }
                                    }
                                }
                                val scrollState = rememberScrollState()
                                Text(
                                    text = highlightCode(code, language, MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.horizontalScroll(scrollState),
                                    style = style.descriptionStyle.copy(fontSize = fontSize).copy(
                                        fontFamily = FontFamily(Font(R.font.jetbrainsmono_regular))
                                    ),
                                    softWrap = false
                                )
                                HorizontalDivider(color = language.color)
                            }
                        }
                    }
                },
                onClick = { onClicked() }
            )
        }
    }

    private fun highlightCode(code: String, language: Language, keywordColor: Color): AnnotatedString {

        if (keyWords.isEmpty()) return AnnotatedString(code)

        return buildAnnotatedString {
            val words = code.split(Regex("(?<=\\b)|(?=\\b)"))
            words.forEach { word ->
                if (keyWords.contains(word)) {
                    withStyle(style = SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                } else {
                    append(word)
                }
            }
        }
    }
}
