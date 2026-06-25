package com.daniil.csbtest

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.csb.classes.createSwitch
import com.daniil.csb.local.LocalSettings
import com.daniil.csb.local.rememberLocalSettingsController

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
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
        val localController = rememberLocalSettingsController {
            register {
                createSwitch("test")
                createSwitch("test3")
                createSwitch("test2") {
                    onChangeValue = {
                        localController.enable("test", it)
                    }
                }
            }
            content = { AllSetting() }

        }

        LocalSettings(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            paddingValues = PaddingValues(16.dp),
            localController = localController
        )
    }
}