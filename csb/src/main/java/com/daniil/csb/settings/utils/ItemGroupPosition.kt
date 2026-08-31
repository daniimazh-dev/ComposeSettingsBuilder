package com.daniil.csb.settings.utils

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.daniil.csb.isInFlag
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.SettingsStyle

enum class GroupItemClip {
    First,
    None,
    Last,
    Full,
}

@Composable
fun GroupItemClip.clippedShape(style: SettingsStyle = LocalSettingsStyle.current): Shape {
    val groupPosition = this

    val baseShape = style.edgeGroupCorner as RoundedCornerShape
    val gcs = style.containerCornerShape
    val entries = if ("disableContainerGroupRound".isInFlag()) GroupItemClip.None else groupPosition

    val groupClip = when (entries) {

        GroupItemClip.First -> baseShape.copy(
            bottomEnd = CornerSize(gcs),
            bottomStart = CornerSize(gcs),
        )

        GroupItemClip.None -> baseShape.copy(
            topStart = CornerSize(gcs),
            topEnd = CornerSize(gcs),
            bottomEnd = CornerSize(gcs),
            bottomStart = CornerSize(gcs),
        )

        GroupItemClip.Last -> baseShape.copy(
            topStart = CornerSize(gcs),
            topEnd = CornerSize(gcs),
        )

        GroupItemClip.Full -> baseShape
    }
    return groupClip
}