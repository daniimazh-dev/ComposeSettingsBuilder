package com.daniil.csb.settingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.utils.LocalGroupPosition
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.styles.LocalSettingsStyle

@Composable
fun DefaultContainer(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    groupItemClip: GroupItemClip? = null,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val style = LocalSettingsStyle.current
    val groupPosition = LocalGroupPosition.current

    val baseShape = style.containerShape as RoundedCornerShape
    val gcs = style.groupCornerShape
    
    val groupClip = when (groupItemClip ?: groupPosition) {
        GroupItemClip.First -> baseShape.copy(
            bottomEnd = CornerSize(gcs),
            bottomStart = CornerSize(gcs),
        )
        GroupItemClip.Default -> baseShape.copy(
            topStart = CornerSize(gcs),
            topEnd = CornerSize(gcs),
            bottomEnd = CornerSize(gcs),
            bottomStart = CornerSize(gcs),
        )
        GroupItemClip.Last -> baseShape.copy(
            topStart = CornerSize(gcs),
            topEnd = CornerSize(gcs),
        )
        GroupItemClip.None -> baseShape
    }
    
    val defaultColor = style.backgroundColor
    val focusColor = style.focusColor

    Box(
        modifier = Modifier
            .shadow(elevation = style.cardElevation, shape = style.containerShape, clip = false)
            .clip(groupClip)
            .background(if (isFocused) focusColor else defaultColor)
            .then(if (enabled) modifier.clickable { onClick() }
            else modifier.alpha(0.5f))

    ) {
        content()
    }
}
