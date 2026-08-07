package com.daniil.csb.settingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daniil.csb.isInFlag
import com.daniil.csb.screens.Screen
import com.daniil.csb.settings.utils.GroupItemClip

@Composable
fun DefaultContainer(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    groupItemClip: GroupItemClip? = null,
    enabled: Boolean,
    paddingValues: PaddingValues = PaddingValues.Zero,
    onClick: (() -> Unit)?,
    disableBackground: Boolean = false,
    content: @Composable () -> Unit
) {
    val style = LocalSettingsStyle.current
    val debagData = LocalDebugData.current
    val groupPosition = LocalGroupPosition.current

    val baseShape = style.edgeGroupCorner as RoundedCornerShape
    val gcs = style.containerCornerShape
    val entries = if ("disableContainerGroupRound".isInFlag()) GroupItemClip.None else groupItemClip ?: groupPosition

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

    val defaultColor = if (disableBackground) Color.Transparent else style.backgroundColor
    val focusColor = style.focusColor


    Box(
        modifier = Modifier
            .shadow(elevation = style.cardElevation, shape = groupClip, clip = false)
            .clip(groupClip)
            .background(if (isFocused) focusColor else defaultColor)
            .padding(paddingValues)
            .then(
                if (enabled) modifier
                    .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier )
                else modifier.alpha(0.5f)
            )
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            content()
            if (debagData != null) {
                Box(
                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    val value by debagData.currentValue.collectAsStateWithLifecycle()
                    Text(
                        text = "${debagData.settingSimpleName} " +
                                "id: ${debagData.settingId} | " +
                                "value: ${if (value is Screen) (value as Screen).id else value}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }

}
