package com.daniil.csb.settingui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.csb.R
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.styles.CSBStyle
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import com.daniil.csb.settingui.styles.Material3

@Composable
fun DefaultSettingUI(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    groupItemClip: GroupItemClip? = null,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    description: @Composable () -> Unit = {},
    display: @Composable () -> Unit,
    onClick: (() -> Unit)?
) {
    val style = LocalSettingsStyle.current
    
    DefaultContainer(
        modifier = modifier,
        isFocused = isFocused,
        groupItemClip = groupItemClip,
        enabled = enabled,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = style.minHeight)
                .padding(horizontal = style.horizontalPadding, vertical = style.verticalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    it()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Column {
                    CompositionLocalProvider(LocalTextStyle provides style.titleStyle) {
                        title()
                    }
                    CompositionLocalProvider(LocalTextStyle provides style.descriptionStyle) {
                        description()
                    }
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            display()
        }

    }
}


@Preview
@Composable
private fun Preview() {
    CompositionLocalProvider(LocalSettingsStyle provides CSBStyle.Material3()) {
        DefaultSettingUI(
            title = {
                Text("Preview")
            },
            icon = {
                Icon(painter = painterResource(R.drawable.info_icon), contentDescription = null)
            },
            description = {
                Text("Preview settings default container")
            },
            display = {
                Switch(checked = true, onCheckedChange = {})
            },
            enabled = true,
            groupItemClip = GroupItemClip.Full
        ) {}
    }
}
