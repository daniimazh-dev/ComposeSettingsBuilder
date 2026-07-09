package com.daniil.csb


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.settingui.LocalDebugData
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3
import com.daniil.csb.styles.SettingsStyle
import kotlinx.coroutines.flow.StateFlow

typealias ScreenTransitionSpec = AnimatedContentTransitionScope<Screen>.() -> ContentTransform

private val defaultTransitionAnimationSpring =
    tween<IntOffset>(durationMillis = 300, easing = LinearOutSlowInEasing)

private val defaultTransitionAnimationFadeSpec =  tween<Float>(200)

private val defaultSettingsScreenTransitionSpecForward: ScreenTransitionSpec = {
    slideInHorizontally(animationSpec = defaultTransitionAnimationSpring) { it }
        .togetherWith(
            fadeOut(defaultTransitionAnimationFadeSpec)
                    + slideOutHorizontally(animationSpec = defaultTransitionAnimationSpring)
        )
}
private val defaultSettingsScreenTransitionSpecBack: ScreenTransitionSpec = {
    (slideInHorizontally(animationSpec = defaultTransitionAnimationSpring) + fadeIn(defaultTransitionAnimationFadeSpec))
        .togetherWith(slideOutHorizontally(animationSpec = defaultTransitionAnimationSpring) { it })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues.Zero,
    screenTransitionForward: ScreenTransitionSpec = defaultSettingsScreenTransitionSpecForward,
    screenTransitionBack: ScreenTransitionSpec = defaultSettingsScreenTransitionSpecBack,
    style: SettingsStyle = CSBStyle.Material3()
) {
    CompositionLocalProvider(LocalSettingsStyle provides style) {
        val navigationModel = CSB.navigationModel
        val currentScreen by navigationModel.currentScreen.collectAsState()
        val lastNavigateAction by navigationModel.lastNavigateAction.collectAsState()
        val screenStack by navigationModel.screenStack.collectAsState()
        val screenHeap by navigationModel.screenHeap.collectAsState()

        BackHandler(ScreenAttribute.Primary !in (currentScreen?.attribute ?: listOf())) {
            currentScreen?.onCloseScreen()
            navigationModel.goBack()
        }
        AnimatedContent(
            modifier = modifier,
            targetState = currentScreen ?: return@CompositionLocalProvider,
            transitionSpec = if (lastNavigateAction == SettingsNavigationModel.LastNavigateAction.Forward)
                screenTransitionForward else screenTransitionBack
        ) { currentScreen ->
            if (currentScreen is AbstractScreen && !"allowDisplayAbstractScreen".isInFlag())
                error("Cannot display abstract screens")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(currentScreen.paddingValues)
                    .then(currentScreen.modifier)
            ) {
                val settingsScreenModel = currentScreen.settingsScreenModel
                val title by settingsScreenModel.title.collectAsState()
                val settings by settingsScreenModel.settings.collectAsState()
                val lazyListState = settingsScreenModel.lazyListState


                val isCanScroll by remember {
                    derivedStateOf { lazyListState.canScrollForward || lazyListState.canScrollBackward }
                }

                val isShowTitleTopBar by remember {
                    derivedStateOf {
                        val isBigTitleVisible =
                            lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "big_title" }
                        if (isCanScroll) !isBigTitleVisible else true
                    }
                }

                val isDebugModeEnable =
                    remember(currentScreen) {
                        currentScreen.attribute?.contains(ScreenAttribute.Debag) == true
                                || "enableDebugMode".isInFlag()
                    }

                val scrollFocusIndex by settingsScreenModel.scrollFocusIndex.collectAsState()
                LaunchedEffect(scrollFocusIndex) {
                    if (scrollFocusIndex != null) {
                        settingsScreenModel.lazyListState.animateScrollToItem(scrollFocusIndex!!)
                    }
                }

                if (isDebugModeEnable) {
                    Box(
                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "${currentScreen::class.simpleName} id: ${currentScreen.id}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                val isPrimaryScreen = remember(currentScreen.id) {
                    if (currentScreen.attribute.isNullOrEmpty()) return@remember true
                    ScreenAttribute.Primary !in currentScreen.attribute!!
                }
                val isShowNavigation = remember(currentScreen.id) {
                    ScreenAttribute.DisableNavigation !in (currentScreen.attribute
                        ?: listOf()) && isPrimaryScreen
                }
                val topbarHeight = 52.dp
                if (isDebugModeEnable) {
                    Box(
                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "${currentScreen::class.simpleName} id: ${currentScreen.id}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(style.itemSpacing),
                    state = lazyListState,
                    userScrollEnabled = remember { !"disableScroll".isInFlag() }
                ) {

                    if (title == null && isShowNavigation || (title != null && !isCanScroll)) {
                        item {
                            Spacer(modifier = Modifier.height(topbarHeight))
                        }
                    }
                    if (title != null && isCanScroll) {
                        item(key = "big_title") {
                            Spacer(Modifier.height(topbarHeight))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = title.orEmpty(),
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                    if (currentScreen is CustomScreen) {
                        item {
                            Column(modifier = Modifier) {
                                currentScreen.Render()
                            }
                        }
                    } else {
                        settings.keys.forEach { group ->
                            if (isDebugModeEnable) {
                                item {
                                    Box(
                                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Text(
                                            text = "Group id: ${group.id} | isHide: ${group.hide}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                            if (!group.hide) {
                                val groupItems = settings[group] ?: return@forEach
                                item(key = "group_title_${group.id}") {
                                    group.groupTitle?.UI(Modifier.animateItem())
                                }
                                val first = groupItems.firstOrNull()?.id ?: return@forEach
                                val last = groupItems.last().id
                                items(items = groupItems, key = { it.id }) { setting ->

                                    val groupPosition = when {
                                        "disableContainerGroupRound".isInFlag() -> GroupItemClip.None
                                        last == first -> GroupItemClip.Full
                                        setting.id == last -> GroupItemClip.Last
                                        setting.id == first -> GroupItemClip.First
                                        else -> GroupItemClip.None
                                    }
                                    val debugData = DebugData(
                                        settingSimpleName = setting::class.simpleName,
                                        settingId = setting.id,
                                        currentValue = setting.value
                                    ).takeIf { isDebugModeEnable }
                                    CompositionLocalProvider(LocalGroupPosition provides groupPosition) {
                                        CompositionLocalProvider(LocalDebugData provides debugData) {
                                            setting.UI(modifier = Modifier.animateItem())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (title == null) Color.Transparent else MaterialTheme.colorScheme.background)
//                        .animateContentSize()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = topbarHeight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isPrimaryScreen && isShowNavigation) {
                            FilledIconButton(
                                onClick = navigationModel::goBack,
                                colors = IconButtonDefaults.iconButtonColors().copy(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back_icon),
                                    contentDescription = "back"
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = isShowTitleTopBar,
                            enter = fadeIn(tween(200)),
                            exit = fadeOut(tween(200))
                        ) {
                            Text(
                                text = title.orEmpty(),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }


                    }

                }

            }
        }
    }
}

internal data class DebugData(
    val settingSimpleName: String?,
    val settingId: String,
    val currentValue: StateFlow<*>
)