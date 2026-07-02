package com.daniil.csbtest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.classes.Switch
import com.daniil.csb.classes.createAction
import com.daniil.csb.classes.createColorPicker
import com.daniil.csb.classes.createCounter
import com.daniil.csb.classes.createCustomSetting
import com.daniil.csb.classes.createGroupTitle
import com.daniil.csb.classes.createInfo
import com.daniil.csb.classes.createMultiplySelect
import com.daniil.csb.classes.createRedirect
import com.daniil.csb.classes.createSelect
import com.daniil.csb.classes.createSlider
import com.daniil.csb.classes.createStringData
import com.daniil.csb.classes.createSwitch
import com.daniil.csb.classes.createTimePicker
import com.daniil.csb.classes.utils.registerSettingScreens
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen

fun initSettings() = registerSettingScreens {
    createAbstractScreen("abstract_test") {}
    createScreen("Main") {
        title = "CSB Preview"

        group {
            createGroupTitle("Preview settings")
            createRedirect("redirect_preview_setting") {
                title = "Preview setting"
                description = "Settings that show the library`s capabilities"
                redirectToId = "preview_setting"
            }
        }
        group("preview") {
            createGroupTitle("CSB Preview") {}
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
                val options = listOf(
                    "1" to "Item 1",
                    "2" to "Item 2",
                    "3" to "Item 3",
                    "4" to "Item 4",
                )
                defaultValue = listOf("1", "2")
                this.options = options

            }
            createSelect("select") {
                title = "Select"
                description = "Selecting one item"
                val options = listOf(
                    "1" to "Item 1",
                    "2" to "Item 2",
                    "3" to "Item 3",
                    "4" to "Item 4",
                )
                defaultValueId = options[0].first
                this.options = options
            }
            createColorPicker("color") {
                title = "Color picker"
                description = "Color selection by HSV and RGB gamma"
                defaultValue = Color.Blue
            }
            createStringData("stringData") {
                title = "String data"
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
                alertText = "The selection result is output to the lambda with result parameter true/false"
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
            title = "Preview setting"
            createSelect("theme_select") {
                title = "Settings theme"
                description = "Change settings theme asset"
                defaultValueId = "material"
                options = listOf(
                    "material" to "Material3",
                    "bobble" to "Bobble",
                    "classic" to "Classic",
                )
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
                    CSB.disableGroup("Main","preview", !state)
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
                        val settings = CSB.navigationModel.findScreenById("Main").settings.values.flatten()
                        settings.forEach { it.resetToDefault() }
                        CSB.navigateToGroup("preview")
                    }
                }
            }
        }
    }
    createAbstractScreen("Abstract") {}



    createCustomScreen("customScreen") {
        title = "Custom screen"
        register {
            createInfo("info_custom") {
                description = "This is custom screen"
            }
        }
        content = {
            AllSetting()
            repeat(100) {
                Text("item ${it+1}")
            }
        }

    }
}

