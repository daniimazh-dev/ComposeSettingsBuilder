package com.daniil.csb.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.daniil.csb.settingui.LocalSettingsStyle


open class GroupTitle internal constructor(
    val title: String,
) {
    internal constructor(
        content: @Composable () -> Unit,
    ) : this("") {
        this.content = content
    }

    var content: @Composable (() -> Unit)? = null

    @Composable
    fun UI(modifier: Modifier = Modifier) {
        val style = LocalSettingsStyle.current

        if (content == null) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val textStyle = style.titleStyle.copy(fontWeight = FontWeight.Bold)
                Text(
                    text = title,
                    maxLines = 1,
                    style = textStyle
                )
            }
        } else {
            this.content?.invoke()
        }
    }
}
internal object DefaultGroupTitle: GroupTitle("")


fun GroupScope.customGroupTitle(
    title: String? = null
): GroupTitle? {
    return title?.let { GroupTitle(it) }
}

fun GroupScope.customGroupTitle(
    content: @Composable () -> Unit,
): GroupTitle {
    return GroupTitle(content)
}