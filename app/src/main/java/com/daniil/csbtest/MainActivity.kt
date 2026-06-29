package com.daniil.csbtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.daniil.csb.CSB
import com.daniil.csb.R
import com.daniil.csb.SettingsScreen
import com.daniil.csb.classes.MultiplySelect
import com.daniil.csb.classes.Select
import com.daniil.csb.classes.createColorPicker
import com.daniil.csb.classes.createGroupTitle
import com.daniil.csb.classes.createInfo
import com.daniil.csb.classes.createMultiplySelect
import com.daniil.csb.classes.createRedirect
import com.daniil.csb.classes.createSelect
import com.daniil.csb.classes.createSlider
import com.daniil.csb.classes.createStringData
import com.daniil.csb.classes.createSwitch
import com.daniil.csb.classes.utils.registerSettingScreens
import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen
import com.daniil.csbtest.ui.theme.ComposeSettingsBuilderTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {

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
                            .padding(innerPadding)
                            .fillMaxSize(),
                        paddingValues = PaddingValues(16.dp)
                    )
                }
            }
        }
    }
}

fun initSettings() = registerSettingScreens {
    createScreen("Main") {
        title = null
//            modifier = Modifier.fillMaxSize().padding(16.dp)
//            title = "Settings"
        group {
            createGroupTitle("Main Screen tittle")
            createRedirect("redirect") {
                title = "Redirect"
                description = "This is text redirect"
                focus = "color2"
                title = "Redirect"
                redirectToId = "customScreen"
            }
            createRedirect("redirect2") {
                title = "Redirect 2"
                description = "This is text redirect"
                focus = "info"
                redirectToId = "RedirectTest"
            }
            createMultiplySelect("multiply_select") {
                val options = listOf(
                    MultiplySelect.Option(id = "1", title = "Item 1"),
                    MultiplySelect.Option(id = "2", title = "Item 2"),
                    MultiplySelect.Option(id = "3", title = "Item 3"),
                    MultiplySelect.Option(id = "4", title = "Item 4"),
                )
                defaultValue = options
                this.options = options

            }
            createSelect("select") {
                val options = listOf(
                    Select.Option(id = "1", title = "Item 1"),
                    Select.Option(id = "2", title = "Item 2"),
                    Select.Option(id = "3", title = "Item 3"),
                    Select.Option(id = "4", title = "Item 4"),
                )
                defaultValue = options[0]
                this.options = options
            }
            createSwitch("switch") {
                defaultValue = true
                title = "Switch"
                onChangeValue = { state ->
                    CSB.enable("switch2", state)
                    CSB.enable("slider", state)
                }
                description = "This is test switch ui"
            }
            createColorPicker("color") {
                defaultValue = Color.Blue
            }
            createStringData("stringData") {
                label = { Text("Test") }
            }
        }
    }
    createScreen("RedirectTest") {

        title = "Redirect test"
        group {
            createSwitch("switch2") {
                defaultValue = false
                title = "Switch 2"
                description = "This is test switch 2 ui"
                onChangeValue = {
                    CSB.hideGroup("sliderGroup", !it)
                }
            }

        }
        group("sliderGroup") {
            createGroupTitle("groupSliderTitle")
            createSlider("slider") {
                description = "This is test slider"
                startPointRange = "Slow"
                endPointRange = "Fast"
                steps = 0
                defaultValue = 3f
                range = 0f..1f
            }
//            sb.createAction("action") {
//                title = "Go to switch"
//                requestAlert = true
//                description = "Enable switch to activate slider"
//                action = { CSB.navigateToSetting("switch2") }
//
//            },

        }
        group {
            createInfo("info") {
                title = ""
                description = "Enable switch and switch 2 to activate"
                onClick = {
                    CSB.navigateToSetting("Main Screen tittle")
                    CSB.navigateToSetting("switch")
                }
            }
        }


    }
    createAbstractScreen("Abstract") {
        createSwitch("switch3") { defaultValue = true }
    }



    createCustomScreen("customScreen") {
        title = "Custom Screen"

        register {
            createColorPicker("color2")
        }
        content = {
            ScreenTopBar(
                actions = {
                    Icon(
                        painter = painterResource(R.drawable.palette_icon),
                        contentDescription = null
                    )
                }
            )
            Text("This is custom screen")
            RegisteredSetting("color2")
            Text("hello")
        }

    }
}


