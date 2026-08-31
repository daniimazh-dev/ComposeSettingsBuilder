package com.daniil.csb

import com.daniil.csb.screens.title.ScreenTitle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DslBuilderTest {

    @Test
    fun `test registerSettingScreens populates screenHeap`() {
        registerSettingScreens {
            createScreen("Main") {
                title = ScreenTitle.setText("Main Screen")
            }
            createScreen("Settings") {
                title = ScreenTitle.setText("Settings Screen")
            }
        }

        val screens = CSB.navigationModel.screenHeap.value
        assertEquals(2, screens.size)
        assertEquals("Main", screens[0].id)
        assertEquals("Settings", screens[1].id)
    }

    @Test
    fun `test group creation within screen`() {
        registerSettingScreens {
            createScreen("Main") {
                group("Group1") {
                    // settings
                }
            }
        }

        val screen = CSB.navigationModel.screenHeap.value.find { it.id == "Main" }
        assertTrue(screen != null)
        assertEquals(1, screen!!.settings.size)
        assertEquals("Group1", screen.settings[0].id)
    }
}
