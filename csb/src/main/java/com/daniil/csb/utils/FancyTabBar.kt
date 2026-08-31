package com.daniil.csb.utils


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniil.csb.settingui.LocalSettingsStyle


private fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}


data class FancyTabBarData(
    val id: String,
    val titleContent: @Composable () -> Unit,
)

object FancyTabBarDefaults {
    @Composable
    fun colors(): FancyTabBarColor {
        return FancyTabBarColor(
            bgColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            indicatorColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

data class FancyTabBarColor(
    val bgColor: Color,
    val indicatorColor: Color,
    val textColor: Color,
    val selectedTextColor: Color
)

@Composable
fun FancyTabBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    entries: List<FancyTabBarData>,
    colors: FancyTabBarColor = FancyTabBarDefaults.colors(),
    horizontal: Boolean = true,
    onSelected: (String) -> Unit
) {
    BoxWithConstraints(
        modifier
            .then(
                if (horizontal) Modifier.height(52.dp)
                else Modifier.width(52.dp)
            )
            .clip(MaterialTheme.shapes.large)
            .background(colors.bgColor)
            .padding(6.dp)
    ) {
        val maxMainSize = if (horizontal) maxWidth else maxHeight
        val tabMainSize = maxMainSize / entries.size

        val indicatorOffset by animateDpAsState(
            targetValue = tabMainSize * selectedIndex,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            label = "indicator offset"
        )

        val arrangement =
            if (horizontal) Arrangement.SpaceBetween else Arrangement.SpaceAround

        val layoutModifier =
            if (horizontal) Modifier.fillMaxWidth()
            else Modifier.fillMaxHeight()

        RowOrColumn(
            horizontal = horizontal,
            modifier = layoutModifier.drawWithContent {
                val radiusPx = if (horizontal) size.height / 2 else size.width / 2

                drawWithLayer {
                    drawContent()

                    val sizePx = if (horizontal)
                        Size(tabMainSize.toPx(), size.height)
                    else
                        Size(size.width, tabMainSize.toPx())

                    val offsetPx = if (horizontal)
                        Offset(indicatorOffset.toPx(), 0f)
                    else
                        Offset(0f, indicatorOffset.toPx())

                    drawRoundRect(
                        color = colors.indicatorColor,
                        topLeft = offsetPx,
                        size = sizePx,
                        cornerRadius = CornerRadius(radiusPx),
                        blendMode = BlendMode.SrcOut
                    )
                }
            },
            arrangement = arrangement
        ) {

            entries.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .then(
                            if (horizontal) Modifier
                                .width(tabMainSize)
                                .fillMaxHeight()
                            else Modifier
                                .height(tabMainSize)
                                .defaultMinSize(42.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelected(item.id) }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        item.titleContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun RowOrColumn(
    horizontal: Boolean,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    content: @Composable () -> Unit
) {
    if (horizontal) {
        Row(modifier = modifier, horizontalArrangement = arrangement) {
            content()
        }
    } else {
        Column(modifier = modifier, verticalArrangement = arrangement) {
            content()
        }
    }
}