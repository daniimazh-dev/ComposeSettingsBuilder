package com.daniil.csb.local

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.ComposeGroup
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.classes.utils.LocalGroupPosition
import com.daniil.csb.settingui.styles.CSBStyle
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import com.daniil.csb.settingui.styles.SettingsStyle
import kotlin.collections.filterNot


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettings(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues.Zero,
    style: SettingsStyle = CSBStyle.Material3(),
    localController: LocalSettingsController,

    ) {
    val customScreen = localController.customScreen
    CompositionLocalProvider(LocalSettingsStyle provides style) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(customScreen.paddingValues?.let { Modifier.padding(it) } ?: Modifier)
                .then(customScreen.modifier ?: modifier)
        ) {
            val settingsScreenModel = customScreen.settingsScreenModel
            val settings by settingsScreenModel.settings.collectAsState()

            val scrollFocusIndex by settingsScreenModel.scrollFocusIndex.collectAsState()
            LaunchedEffect(scrollFocusIndex) {
                if (scrollFocusIndex != null) {
                    settingsScreenModel.lazyListState.animateScrollToItem(scrollFocusIndex!!)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = settingsScreenModel.lazyListState
            ) {
                settings.keys.forEach { group ->
                    if (!group.hide) {
                        val groupItems = settings[group] ?: return@forEach
                        items(items = groupItems, key = { it.id }) { setting ->
                            val groupWithoutTitle = groupItems.filterNot { it is ComposeGroup }
                            val first = groupWithoutTitle.first().id
                            val last = groupWithoutTitle.last().id
                            val groupPosition = when {
                                last == first -> GroupItemClip.Full
                                setting.id == last -> GroupItemClip.Last
                                setting.id == first -> GroupItemClip.First
                                else -> GroupItemClip.None
                            }
                            CompositionLocalProvider(LocalGroupPosition provides groupPosition) {
                                setting.UI(modifier = Modifier.animateItem())
                            }
                        }
                    }
                }
            }
        }
    }
}


