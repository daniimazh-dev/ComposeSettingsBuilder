package com.daniil.csb.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

open class DefaultSettingStyle : SettingStyle {
    override var backgroundColor: Color = Color.Transparent
    override var containerColor: Color = Color.Transparent
    override var activeColor: Color = Color(0xFF03A9F4)
    override var focusColor: Color = Color.Transparent
    override var titleStyle: TextStyle = TextStyle.Default
    override var labelStyle: TextStyle = TextStyle.Default.copy(fontSize = 8.sp)
    override var descriptionStyle: TextStyle = TextStyle.Default
    override var edgeGroupCorner: Shape = RoundedCornerShape(12.dp)
    override var containerCornerShape: Dp = 4.dp
    override var horizontalPadding: Dp = 16.dp
    override var verticalPadding: Dp = 12.dp
    override var minHeight: Dp = 52.dp
    override var itemSpacing: Dp = 8.dp

    override var cardElevation: Dp = 2.dp

    @Composable
    override fun ContainerSlot(
        modifier: Modifier,
        isFocused: Boolean,
        shape: Shape,
        enabled: Boolean,
        height: Dp,
        onClick: (() -> Unit)?,
        content: @Composable (() -> Unit)
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = cardElevation, shape = shape, clip = false)
                .clip(shape)
                .background(if (isFocused) focusColor else backgroundColor)
                .heightIn(height)
                .then(
                    if (enabled) modifier
                        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                    else modifier.alpha(0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    @Composable
    override fun ItemLayoutSlot(
        title: @Composable () -> Unit,
        description: @Composable () -> Unit,
        action: @Composable () -> Unit,
        display: @Composable () -> Unit,
        icon: (@Composable () -> Unit)?,
        badge: (@Composable () -> Unit)?,
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        icon()
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompositionLocalProvider(LocalTextStyle provides titleStyle) {
                                title()
                            }
                            badge?.let {
                                Spacer(modifier = Modifier.width(6.dp))
                                badge()
                            }
                        }
                        CompositionLocalProvider(LocalTextStyle provides descriptionStyle) {
                            description()
                        }
                    }
                }
                Box(modifier = Modifier.padding(start = 6.dp)) {
                    action()
                }
            }
            display()
        }
    }
}
