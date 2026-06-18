package com.daniil.csbtest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.daniil.csb.SettingsNavigationModel
import com.daniil.csb.SettingsProvider
import com.daniil.csb.SettingsScreen
import com.daniil.csb.classes.MultiplySelect
import com.daniil.csb.classes.Select
import com.daniil.csb.classes.createAction
import com.daniil.csb.classes.createColorPicker
import com.daniil.csb.classes.createInfo
import com.daniil.csb.classes.createMultiplySelect
import com.daniil.csb.classes.createRedirect
import com.daniil.csb.classes.createSelect
import com.daniil.csb.classes.createSlider
import com.daniil.csb.classes.createStringData
import com.daniil.csb.classes.createSwitch
import com.daniil.csb.classes.utils.CSBCreator
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen
import com.daniil.csb.ui.theme.ComposeSettingsBuilderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val settingsNavigationModel by viewModels<SettingsNavigationModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initSettings(
            settingsNavigationModel,
            this,
            lifecycleScope,
        )

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
                            .padding(16.dp),
                        paddingValues = innerPadding,
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


}

fun initSettings(
    settingsNavigationModel: SettingsNavigationModel,
    context: Context,
    coroutineScope: CoroutineScope
) {
    settingsNavigationModel.initialize(context)
    val csb = CSBCreator()

    val secondScreen = csb.createScreen("RedirectTest") {
        title = "Redirect test"
        modifier = Modifier
        newGroup(
            csb.createSwitch("switch2") {
                defaultValue = false
                title = "Switch 2"
                description = "This is test switch 2 ui"
            },

            )
        newGroup(
            "test2", "Slider",
            csb.createSlider("slider") {
                description = "This is test slider"
                startPointRange = "Slow"
                endPointRange = "Fast"
                steps = 0
                defaultValue = 3f
                range = 0f..1f
            },
            csb.createAction("action") {
                title = "Go to switch"
                requestAlert = true
                description = "Enable switch to activate slider"
                action = { SettingsProvider.navigateToSetting("switch2") }

            },
            csb.createInfo("info") {
                title = ""
                description = "Enable switch and switch 2 to activate"
            }
        )


    }

    val mainScreen = csb.createScreen("Main") {
//            modifier = Modifier.fillMaxSize().padding(16.dp)
//            title = "Settings"
        newGroup(
            csb.createRedirect("redirect") {
                title = "Redirect"
                description = "This is text redirect"
                focus = "color2"
                redirectToId = "customScreen"
                navigationModel = settingsNavigationModel
            },
            csb.createRedirect("redirect2") {
                title = "Redirect 2"
                description = "This is text redirect"
                focus = "info"
                redirectToId = "RedirectTest"
                navigationModel = settingsNavigationModel
            },
            csb.createMultiplySelect("multiply_select") {
                val options = listOf(
                    MultiplySelect.Option(id = "1", title = "Item 1"),
                    MultiplySelect.Option(id = "2", title = "Item 2"),
                    MultiplySelect.Option(id = "3", title = "Item 3"),
                    MultiplySelect.Option(id = "4", title = "Item 4"),
                )
                defaultValue = options
                this.options = options

            },
            csb.createSelect("select") {
                val options = listOf(
                    Select.Option(id = "1", title = "Item 1"),
                    Select.Option(id = "2", title = "Item 2"),
                    Select.Option(id = "3", title = "Item 3"),
                    Select.Option(id = "4", title = "Item 4"),
                )
                defaultValue = options[0]
                this.options = options
            },
            csb.createSwitch("switch") {
                defaultValue = true
                title = "Switch"
                description = "This is test switch ui"
            },
            csb.createColorPicker("color") {
                defaultValue = Color.Blue
            },
            csb.createStringData("stringData") {
                label = { Text("Test") }
            }
        )
    }
    val abstract = csb.createAbstractScreen("Abstract",
        csb.createSwitch("switch3") { defaultValue = true }
    )

    val customScreen = csb.createCustomScreen("customScreen") {
        modifier = Modifier.background(Color.Blue)
        title = "Custom Screen"

        register(csb.createColorPicker("color2"))
        content = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ScreenTopBar(navigationModel = settingsNavigationModel) {
                    Icon(painter = painterResource(com.daniil.csb.R.drawable.palette_icon), contentDescription = null)
                }
                Text("This is custom screen")
                RegisteredSetting("color2")
                Text("hello")
            }

        }
    }

    settingsNavigationModel.setScreensHeap(
        mainScreen, secondScreen, abstract, customScreen
    )
    coroutineScope.launch(Dispatchers.IO) {
        SettingsProvider.loadData(context)
    }
}

