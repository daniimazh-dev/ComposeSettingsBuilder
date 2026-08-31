package com.daniil.csb.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SelectTest {

    @Test
    fun `test select initial value`() {
        val options = listOf(
            Select.Option("id1", "Title 1"),
            Select.Option("id2", "Title 2")
        )
        val select = Select(
            id = "test_select",
            options = options,
            defaultValue = options[0],
            title = "Test Select",
            alertTitle = "Choose",
            description = null,
            uiMode = Select.UIMode.Alert,
            isSaveSetting = false
        )
        
        assertEquals("id1", select.value.value.id)
    }

    @Test
    fun `test select changeValue by Option object`() {
        val options = listOf(
            Select.Option("id1", "Title 1"),
            Select.Option("id2", "Title 2")
        )
        val select = Select(
            id = "test_select",
            options = options,
            defaultValue = options[0],
            title = "Test Select",
            alertTitle = "Choose",
            description = null,
            uiMode = Select.UIMode.Alert,
            isSaveSetting = false
        )
        
        select.changeValue(options[1])
        assertEquals("id2", select.value.value.id)
    }

    @Test
    fun `test select changeValue by optionId`() {
        val options = listOf(
            Select.Option("id1", "Title 1"),
            Select.Option("id2", "Title 2")
        )
        val select = Select(
            id = "test_select",
            options = options,
            defaultValue = options[0],
            title = "Test Select",
            alertTitle = "Choose",
            description = null,
            uiMode = Select.UIMode.Alert,
            isSaveSetting = false
        )
        
        select.changeValue("id2")
        assertEquals("id2", select.value.value.id)
    }

    @Test
    fun `test select ignore invalid optionId`() {
        val options = listOf(Select.Option("id1", "Title 1"))
        val select = Select(
            id = "test_select",
            options = options,
            defaultValue = options[0],
            title = "Test Select",
            alertTitle = "Choose",
            description = null,
            uiMode = Select.UIMode.Alert,
            isSaveSetting = false
        )
        
        select.changeValue("invalid_id")
        assertEquals("id1", select.value.value.id)
    }
}
