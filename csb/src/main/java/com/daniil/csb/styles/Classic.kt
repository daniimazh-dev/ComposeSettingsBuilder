package com.daniil.csb.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



/** Without material theme */
val CSBStyle.ClassicLight: SettingsStyle
    get() = SettingsStyle(
        titleStyle = TextStyle.Default.copy(
            fontSize = 16.sp
        ),
        labelStyle = TextStyle.Default.copy(
            fontSize = 11.sp
        ),
        activeColor = Color.Cyan,
        descriptionStyle = TextStyle.Default.copy(
            fontSize = 12.sp,
            color = Color.Gray
        ),
        edgeGroupCorner = RoundedCornerShape(6.dp),
        containerCornerShape = 0.dp,
        backgroundColor = Color.White,
        focusColor = Color.Gray.copy(alpha = 0.4f),
        horizontalPadding = 12.dp,
        verticalPadding = 10.dp,
        minHeight = 52.dp,
        itemSpacing = 2.dp,
        cardElevation = 0.dp,
    )

/** Without material theme */
val CSBStyle.ClassicDark
    get() = ClassicLight.copy(
        backgroundColor = Color(0xFF151515)
    )
