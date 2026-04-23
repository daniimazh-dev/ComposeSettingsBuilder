package com.daniil.csb.settingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DefaultContainer(
    modifier: Modifier = Modifier,
    columnMode: Boolean = false,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit = {},
    display: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(if (enabled) modifier.clickable { onClick() }
            else modifier.alpha(0.5f))
    ) {
        RowOrColumn(
            columnMode = columnMode,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Column() {
                val titleStyle = MaterialTheme.typography.titleMedium
                CompositionLocalProvider(LocalTextStyle provides titleStyle) {
                    title()
                }
                val descriptionStyle = MaterialTheme.typography.labelSmall
                    .copy(color = MaterialTheme.colorScheme.outline)
                CompositionLocalProvider(LocalTextStyle provides descriptionStyle) {
                    description()
                }
            }
            display()
        }
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

@Preview
@Composable
private fun Preview() {
    DefaultContainer(
        title = {
            Text("Preview")
        },
        description = {
          Text("Preview settings default container")
        },
        display = {
            Switch(checked = true, onCheckedChange = {})
        },
        enabled = true
    ) {}
}