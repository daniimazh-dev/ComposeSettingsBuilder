package com.daniil.csb

import com.daniil.csb.settings.Switch
import com.daniil.csb.screens.Screen
import com.daniil.csb.group.Group
import android.content.Context
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import io.mockk.mockk

class CsbCoreTest {

    @Before
    fun setup() {
        val mockContext = mockk<Context>(relaxed = true)
        every { mockContext.applicationContext } returns mockContext
        CSB.init(mockContext)

        // Reset state for each test
        CSB.config { }
        registerSettingScreens {
            createScreen("Main") {
                group("General") {
                    createSwitch("test_switch") {
                        defaultValue = false
                    }
                }
            }
        }
    }

    @Test
    fun `test getValue and setValue`() {
        val switchValue = CSB.getValue<Boolean>("test_switch")
        assertFalse(switchValue.value)
        
        CSB.setValue("test_switch", true)
        assertTrue(switchValue.value)
    }

    @Test
    fun `test resetToDefault`() {
        CSB.setValue("test_switch", true)
        assertTrue(CSB.getValue<Boolean>("test_switch").value)
        
        CSB.resetToDefault("test_switch")
        assertFalse(CSB.getValue<Boolean>("test_switch").value)
    }

    @Test(expected = IllegalStateException::class)
    fun `test findSettingById throws exception if not found and flag is off`() {
        CSB.findSettingById("non_existent").getOrThrow()
    }

    @Test
    fun `test ignoreSettingNotFoundError flag`() {
        CSB.config {
            + "f:ignoreSettingNotFoundError"
        }
        
        val result = CSB.findSettingById("non_existent")
        assertTrue(result.isFailure)
    }
}
