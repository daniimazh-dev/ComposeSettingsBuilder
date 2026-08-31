package com.daniil.csb

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.daniil.csb.settings.Switch
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.Material3
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SwitchUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSwitchRenderingAndClick() {
        var clicked = false
        val switch = Switch(
            id = "test_switch",
            defaultValue = false,
            title = "Test Switch",
            description = "Test Description",
            onChangeValue = { clicked = it }
        )

        composeTestRule.setContent {
            CompositionLocalProvider(LocalSettingsStyle provides CSBStyle.Material3()) {
                switch.UI(modifier = Modifier, position = null)
            }
        }

        // Check if title is displayed
        composeTestRule.onNodeWithText("Test Switch").assertExists()
        
        // Perform click (DefaultSettingUI should handle click to toggle)
        composeTestRule.onNodeWithText("Test Switch").performClick()
        
        assertTrue(clicked)
        assertTrue(switch.value.value)
    }
}
