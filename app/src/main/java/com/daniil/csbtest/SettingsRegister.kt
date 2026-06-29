package com.daniil.csbtest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniil.csb.R
import com.daniil.csb.classes.createAction
import com.daniil.csb.classes.createColorPicker
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
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.screens.createAbstractScreen
import com.daniil.csb.screens.createCustomScreen
import com.daniil.csb.screens.createScreen

fun initSettings() = registerSettingScreens {
    createScreen("Main", screenAttribute = listOf(ScreenAttribute.Primary)) {
        title = null
        group {
            createGroupTitle("Setting preview") {}
            createRedirect("redirect") {
                title = "Redirect"
                description = "Go to another page"
                redirectToId = "customScreen"
            }
            createSwitch("switch") {
                defaultValue = true
                title = "Switch"
                description = "Regular switch"
                onChangeValue = { state -> }
            }
            createSlider("slider") {
                description = "This is test slider"
                startPointRange = "Slow"
                endPointRange = "Fast"
                steps = 0
                defaultValue = 3f
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
    createAbstractScreen("Abstract") {}



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

