package com.daniil.csb


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.daniil.csb.group.AbstractGroup
import com.daniil.csb.group.FragmentedGroup
import com.daniil.csb.group.Group
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.settings.settingcore.GroupItemClip
import com.daniil.csb.settingui.LocalCSBTranslator
import com.daniil.csb.settingui.LocalGroupPosition
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3
import com.daniil.csb.styles.SettingStyle
import kotlinx.coroutines.flow.StateFlow

typealias ScreenTransitionSpec = AnimatedContentTransitionScope<Screen>.() -> ContentTransform

private val defaultTransitionAnimationSpring =
    tween<IntOffset>(durationMillis = 300, easing = LinearOutSlowInEasing)

private val defaultTransitionAnimationFadeSpec = tween<Float>(200)

private val defaultSettingsScreenTransitionSpecForward: ScreenTransitionSpec = {
    slideInHorizontally(animationSpec = defaultTransitionAnimationSpring) { it }
        .togetherWith(
            fadeOut(defaultTransitionAnimationFadeSpec)
                    + slideOutHorizontally(animationSpec = defaultTransitionAnimationSpring)
        )
}
private val defaultSettingsScreenTransitionSpecBack: ScreenTransitionSpec = {
    (slideInHorizontally(animationSpec = defaultTransitionAnimationSpring) + fadeIn(
        defaultTransitionAnimationFadeSpec
    ))
        .togetherWith(slideOutHorizontally(animationSpec = defaultTransitionAnimationSpring) { it })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    screenTransitionForward: ScreenTransitionSpec = defaultSettingsScreenTransitionSpecForward,
    screenTransitionBack: ScreenTransitionSpec = defaultSettingsScreenTransitionSpecBack,
    style: SettingStyle = CSBStyle.Material3()
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalSettingsStyle provides style) {
        val navigationModel = CSB.navigationModel
        val currentScreen by navigationModel.currentScreen.collectAsState()
        val lastNavigateAction by navigationModel.lastNavigateAction.collectAsState()
        val attributes = remember(currentScreen?.id) { currentScreen?.attribute ?: listOf() }
        val enableBackHandler by remember(currentScreen?.id) { mutableStateOf(ScreenAttribute.Primary !in attributes) }

        val onBack = {
            currentScreen?.onCloseScreen()
            navigationModel.goBack()
        }

        BackHandler(enableBackHandler) { onBack() }
        CompositionLocalProvider(LocalCSBTranslator provides CSB.translator) {
            AnimatedContent(
                modifier = modifier,
                contentKey = { it.id },
                targetState = currentScreen ?: return@CompositionLocalProvider,
                transitionSpec = if (lastNavigateAction == SettingsNavigationModel.LastNavigateAction.Forward)
                    screenTransitionForward else screenTransitionBack
            ) { currentScreen ->
                if (currentScreen is AbstractScreen && !"allowDisplayAbstractScreen".isInFlag())
                    error("Cannot display abstract screens")
                Box(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(currentScreen.paddingValues)
                        .then(currentScreen.modifier)
                ) {
                    val settingsScreenModel = currentScreen.settingsScreenModel
                    val settings by settingsScreenModel.settings.collectAsState()
                    val lazyListState = settingsScreenModel.lazyListState

                    val scrollFocusIndex by settingsScreenModel.scrollFocusIndex.collectAsState()


                    val bigTitleHeight = style.topBarHeight
                    val bigTitleHeightPx = remember(density) {
                        with(density) { bigTitleHeight.toPx() }
                    }

                    if (currentScreen is CustomScreen) {
                        val scrollState = rememberScrollState()
                        val customFirstVisibleOffset by remember {
                            derivedStateOf {
                                (scrollState.value.toFloat() / bigTitleHeightPx).coerceIn(0f, 1f)
                            }
                        }
                        LaunchedEffect(scrollFocusIndex) {
                            if (scrollFocusIndex != null) {
                                scrollState.animateScrollTo((style.minHeight.value * scrollFocusIndex!!).toInt())
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            if (currentScreen.topBar != null) {
                                currentScreen.topBar?.TopScreenBarUI(
                                    onBack = onBack,
                                    height = style.topBarHeight,
                                    defaultText = currentScreen.id,
                                    isShowNavigationIcon = ScreenAttribute.Primary !in attributes,
                                    firstVisibleOffset = customFirstVisibleOffset
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                if (currentScreen.topBar != null) {
                                    Spacer(modifier = Modifier.height(style.topBarHeight))
                                }
                                currentScreen.Render()
                            }
                        }
                        return@AnimatedContent
                    }
                    LaunchedEffect(scrollFocusIndex) {
                        if (scrollFocusIndex != null) {
                            settingsScreenModel.lazyListState.animateScrollToItem(scrollFocusIndex!!)
                        }
                    }
                    val firstVisibleOffset by remember {
                        derivedStateOf {
                            if (lazyListState.firstVisibleItemIndex > 0) {
                                1f
                            } else {
                                (lazyListState.firstVisibleItemScrollOffset.toFloat() / bigTitleHeightPx).coerceIn(0f, 1f)
                            }
                        }
                    }


                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(style.itemSpacing),
                        state = lazyListState,
                        userScrollEnabled = remember { !"disableScroll".isInFlag() },
                    ) {
                        if (currentScreen.topBar != null) {
                            stickyHeader {
                                currentScreen.topBar?.TopScreenBarUI(
                                    onBack = onBack,
                                    height = style.topBarHeight,
                                    defaultText = currentScreen.id,
                                    isShowNavigationIcon = ScreenAttribute.Primary !in attributes,
                                    firstVisibleOffset = firstVisibleOffset
                                )
                            }
                        }
                        settings.forEach { group ->
                            when (group) {
                                is FragmentedGroup -> {
                                    item(key = "group_title_${group.id}") {
                                        val isGroupVisible by group.visible.collectAsState()
                                        if (isGroupVisible) {
                                            Spacer(modifier = Modifier.height(style.groupSpacing))
                                            group.groupTitle?.content?.let { it(GroupTitle.GroupTitleContentScope()) }
                                        }
                                    }
                                    group.unfragmentedGroup?.also { gp ->
                                        val first = gp.settings.firstOrNull()?.id ?: return@also
                                        val last = gp.settings.last().id

                                        items(gp.settings, key = { it.id }) { setting ->
                                            val isVisible by setting.visible.collectAsState()
                                            val isGroupVisible by group.visible.collectAsState()

                                            if (isGroupVisible && isVisible) {
                                                val groupPosition = when {
                                                    "disableContainerGroupRound".isInFlag() -> GroupItemClip.None
                                                    last == first || setting.id == first -> GroupItemClip.First
                                                    else -> GroupItemClip.None
                                                }
                                                CompositionLocalProvider(LocalGroupPosition provides groupPosition) {
                                                    setting.UI(modifier = Modifier)
                                                }
                                            }

                                        }
                                    }
                                    item(key = group.id) {
                                        val isGroupVisible by group.visible.collectAsState()
                                        if (!isGroupVisible) return@item

                                        Spacer(modifier = Modifier.height(style.groupSpacing))

                                        val fragment by group.currentFragment.collectAsState()
                                        val first =
                                            fragment.settings.firstOrNull()?.id ?: return@item
                                        val last = fragment.settings.last().id

                                        AnimatedContent(
                                            modifier = Modifier
                                                .padding(group.paddingValues)
                                                .then(group.modifier),
                                            targetState = fragment,
                                        ) { fr ->
                                            if (fr.visible.collectAsState().value) return@AnimatedContent
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
                                            ) {
                                                fr.settings.forEach { setting ->

                                                    val isVisible by setting.visible.collectAsState()
                                                    if (isVisible) {

                                                        val groupPosition = when {
                                                            "disableContainerGroupRound".isInFlag() -> GroupItemClip.None
                                                            last == first -> if (group.unfragmentedGroup != null) GroupItemClip.Last else GroupItemClip.Full
                                                            setting.id == last -> GroupItemClip.Last
                                                            setting.id == first -> if (group.unfragmentedGroup != null) GroupItemClip.Last else GroupItemClip.First
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

                                is Group -> {
                                    item(key = "group_title_${group.id}") {
                                        val isGroupVisible by group.visible.collectAsState()
                                        if (isGroupVisible) {
                                            Spacer(modifier = Modifier.height(style.groupSpacing))
                                            group.groupTitle?.content?.let { it(GroupTitle.GroupTitleContentScope()) }
                                        }
                                    }
                                    val first = group.settings.firstOrNull()?.id ?: return@forEach
                                    val last = group.settings.last().id

                                    items(items = group.settings, key = { it.id }) { setting ->

                                        val isGroupVisible by group.visible.collectAsState()
                                        val isVisible = setting.visible.collectAsState().value

                                        if (isGroupVisible && isVisible) {
                                            val groupPosition = when {
                                                "disableContainerGroupRound".isInFlag() -> GroupItemClip.None
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

                                is AbstractGroup -> {}
                            }

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