package com.daniil.csb.preview

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.SettingsScreen
import com.daniil.csb.local.LocalSettings
import com.daniil.csb.local.rememberLocalSettingsController
import com.daniil.csb.registerSettingScreens
import com.daniil.csb.screens.FragmentController
import com.daniil.csb.screens.customGroupTitle
import com.daniil.csb.settings.ContentChoice
import com.daniil.csb.settings.Select
import com.daniil.csb.settings.Switch
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.Bobble
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.ClassicDark
import com.daniil.csb.styles.ClassicLight
import com.daniil.csb.styles.Material3

@SuppressLint("RememberReturnType")
@Preview(showBackground = true)
@Composable
private fun Preview() {
    remember { initSettings() }
    val style = CSB.getValue<Select.Option>("theme_select").collectAsState().value
    val isDarkTheme = CSB.getValue<Boolean>("dark_mode").collectAsState().value
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            SettingsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                paddingValues = PaddingValues(16.dp),
                style = when (style.id) {
                    "material" -> CSBStyle.Material3()
                    "bobble" -> CSBStyle.Bobble()
                    "classic" -> if (isDarkTheme) CSBStyle.ClassicDark else CSBStyle.ClassicLight
                    else -> CSBStyle.Material3()
                }
            )
        }
    }

}


private fun initSettings() = registerSettingScreens {
    CSB.config {
//        +"flag:disableStored"
//        +"flag:enableDebugMode"
    }
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
        group {
            createTabBar("tab") {
                tab("first")
                tab("second")
                tab("third")
                tab("fourth")
            }


        }
//        createTabBar("switch_tab", CSB.fragmentController("Fragment"))
        fragmentedGroup("Fragment") {
            groupTitle = customGroupTitle("Switch stiles")
            initialFragmentId = "1"
//            createInfo("switch_info") { description = "Switch styles" }
            createTabBar("switch_tab", controller)
            fragment("1") {
                groupTitle = customGroupTitle("Default")
                createSwitch("switch_style1") {
                    title = "Switch 1"
                    description = "Regular switch"
                    uiMode = Switch.UIMode.Switch
                    onChangeValue = { }
                }
            }
            fragment("2") {
                createSwitch("switch_style2") {
                    title = "Switch 2"
                    description = "Switch style RadioButton"
                    uiMode = Switch.UIMode.RadioButton
                }
            }
            fragment("3") {
                createSwitch("switch_style3") {
                    title = "Switch 3"
                    description = "Switch style SquareRadioButton"
                    uiMode = Switch.UIMode.SquareRadioButton
                }
            }
            fragment("4") {
                createSwitch("switch_style4") {
                    title = "Switch 4"
                    description = "Switch style CheckBox"
                    uiMode = Switch.UIMode.CheckBox
                }
            }
            fragment("5") {
                createSwitch("switch_style5") {
                    title = "Switch 5"
                    description = "Switch style OnOffState"
                    uiMode = Switch.UIMode.OnOffState
                }
            }
        }
        group("preview") {
            groupTitle = customGroupTitle("CSB Preview")
            createRedirect("redirect") {
                title = "Redirect"
                description = "Go to another page"
                redirectToId = "customScreen"
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
            createContentChoice("content_choice") {
                title = "Content choice"
                description = "Choice option with UI"
                @Composable
                fun Dark(state: Boolean) {
                    ThemeToggleIcon(
                        isDarkTheme = true,
                        isActive = state,
                        shape = LocalSettingsStyle.current.edgeGroupCorner,
                        size = minContentHeight
                    )
                }

                @Composable
                fun Light(state: Boolean) {
                    ThemeToggleIcon(
                        isDarkTheme = false,
                        isActive = state,
                        shape = LocalSettingsStyle.current.edgeGroupCorner,
                        size = minContentHeight
                    )
                }
                uiMode = ContentChoice.UIMode.Row
                option("Light_theme") { Light(it) }
                option("Dark_theme") { Dark(it) }
                defaultValueId = "Dark_theme"
                onChangeValue = { CSB.setValue("dark_mode", it == "Dark_theme") }
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
                setContent {
                    Row() {
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
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
    createScreen("preview_setting") {
        group {
            groupTitle = customGroupTitle("Preview settings")
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
                    CSB.groupController("preview").isShow(!state)
                }
            }
            createSwitch("disable_preview") {
                title = "Disable preview"
                description = "Disable all setting in group \"CSB preview\""
                defaultValue = false
                onChangeValue = { state ->
                    CSB.groupController("preview").isDisable(!state)
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
                            CSB.navigationModel.findScreenById("Main").settings.flatMap { it.settings }
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
                scrollState = null,
            )
        }
    }
    createAbstractScreen("Abstract") {
        createSwitch("dark_mode") {
            defaultValue = true
        }
    }


    createCustomScreen("customScreen") {
        title = "Custom screen"
        createInfo("info_custom") {
            description = "This is custom screen"
        }
        useDefaultContent()
//        setContent {
//            AllSettings()
//            repeat(100) {
//                Text("item ${it + 1}")
//            }
//        }
    }
}



@Composable
fun ThemeToggleIcon(
    isDarkTheme: Boolean,
    isActive: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    // Анімація кольорів залежно від теми та її активності
    val skyColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFE0F7FA) // Активне денне небо
            isDarkTheme && isActive -> Color(0xFF1A1B2F)  // Активне нічне небо
            else -> Color(0xFFE0E0E0)                      // Неактивний фон (сірий)
        },
        animationSpec = tween(500), label = "Sky"
    )

    val celestialColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFFFB300) // Активне Сонце
            isDarkTheme && isActive -> Color(0xFFFFF59D)  // Активний Місяць
            else -> Color(0xFF9E9E9E)                      // Неактивне світило
        },
        animationSpec = tween(500), label = "Celestial"
    )

    val mountainColor1 by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFF90A4AE) // Активні гори день
            isDarkTheme && isActive -> Color(0xFF37474F)  // Активні гори ніч
            else -> Color(0xFFBDBDBD)                      // Неактивні гори
        },
        animationSpec = tween(500), label = "Mountain1"
    )

    val mountainColor2 by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFF78909C)
            isDarkTheme && isActive -> Color(0xFF263238)
            else -> Color(0xFF757575)
        },
        animationSpec = tween(500), label = "Mountain2"
    )

    Canvas(modifier = modifier
        .size(size)
        .clip(shape)
        .background(skyColor)
    ) {
        val width = size.toPx()
        val height = size.toPx()

        // 2. Світило (Сонце або Місяць)
        if (!isDarkTheme) {
            // Сонце
            drawCircle(
                color = celestialColor,
                radius = width * 0.15f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
        } else {
            // Місяць (через накладання кола кольору неба)
            drawCircle(
                color = celestialColor,
                radius = width * 0.15f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
            drawCircle(
                color = skyColor,
                radius = width * 0.15f,
                center = Offset(width * 0.43f, height * 0.30f)
            )
        }

        // 3. Задня гора
        val path1 = Path().apply {
            moveTo(width * 0.12f, height * 0.80f)
            lineTo(width * 0.45f, height * 0.45f)
            lineTo(width * 0.80f, height * 0.80f)
            close()
        }
        drawPath(path = path1, color = mountainColor1)

        // 4. Передня гора
        val path2 = Path().apply {
            moveTo(width * 0.30f, height * 0.80f)
            lineTo(width * 0.65f, height * 0.52f)
            lineTo(width * 0.90f, height * 0.80f)
            close()
        }
        drawPath(path = path2, color = mountainColor2)
    }
}
