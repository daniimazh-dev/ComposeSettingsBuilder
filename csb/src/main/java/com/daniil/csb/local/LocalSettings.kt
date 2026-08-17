package com.daniil.csb.local

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.FragmentedGroup
import com.daniil.csb.screens.Group
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
                .padding(paddingValues)
                .padding(screen.paddingValues)
                .then(scrollState?.let { Modifier.verticalScroll(it) } ?: Modifier),
            verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
        ) {
            if (screen is CustomScreen) {
                screen.Render()
                return@Column
            }

            for (group in screen.settings) {
                if (group.hide.collectAsState().value) continue
                when (group) {
                    is FragmentedGroup -> {
                        val fragment by group.currentFragment.collectAsState()
                        group.groupTitle?.UI(Modifier)
                        val first = fragment.settings.firstOrNull()?.id ?: return@Column
                        val last = fragment.settings.last().id
                        group.unfragmentedGroup?.also { gp ->
                            val first = gp.settings.firstOrNull()?.id ?: return@also
                            val last = gp.settings.last().id
                            gp.settings.forEach { setting ->
                                val groupPosition = when {
                                    last == first -> GroupItemClip.Full
                                    setting.id == last -> GroupItemClip.Last
                                    setting.id == first -> GroupItemClip.First
                                    else -> GroupItemClip.None
                                }
                                CompositionLocalProvider(LocalGroupPosition provides groupPosition) {

                                            setting.UI(modifier = Modifier)

                                }
                            }
                        }
                        AnimatedContent(
                            modifier = Modifier
                                .then(group.modifier)
                                .padding(group.paddingValues),
                            targetState = fragment
                        ) { fr ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
                            ) {
                                fr.settings.forEach { setting ->
                                    val groupPosition = when {
                                        last == first -> GroupItemClip.Full
                                        setting.id == last -> GroupItemClip.Last
                                        setting.id == first -> GroupItemClip.First
                                        else -> GroupItemClip.None
                                    }
                                    CompositionLocalProvider(LocalGroupPosition provides groupPosition) {

                                        setting.UI(modifier = Modifier)

                                    }
                                }
                            }
                        }
                    }

                    is Group -> {

                        val groupItems = group.settings
                        group.groupTitle?.UI(Modifier)
                        val first = groupItems.firstOrNull()?.id ?: return@Column
                        val last = groupItems.last().id
                        groupItems.forEach { setting ->

                            val groupPosition = when {
                                last == first -> GroupItemClip.Full
                                setting.id == last -> GroupItemClip.Last
                                setting.id == first -> GroupItemClip.First
                                else -> GroupItemClip.None
                            }

                            CompositionLocalProvider(LocalGroupPosition provides groupPosition) {

                                setting.UI(modifier = Modifier)

                            }
                        }

                    }
                }

            }
        }
    }

}




