package com.daniil.csb.local

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(customScreen.paddingValues)
                .then(customScreen.modifier)
        ) {
            customScreen.Render()
        }
    }
}


