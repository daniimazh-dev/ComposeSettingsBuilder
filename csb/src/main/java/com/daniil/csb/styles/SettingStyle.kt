package com.daniil.csb.styles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

interface SettingStyle {
    val backgroundColor: Color
    val containerColor: Color
    val activeColor: Color
    val focusColor: Color
    val titleStyle: TextStyle
    val labelStyle: TextStyle
    val descriptionStyle: TextStyle
    val edgeGroupShape: Shape
    val containerCorner: Dp
    val horizontalPadding: Dp
    val verticalPadding: Dp
    val minHeight: Dp
    val itemSpacing: Dp
    val slotSpacing: Dp
    val groupSpacing: Dp
    val cardElevation: Dp
    val topBarContainerColor: Color
    val topBarHeight: Dp

    val composer: Composer
}
