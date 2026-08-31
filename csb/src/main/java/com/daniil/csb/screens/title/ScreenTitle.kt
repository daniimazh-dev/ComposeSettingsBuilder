package com.daniil.csb.screens.title

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.daniil.csb.CSB
import com.daniil.csb.settingui.LocalSettingsStyle

class ScreenTitle internal constructor(
    internal val content: (@Composable ScreenTitleContentScope.(ScreenTitleConfig) -> Unit)? = null,
) {
    open class ScreenTitleConfig(
        val alignment: Alignment,
        val textAlign: TextAlign,
        val maxLines: Int,
        val style: TextStyle?,
    ) {
         companion object Default: ScreenTitleConfig(
             alignment = Alignment.CenterStart,
             textAlign = TextAlign.Start,
             maxLines = 1,
             style = null
         )
    }
    companion object {
        fun setContent(content: @Composable ScreenTitleContentScope.(ScreenTitleConfig) -> Unit): ScreenTitle {
            return ScreenTitle(content)
        }
        fun setText(text: String): ScreenTitle {
            val content: @Composable ScreenTitleContentScope.(ScreenTitleConfig) -> Unit = { config ->
                val style = LocalSettingsStyle.current
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = config.alignment
                ) {
                    Text(
                        text = CSB.translator(text),
                        textAlign = config.textAlign,
                        maxLines = config.maxLines,
                        overflow = TextOverflow.Ellipsis,
                        style = config.style ?: MaterialTheme.typography.titleLarge
                    )
                }
            }
            return ScreenTitle(content)
        }
    }

    class ScreenTitleContentScope
}