package com.daniil.csb.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/** Without material theme */
val CSBStyle.ClassicLight: SettingStyle
    get() = object : DefaultSettingStyle() {
        override var backgroundColor: Color = Color.White
        override var containerColor: Color = Color.Transparent
        override var activeColor: Color = Color.Cyan
        override var focusColor: Color = Color.Gray.copy(alpha = 0.4f)
        override var titleStyle: TextStyle = TextStyle.Default.copy(fontSize = 16.sp)
        override var labelStyle: TextStyle = TextStyle.Default.copy(fontSize = 11.sp)
        override var descriptionStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp, color = Color.Gray)
        override var edgeGroupShape: Shape = RoundedCornerShape(6.dp)
        override var containerCorner: Dp = 0.dp
        override var horizontalPadding: Dp = 12.dp
        override var verticalPadding: Dp = 10.dp
        override var minHeight: Dp = 52.dp
        override var itemSpacing: Dp = 2.dp
        override var groupSpacing: Dp = 4.dp
        override var slotSpacing: Dp = 6.dp
        override var cardElevation: Dp = 0.dp
        override var topBarContainerColor: Color = Color.White
    }

/** Without material theme */
val CSBStyle.ClassicDark
    get() = object : DefaultSettingStyle() {
        override var backgroundColor: Color = Color(0xFF151515)
        override var containerColor: Color = Color.Transparent
        override var activeColor: Color = Color.Cyan
        override var focusColor: Color = Color.Gray.copy(alpha = 0.4f)
        override var titleStyle: TextStyle = TextStyle.Default.copy(fontSize = 16.sp)
        override var labelStyle: TextStyle = TextStyle.Default.copy(fontSize = 11.sp)
        override var descriptionStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp, color = Color.Gray)
        override var edgeGroupShape: Shape = RoundedCornerShape(6.dp)
        override var containerCorner: Dp = 0.dp
        override var horizontalPadding: Dp = 12.dp
        override var verticalPadding: Dp = 10.dp
        override var minHeight: Dp = 52.dp
        override var itemSpacing: Dp = 2.dp
        override var groupSpacing: Dp = 4.dp
        override var slotSpacing: Dp = 6.dp
        override var cardElevation: Dp = 0.dp
        override var topBarContainerColor: Color = Color.Black
    }
