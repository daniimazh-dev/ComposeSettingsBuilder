package com.daniil.csb.group.title

import androidx.compose.runtime.Composable
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalSettingsStyle

open class GroupTitle(
    val content: (@Composable GroupTitleContentScope.() -> Unit)? = null,
) {
    companion object {
        fun text(text: String): GroupTitle {
            val content: @Composable GroupTitleContentScope.() -> Unit = {
                LocalSettingsStyle.current.GroupTitle(LocalCSBTranslator.current.translate(text))
            }
            return GroupTitle(content)
        }
    }
    class GroupTitleContentScope
}