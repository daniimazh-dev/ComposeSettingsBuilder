package com.daniil.csb.styles

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp


interface SettingComposer {
    @Composable
    fun ContainerSlot(
        modifier: Modifier,
        isFocused: Boolean,
        shape: Shape,
        enabled: Boolean,
        minHeight: Dp,
        onClick: (() -> Unit)?,
        content: @Composable () -> Unit
    )

    @Composable
    fun ItemLayoutSlot(
        title: @Composable () -> Unit,
        description: @Composable () -> Unit = {},
        action: @Composable () -> Unit,
        display: @Composable () -> Unit = {},
        icon: (@Composable () -> Unit)? = null,
        badge: (@Composable () -> Unit)? = null,
        paddingValues: PaddingValues,
    )
    @Composable
    fun SettingIcon(
        @DrawableRes res: Int,
        tint: Color,
        contentDescription: String?
    )
}

interface  ScreenComposer {
    @Composable
    fun GroupTitle(
        text: String
    )
    @Composable
    fun TopScreenBar(
        text: String,
        isShowNavigationIcon: Boolean,
        height: Dp,
        containerColor: Color,
        firstVisibleOffset: Float,
        actions: @Composable () -> Unit,
        onBack: () -> Unit
    )
}
interface Composer: SettingComposer, ScreenComposer