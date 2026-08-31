package com.daniil.csb.settings

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwitchTest {

    @Test
    fun `test switch initial value`() {
        val switch = Switch(
            id = "test_switch",
            defaultValue = true,
            title = "Test Switch",
            description = null
        )
        assertTrue(switch.value.value)
    }

    @Test
    fun `test switch changeValue updates state and triggers callback`() {
        var callbackValue = false
        val switch = Switch(
            id = "test_switch",
            defaultValue = false,
            title = "Test Switch",
            description = null,
            onChangeValue = { callbackValue = it }
        )

        switch.changeValue(true)
        assertTrue(switch.value.value)
        assertTrue(callbackValue)
    }

    @Test
    fun `test switch enabled state`() {
        val switch = Switch(
            id = "test_switch",
            defaultValue = false,
            title = "Test Switch",
            description = null,
            enabled = true
        )
        assertTrue(switch.enabled.value)
        switch.enabled(false)
        assertFalse(switch.enabled.value)
    }
}
