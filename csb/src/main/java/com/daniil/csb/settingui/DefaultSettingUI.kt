package com.daniil.csb.settingui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.daniil.csb.R
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settings.settingcore.clippedShape
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3

@Composable
fun DefaultSettingUI(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    groupItemClip: GroupItemClip? = null,
    enabled: Boolean = true,
    paddingValues: PaddingValues =
        LocalSettingsStyle.current.let {
            PaddingValues(it.horizontalPadding, it.verticalPadding) },
    minHeight: Dp = LocalSettingsStyle.current.minHeight,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit = {},
    icon: SettingIcon? = null,
    badge: SettingBadge? = null,
    action: @Composable () -> Unit = {},
    onClick: (() -> Unit)?,
    display: @Composable () -> Unit = {}
) {
    val style = LocalSettingsStyle.current
    style.ContainerSlot(
        modifier = modifier,
        isFocused = isFocused,
        shape = (groupItemClip ?: LocalGroupPosition.current).clippedShape(),
        enabled = enabled,
        onClick = onClick,
        minHeight = minHeight,
        content = {
            style.ItemLayoutSlot(
                title = title,
                description = description,
                action = action,
                display = display,
                icon = icon?.let { { it.content() } },
                badge = badge?.let { { it.content() } },
                paddingValues = paddingValues
            )
        }
    )
}


@Preview
@Composable
private fun Preview() {
    CompositionLocalProvider(LocalSettingsStyle provides CSBStyle.Material3()) {
        DefaultSettingUI(
            title = {
                Text("Preview")
            },
            icon = SettingIcon.fromRes(R.drawable.info_icon),
            description = {
                Text("Preview settings default container")
            },
            action = {
                Switch(checked = true, onCheckedChange = {})
            },
            badge = SettingBadge.icon(R.drawable.star),
            enabled = true,
            groupItemClip = GroupItemClip.Full,
            onClick = null
        )
    }
}
