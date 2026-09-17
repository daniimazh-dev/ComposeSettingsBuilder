package com.daniil.csb.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.daniil.csb.R
import com.daniil.csb.settingui.LocalSettingsStyle

class TopScreenBar(
    config: TopScreenBarConfigScope.() -> Unit = { text = "TopScreenBar" }
) {
    constructor(
        text: String
    ): this({ this.text = text })

    internal val data = TopScreenBarConfigScope().apply(config)
    companion object {
        fun text(text: String) = TopScreenBar { this.text = text }
        fun navigateOnly(config: NavigationOnlyBarConfigScope.() -> Unit = {}): TopScreenBar {
            val configNavOnly = NavigationOnlyBarConfigScope().apply(config)
            return TopScreenBar {
                navigateOnly = true
                onBackClicked = configNavOnly.onBackClicked
                containerColor = configNavOnly.containerColor
            }
        }
    }
    @Composable
    internal fun TopScreenBarUI(
        onBack: () -> Unit,
        height: Dp,
        defaultText: String,
        firstVisibleOffset: Float,
        isShowNavigationIcon: Boolean
    ) {
        val style = LocalSettingsStyle.current
        if (data.navigateOnly) {
            if (isShowNavigationIcon) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    FilledIconButton(
                        modifier = Modifier,
                        onClick = { data.onBackClicked?.invoke(); onBack() },
                        colors = IconButtonDefaults.filledIconButtonColors()
                            .copy(
                                containerColor = style.topBarContainerColor,
                                contentColor = LocalContentColor.current
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_icon),
                            contentDescription = "Back arrow",
                        )
                    }
                }
            }
        } else {
            style.TopScreenBar(
                text = data.text ?: defaultText,
                isShowNavigationIcon = isShowNavigationIcon,
                height = data.height ?: height,
                containerColor = data.containerColor ?: style.topBarContainerColor,
                firstVisibleOffset = firstVisibleOffset,
                actions = data.actions
            ) { data.onBackClicked?.invoke(); onBack() }
        }
    }
    class TopScreenBarConfigScope internal constructor() {
        var height: Dp? = null
        var text: String? = null
        var onBackClicked: (() -> Unit)? = null
        var containerColor: Color? = null
        var actions: @Composable () -> Unit = {}
        internal var navigateOnly = false
    }
    class NavigationOnlyBarConfigScope internal constructor() {
        var onBackClicked: (() -> Unit)? = null
        var containerColor: Color? = null
    }
}