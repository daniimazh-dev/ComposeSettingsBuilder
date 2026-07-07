package com.daniil.csb.local

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3
import com.daniil.csb.styles.SettingsStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettings(
    localController: LocalSettingsController,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues.Zero,
    scrollState: ScrollState? = rememberScrollState(),
    style: SettingsStyle = CSBStyle.Material3(),
) {
    val screen = localController.screen
    CompositionLocalProvider(LocalSettingsStyle provides style) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(screen.paddingValues)
                .then(scrollState?.let { Modifier.verticalScroll(it) } ?: Modifier)
        ) {
            if (screen is CustomScreen) {
                screen.Render()
            }

            val settings = screen.settings
            for (group in screen.settings.keys) {

                if (!group.hide) {
                    val groupItems = settings[group] ?: continue

                    group.groupTitle?.UI()
                    val first = groupItems.firstOrNull()?.id ?: continue
                    val last = groupItems.last().id
                    for (setting in groupItems) {
                        val groupPosition = when {
                            last == first -> GroupItemClip.Full
                            setting.id == last -> GroupItemClip.Last
                            setting.id == first -> GroupItemClip.First
                            else -> GroupItemClip.None
                        }
                        CompositionLocalProvider(LocalGroupPosition provides groupPosition) {
                            setting.UI()
                        }
                    }
                }
            }



        }
    }

}




