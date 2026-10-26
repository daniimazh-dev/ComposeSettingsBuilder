package com.daniil.csb

import com.daniil.csb.screens.TopScreenBar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DslBuilderTest {

    @Test
    fun `test registerSettingScreens populates screenHeap`() {
        registerSettingScreens {
            createScreen("Main") {
                topBar = TopScreenBar.text("Main Screen")
            }
            createScreen("Settings") {
                topBar = TopScreenBar.text("Settings Screen")
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
