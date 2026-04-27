package com.daniil.csb.settingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.util.ItemGroupPosition
import kotlinx.coroutines.delay

@Composable
fun DefaultContainer(
    modifier: Modifier = Modifier,
    focusState: Boolean = false,
    itemGroupPosition: ItemGroupPosition = ItemGroupPosition.None,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit

) {
    val shape = MaterialTheme.shapes.medium
    val groupClip = when (itemGroupPosition) {
        ItemGroupPosition.First -> shape.copy(
            topStart = CornerSize(12.dp),
            topEnd = CornerSize(12.dp),
            bottomEnd = CornerSize(4.dp),
            bottomStart = CornerSize(4.dp),
        )
        ItemGroupPosition.Default -> shape.copy(
            topStart = CornerSize(4.dp),
            topEnd = CornerSize(4.dp),
            bottomEnd = CornerSize(4.dp),
            bottomStart = CornerSize(4.dp),
        )
        ItemGroupPosition.Last -> shape.copy(
            topStart = CornerSize(4.dp),
            topEnd = CornerSize(4.dp),
            bottomEnd = CornerSize(12.dp),
            bottomStart = CornerSize(12.dp),
        )
        ItemGroupPosition.None -> shape
    }
    val defaultColor = MaterialTheme.colorScheme.surfaceContainer
    val focusColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Box(
        modifier = Modifier
            .clip(groupClip)
            .background(if (focusState) focusColor else defaultColor)
            .then(if (enabled) modifier.clickable { onClick() }
            else modifier.alpha(0.5f))
    ) {
        content()
    }
}