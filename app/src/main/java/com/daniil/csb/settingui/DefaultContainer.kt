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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.ItemGroupPosition

@Composable
fun DefaultContainer(
    modifier: Modifier = Modifier,
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
    Box(
        modifier = Modifier
            .clip(groupClip)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(if (enabled) modifier.clickable { onClick() }
            else modifier.alpha(0.5f))
    ) {
        content()
    }
}


@Composable
fun RowOrColumn(
    modifier: Modifier = Modifier,
    columnMode: Boolean,
    // Row
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    // Column
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    if (columnMode) {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            content()
        }
    }
}