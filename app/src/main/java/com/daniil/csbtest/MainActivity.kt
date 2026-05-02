package com.daniil.csbtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.SettingsProvider
import com.daniil.csb.SettingsScreen
import com.daniil.csb.classes.MultiplySelect
import com.daniil.csb.classes.Select
import com.daniil.csb.classes.createColorPicker
import com.daniil.csb.classes.createInfo
import com.daniil.csb.classes.createMultiplySelect
import com.daniil.csb.classes.createRedirect
import com.daniil.csb.classes.createSelect
import com.daniil.csb.classes.createSlider
import com.daniil.csb.classes.createStringData
import com.daniil.csb.classes.createSwitch
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen
import com.daniil.csb.ui.theme.ComposeSettingsBuilderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val settingsNavigationModel by viewModels<SettingsNavigationModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initSettings()

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
                    SettingsScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
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

    fun initSettings() {
        settingsNavigationModel.initialize(this)

        val secondScreen = createScreen("RedirectTest") {
            title = "Redirect test"

            newGroup(
                createSwitch("switch2") {
                    defaultValue = false
                    title = "Switch 2"
                    description = "This is test switch 2 ui"
                }

            )
            newGroup(
                "test2", "Slider",
                createSlider("slider") {
                    description = "This is test slider"
                    startPointRange = "Slow"
                    endPointRange = "Fast"
                    steps = 0
                    defaultValue = 3f
                    range = 0f..1f
                },
                createInfo("info") {
                    title = ""
                    description = "Enable switch and switch 2 to activate"
                }
            )


        }

        val mainScreen = createScreen("Main") {
            title = "Settings"
            newGroup(
                createRedirect("redirect") {
                    title = "Redirect"
                    description = "This is text redirect"
                    focus = "color2"
                    redirectToId = "customScreen"
                    navigationModel = settingsNavigationModel
                },
                createRedirect("redirect2") {
                    title = "Redirect 2"
                    description = "This is text redirect"
                    focus = "info"
                    redirectToId = "RedirectTest"
                    navigationModel = settingsNavigationModel
                },
                createMultiplySelect("multiply_select") {
                    val options = listOf(
                        MultiplySelect.Option(id = "1", title = "Item 1"),
                        MultiplySelect.Option(id = "2", title = "Item 2"),
                        MultiplySelect.Option(id = "3", title = "Item 3"),
                        MultiplySelect.Option(id = "4", title = "Item 4"),
                    )
                    defaultValue = options
                    this.options = options

                },
                createSelect("select") {
                    val options = listOf(
                        Select.Option(id = "1", title = "Item 1"),
                        Select.Option(id = "2", title = "Item 2"),
                        Select.Option(id = "3", title = "Item 3"),
                        Select.Option(id = "4", title = "Item 4"),
                    )
                    defaultValue = options[0]
                    this.options = options
                },
                createSwitch("switch") {
                    defaultValue = true
                    title = "Switch"
                    description = "This is test switch ui"
                },
                createColorPicker("color") {
                    defaultValue = Color.Blue
                },
                createStringData("stringData") {
                    label = { Text("Test") }
                }
            )
        }
        val abstract = createAbstractScreen("Abstract",
            createSwitch("switch3") { defaultValue = true }
            )

        val customScreen = createCustomScreen("customScreen") {
            title = "Custom Screen"

            register(createColorPicker("color2"))
            content = {
                Text("This is custom screen")
                RenderSetting("color2")
                Text("hello")
            }
        }

        settingsNavigationModel.setScreensHeap(
            mainScreen, secondScreen, abstract, customScreen
        )
        lifecycleScope.launch(Dispatchers.IO) {
            SettingsProvider.loadData(this@MainActivity)
        }
    }
}

