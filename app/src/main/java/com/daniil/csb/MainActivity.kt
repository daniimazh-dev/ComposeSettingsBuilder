package com.daniil.csb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniil.csb.classes.ColorPicker
import com.daniil.csb.classes.Custom
import com.daniil.csb.classes.Info
import com.daniil.csb.classes.Redirect
import com.daniil.csb.classes.Select
import com.daniil.csb.classes.Slider
import com.daniil.csb.classes.Switch
import com.daniil.csb.screens.AbstractScreen
import com.daniil.csb.screens.ScreenInstance
import com.daniil.csb.ui.theme.ComposeSettingsBuilderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val settingsNavigationModel by viewModels<SettingsNavigationModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        settingsNavigationModel.initialize(this)
        innitSettings()


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

    override fun onPause() {
        lifecycleScope.launch(Dispatchers.IO) {
            SettingsProvider.saveData(this@MainActivity)
        }
        super.onPause()
    }

    fun innitSettings() {
        val secondScreen =
            ScreenInstance.Builder("RedirectTest").setTitle("Redirect test").setGroupedContent {
                newGroup(id = "test", name = "", hide = true,
                    Switch.Builder("switch2") {
                        innitValue = false
                        title = "Switch 2"
                        description = "This is test switch 2 ui"
                    }.create(),
                )
                newGroup("test2", "Slider", hide = false,
                    Slider.Builder("slider") {
                        description = "This is test slider"
                        startPointRange = "Slow"
                        endPointRange = "Fast"
                        steps = 0
                        innitValue = 3f
                        range = 0f..1f
                    }.create(),
                    Info.Builder("info") {
                        title = ""
                        description = "Enable switch and switch 2 to activate"
                    }.create(),
                )

            }.build()


        val mainScreen = ScreenInstance.Builder("Main").setTitle("Settings").setContent(
            Redirect.Builder("redirect") {
                title = "Redirect"
                description = "This is text redirect"
                redirectToId = "RedirectTest"
                navigationModel = settingsNavigationModel
            }.create(),
            Select.Builder("select") {
                val options = listOf(
                    Select.Option(id = "1", title = "Item 1"),
                    Select.Option(id = "2", title = "Item 2"),
                    Select.Option(id = "3", title = "Item 3"),
                    Select.Option(id = "4", title = "Item 4"),
                )
                innitValue = options[0]
                this.options = options

            }.create(),
            Switch.Builder("switch") {
                innitValue = true
                title = "Switch"
                description = "This is test switch ui"
            }.create(),
            ColorPicker.Builder("color") {
                initValue = Color.Blue
            }.create()
        ).build()
        val abstract = AbstractScreen.Builder("Abstract")
            .setContent(
                Switch.Builder("switch3") { innitValue = true }.create()
            )
            .build()

        settingsNavigationModel.setScreensHeap(
            mainScreen, secondScreen, abstract
        )
        lifecycleScope.launch(Dispatchers.IO) {
            settingsNavigationModel.load(this@MainActivity)
        }
    }
}

