package com.daniil.csb

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeSettingUI(
    modifier: Modifier = Modifier,
    navigationModel: SettingsNavigationModel
) {

    val slider by SettingsProvider.getValue<Float>("slider").collectAsState()
    val switch1 by SettingsProvider.getValue<Boolean>("switch").collectAsState()
    val switch2 by SettingsProvider.getValue<Boolean>("switch2").collectAsState()
    LaunchedEffect(switch1, switch2) {

        if (!switch1) {
            SettingsProvider.setValue("switch2", false)
            SettingsProvider.setValue("slider", 0f)
        }
        SettingsProvider.enable("switch2", switch1)
        SettingsProvider.enable("slider", switch2)

    }
    LaunchedEffect(Unit) {
        delay(300)
        SettingsProvider.navigateToSetting("switch")
    }


    Box(
        modifier = modifier
            .then(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
    ) {
        SettingsScreen(navigationModel)
    }

}