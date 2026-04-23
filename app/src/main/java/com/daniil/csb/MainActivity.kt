package com.daniil.csb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniil.csb.classes.Redirect
import com.daniil.csb.classes.Slider
import com.daniil.csb.classes.Switch
import com.daniil.csb.ui.theme.ComposeSettingsBuilderTheme

class MainActivity : ComponentActivity() {
    val settingsNavigationModel by viewModels<SettingsNavigationModel>()



    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        innitSettings()
        SettingsProvider.innit(settingsNavigationModel)
        setContent {
            ComposeSettingsBuilderTheme {
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
                    ComposeSettingUI(
                        modifier = Modifier
                            .padding(innerPadding),
                        navigationModel = settingsNavigationModel
                    )
                }
            }
        }
    }
    fun innitSettings() {
        val secondScreen = ScreenInstance.Builder("RedirectTest").setTitle("Redirect test").setContent(
            Switch.Builder("switch2") {
                innitValue = false
                title = "Switch 2"
                description = "This is test switch 2 ui"
            }.create(),
            Slider.Builder("slider") {
                description = "This is test slider"
                steps = 5
                innitValue = 3f
                range = 0f..5f
            }.create()
        ).build()

        val mainScreen = ScreenInstance.Builder("Main").setTitle("Settings").setContent(
            Redirect.Builder("redirect") {
                title = "Redirect"
                description = "This is text redirect"
                redirectTo = secondScreen
                navigationModel = settingsNavigationModel
            }.create(),
            Switch.Builder("switch") {
                innitValue = true
                title = "Switch"
                description = "This is test switch ui"
            }.create(),
        ).build()
        settingsNavigationModel.setScreensHeap(
            mainScreen, secondScreen
        )
    }
}

