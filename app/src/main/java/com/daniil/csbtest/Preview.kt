package com.daniil.csbtest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.SettingsScreen
import com.daniil.csb.settingui.styles.CSBStyle

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    initSettings()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("CSB Preview")
                },
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SettingsScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            paddingValues = innerPadding,
            style = CSBStyle.Unspecified(isSystemInDarkTheme())
        )
    }
}