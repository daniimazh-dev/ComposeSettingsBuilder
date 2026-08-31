package com.daniil.csb.group.title

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.daniil.csb.CSB
import com.daniil.csb.settingui.LocalSettingsStyle

open class GroupTitle internal constructor(
    internal val content: (@Composable GroupTitleContentScope.() -> Unit)? = null,
) {
    companion object {
        fun setContent(content: @Composable GroupTitleContentScope.() -> Unit): GroupTitle {
            return GroupTitle(content)
        }
        fun setText(text: String): GroupTitle {
            val content: @Composable GroupTitleContentScope.() -> Unit = {
                val style = LocalSettingsStyle.current
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val textStyle = style.titleStyle.copy(fontWeight = FontWeight.Bold)
                    Text(
                        text = CSB.translator(text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = textStyle
                    )
                }
            }
            return GroupTitle(content)
        }
    }
    class GroupTitleContentScope
}