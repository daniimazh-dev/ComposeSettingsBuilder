package com.daniil.csb


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.ComposeGroup
import com.daniil.csb.classes.GroupTitle
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.classes.utils.LocalGroupPosition
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.settingui.styles.CSBStyle
import com.daniil.csb.settingui.styles.LocalSettingsStyle
import com.daniil.csb.settingui.styles.SettingsStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    style: SettingsStyle = CSBStyle.Material3()
) {
    CompositionLocalProvider(LocalSettingsStyle provides style) {
        val navigationModel = CSB.navigationModel
        val currentScreen by navigationModel.currentScreen.collectAsState()
        val lastNavigateAction by navigationModel.lastNavigateAction.collectAsState()
        val screenStack by navigationModel.screenStack.collectAsState()
        val screenHeap by navigationModel.screenHeap.collectAsState()

        BackHandler(screenStack.size > 1) {
            navigationModel.goBack()
        }

        AnimatedContent(
            modifier = Modifier,
            targetState = currentScreen ?: return@CompositionLocalProvider,
            transitionSpec = {
                if (lastNavigateAction == SettingsNavigationModel.LastNavigateAction.Back) {
                    fadeIn(tween(300))
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { it / 1 })
                } else {
                    slideInHorizontally(animationSpec = tween(300)) { it / 1 }
                        .togetherWith(fadeOut(tween(300)))
                }
            },
        ) { currentScreen ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .then(currentScreen.paddingValues?.let { Modifier.padding(it) } ?: Modifier)
                    .then(currentScreen.modifier ?: modifier)
            ) {
                if (currentScreen is CustomScreen) {
                    currentScreen.Render()
                    return@AnimatedContent
                }

                val settingsScreenModel = currentScreen.settingsScreenModel
                val title by settingsScreenModel.title.collectAsState()
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
                    title?.let { title ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val showNavigation =
                                    remember(currentScreen.id) { screenStack.size > 1 }
                                if (showNavigation) {
                                    IconButton(
                                        onClick = {
                                            navigationModel.goBack()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_back_icon),
                                            contentDescription = "back"
                                        )
                                    }
                                }

                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleLarge
                                )

                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }

                    settings.keys.forEach { group ->
                        if (!group.hide) {
                            val group = settings[group] ?: return@forEach
                            itemsIndexed(group) { index, setting ->
                                val groupWithoutTitle = group.filterNot { it is ComposeGroup }
                                val first = groupWithoutTitle.first().id
                                val last = groupWithoutTitle.last().id
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
    }
}

