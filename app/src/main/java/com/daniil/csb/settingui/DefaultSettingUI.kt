package com.daniil.csb.settingui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.util.ItemGroupPosition

@Composable
fun DefaultSettingUI(
    modifier: Modifier = Modifier,
    focusState: Boolean = false,
    itemGroupPosition: ItemGroupPosition = ItemGroupPosition.None,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit = {},
    display: @Composable () -> Unit,
    onClick: () -> Unit
) {
    DefaultContainer(
        modifier = modifier,
        focusState = focusState,
        itemGroupPosition = itemGroupPosition,
        enabled = enabled,
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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


@Preview
@Composable
private fun Preview() {
    DefaultSettingUI(
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