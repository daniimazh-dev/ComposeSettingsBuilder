package com.daniil.csb.styles

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

interface SettingStyle {
    var backgroundColor: Color
    var containerColor: Color
    var activeColor: Color
    var focusColor: Color
    var titleStyle: TextStyle
    var labelStyle: TextStyle
    var descriptionStyle: TextStyle
    var edgeGroupShape: Shape
    var containerCorner: Dp
    var horizontalPadding: Dp
    var verticalPadding: Dp
    var minHeight: Dp
    var itemSpacing: Dp
    var slotSpacing: Dp
    var groupSpacing: Dp
    var cardElevation: Dp
    var topBarContainerColor: Color
    var topBarHeight: Dp

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

    @Composable
    fun SettingIcon(
        @DrawableRes res: Int,
        tint: Color,
        contentDescription: String?
    )
}
