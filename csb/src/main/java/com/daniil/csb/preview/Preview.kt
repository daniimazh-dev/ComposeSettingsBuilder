package com.daniil.csb.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.SettingsScreen
import com.daniil.csb.settings.Switch
import com.daniil.csb.local.LocalSettings
import com.daniil.csb.local.rememberLocalSettingsController
import com.daniil.csb.registerSettingScreens
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen
import com.daniil.csb.screens.customGroupTitle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3

@Preview(showBackground = true)
@Composable
private fun Preview() {
    initSettings()
    SettingsScreen(
        modifier = Modifier.fillMaxSize(),
        paddingValues = PaddingValues(16.dp),
        style = CSBStyle.Material3()
    )
}


fun initSettings() = registerSettingScreens {
    createScreen("Main") {
        title = "CSB Preview"
        group {
            groupTitle = customGroupTitle("Preview settings")
            createRedirect("redirect_preview_setting") {
                title = "Preview setting"
                description = "Settings that show the library`s capabilities"
                redirectToId = "preview_setting"
            }
        }
        createRedirect("local_settings_redirect") {
            redirectToId = "Local_setting_screen"
            title = "Local settings"
            description = "Setting without centralized binding"
        }
        group("preview") {
            groupTitle = customGroupTitle("CSB Preview")
            createRedirect("redirect") {
                title = "Redirect"
                description = "Go to another page"
                redirectToId = "customScreen"
            }
            createSwitch("switch_style1") {
                defaultValue = true
                title = "Switch 1"
                description = "Regular switch"
                uiMode = Switch.UIMode.Switch
                onChangeValue = { state -> }
            }

            createSwitch("switch_style2") {
                defaultValue = true
                title = "Switch 2"
                description = "Switch style RadioButton"
                uiMode = Switch.UIMode.RadioButton
            }
            createSwitch("switch_style3") {
                defaultValue = true
                title = "Switch 3"
                description = "Switch style SquareRadioButton"
                uiMode = Switch.UIMode.SquareRadioButton
            }
            createSwitch("switch_style4") {
                defaultValue = true
                title = "Switch 4"
                description = "Switch style CheckBox"
                uiMode = Switch.UIMode.CheckBox
            }
            createSwitch("switch_style5") {
                defaultValue = true
                title = "Switch 5"
                description = "Switch style OnOffState"
                uiMode = Switch.UIMode.OnOffState
            }
            createCounter("counter") {
                title = "Counter"
                description = "Counter in range for 0 to 100"
                steps = 1
                defaultValue = 1
                range = 0..100
            }
            createSlider("slider") {
                title = "Slider"
                description = "Slider with range for 0 to 1"
                startPointRange = "Slow 0"
                endPointRange = "Fast 1"
                steps = 0
                defaultValue = 0.5f
                range = 0f..1f
            }
            createMultiplySelect("multiply_select") {
                title = "Multiply select"
                description = "Multiple selection of items"
                option("1", "Item 1")
                option("2", "Item 2")
                option("3", "Item 3")
                option("4", "Item 4")
                defaultValue = listOf("1", "2")
            }
            createSelect("select") {
                title = "Select"
                description = "Selecting one item"
                option("1", "Item 1")
                option("2", "Item 2")
                option("3", "Item 3")
                option("4", "Item 4")
                defaultValueId = "1"
            }
            createColorPicker("color") {
                title = "Color picker"
                description = "Color selection by HSV and RGB gamma"
                defaultValue = Color.Blue
            }
            createTextField("text_field") {
                title = "Text field"
                description = "Entering an arbitrary string"
                label = { Text("Test") }
            }
            createTimePicker("time") {
                title = "Time picker"
                description = "test time picker"
            }
            createInfo("info") {
                title = "Info"
                description = "Information panel"
            }
            createAction("action") {
                title = "Action"
                description = "Some action with a alert request"
                requestAlert = true
                alertTitle = "Confirmation alert"
                alertText =
                    "The selection result is output to the lambda with result parameter true/false"
                action = { result ->

                }
            }
            createCustomSetting<Unit>("custom") {
                title = "Custom"
                description = "Custom setting with custom ui and rules"
                defaultValue = Unit
                content = {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = title.orEmpty(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = description.orEmpty(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
    createScreen("preview_setting") {
        group {
            groupTitle = customGroupTitle("Preview setting")
            createSelect("theme_select") {
                title = "Settings theme"
                description = "Change settings theme asset"
                defaultValueId = "material"
                option("material", "Material3")
                option("bobble", "Bobble")
                option("classic", "Classic")
            }
            createSwitch("hide_preview") {
                title = "Hide preview"
                description = "Hide group \"CSB preview\""
                defaultValue = false
                onChangeValue = { state ->
                    CSB.hideGroup("Main", "preview", state)
                }
            }
            createSwitch("disable_preview") {
                title = "Disable preview"
                description = "Disable all setting in group \"CSB preview\""
                defaultValue = false
                onChangeValue = { state ->
                    CSB.disableGroup("Main", "preview", !state)
                }
            }
            createAction("reset_to_default") {
                title = "Reset to default"
                description = "Reset all preview settings to default value"
                requestAlert = true
                alertTitle = "Reset preview setting"
                alertText = "Reset alert setting to default?"
                action = { result ->
                    if (result) {
                        val settings =
                            CSB.navigationModel.findScreenById("Main").settings.values.flatten()
                        settings.forEach { it.resetToDefault() }
                        CSB.navigateToGroup("preview")
                    }
                }
            }
        }
    }
    createCustomScreen("Local_setting_screen") {
        title = "Local settings"
        setContent {
            val localSettingsController = rememberLocalSettingsController()

            localSettingsController.setCustomScreen {
                createInfo("local_info") {
                    title = "local settings"
                }
                useDefaultContent()
            }
            LocalSettings(
                localController = localSettingsController,
                scrollState = null
            )
        }
    }
    createAbstractScreen("Abstract") {}



    createCustomScreen("customScreen") {
        title = "Custom screen"
        createInfo("info_custom") {
            description = "This is custom screen"
        }
        setContent {
            AllSettings()
            repeat(100) {
                Text("item ${it + 1}")
            }
        }
    }
}
