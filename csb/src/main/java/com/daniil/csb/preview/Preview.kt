package com.daniil.csb.preview

import android.annotation.SuppressLint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CSBTranslator
import com.daniil.csb.R
import com.daniil.csb.SettingsScreen
import com.daniil.csb.registerSettingScreens
import com.daniil.csb.screens.title.ScreenTitle
import com.daniil.csb.settings.ContentChoice
import com.daniil.csb.settings.Select
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
    remember {
        previewInit()
    }
    val style = CSB.getValue<Select.Option>("theme_select").collectAsState().value
    val isDarkTheme = CSB.getValue<Boolean>("dark_mode").collectAsState().value
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
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

private fun previewInit() = registerSettingScreens {
    CSB.config {
        flag("disableStored")
        translator = object : CSBTranslator {
            @Composable
            override fun translate(key: String): String = key
        }
    }
    createScreen("MainScreen") {
        title = ScreenTitle.setText(res(R.string.app_name))
        group("All settings") {
            isHide = true
            createSwitch("Switch") {}
            createMultiplySelect("Multiply select")
            createTimePicker("Timer picker")
            createDatePicker("Date picker")
            createAction("Action")
            createColorPicker("Color picker")

            createContentChoice(" Content choice") {
                uiMode = ContentChoice.UIMode.Row
                onChangeValue = { CSB.setValue<Boolean>("dark_mode", it == "1") }
                defaultValueId = "1"
                option("0") { ThemeToggleIcon(isDarkTheme = false, it)  }
                option("1") { ThemeToggleIcon(isDarkTheme = true, it)  }
            }

            createCounter("Counter")
            createCustomSetting("Custom setting") {
                defaultValue = Unit
                useEmptyContent()
            }
            createInfo("Info")
            createRedirect("Redirect") { setRedirect("new") }
            createSelect("Select") {
                option("1", "First")
            }
            createSlider("Slider")
            createRangeSlider("Range slider")
            createTextField("Text field")
            createPasswordField("Password field")
            createSearchField("Search field")
            createRatingBar("Rating bar")
            crateProgressBar("Progress bar")

            createTabBar("Tab bar") {
                tab("1") { }
            }

            createCodePreview("Code preview")
            createFilePicker("File picker") {
                setContract(ActivityResultContracts.GetContent())
            }
        }
    }
    createScreen("new") {

    }
    createAbstractScreen("Abstract") {
        createSelect("theme_select") {
            defaultValueId = "material"
            option("material", "Material")
            option("bobble", "Bobble")
            option("classic", "classic")
        }
        createSwitch("dark_mode") { defaultValue = true }
    }
}



@Composable
fun ThemeToggleIcon(
    isDarkTheme: Boolean,
    isActive: Boolean,
    shape: Shape = LocalSettingsStyle.current.edgeGroupCorner,
    size: Dp = 60.dp,
    modifier: Modifier = Modifier
) {
    // Color animation depending on theme and activity
    val skyColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFE0F7FA) // Active day sky
            isDarkTheme && isActive -> Color(0xFF1A1B2F)  // Active night sky
            else -> Color(0xFFE0E0E0)                      // Inactive background (gray)
        },
        animationSpec = tween(500), label = "Sky"
    )

    val celestialColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFFFB300) // Active Sun
            isDarkTheme && isActive -> Color(0xFFFFF59D)  // Active Moon
            else -> Color(0xFF9E9E9E)                      // Inactive luminary
        },
        animationSpec = tween(500), label = "Celestial"
    )

    val mountainColor1 by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFF90A4AE) // Active mountains day
            isDarkTheme && isActive -> Color(0xFF37474F)  // Active mountains night
            else -> Color(0xFFBDBDBD)                      // Inactive mountains
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

    Canvas(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(skyColor)
    ) {
        val width = size.toPx()
        val height = size.toPx()

        // 2. Luminary (Sun or Moon)
        if (!isDarkTheme) {
            // Sun
            drawCircle(
                color = celestialColor,
                radius = width * 0.15f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
        } else {
            // Moon (by overlaying sky color circle)
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

        // 3. Back mountain
        val path1 = Path().apply {
            moveTo(width * 0.12f, height * 0.80f)
            lineTo(width * 0.45f, height * 0.45f)
            lineTo(width * 0.80f, height * 0.80f)
            close()
        }
        drawPath(path = path1, color = mountainColor1)

        // 4. Front mountain
        val path2 = Path().apply {
            moveTo(width * 0.30f, height * 0.80f)
            lineTo(width * 0.65f, height * 0.52f)
            lineTo(width * 0.90f, height * 0.80f)
            close()
        }
        drawPath(path = path2, color = mountainColor2)
    }
}
 
