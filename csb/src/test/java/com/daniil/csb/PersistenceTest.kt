package com.daniil.csb

import androidx.compose.ui.graphics.Color
import com.daniil.csb.persistence.SaveSettingPackage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersistenceTest {

    @Test
    fun `test BooleanPackage serialization`() {
        val pack: SaveSettingPackage = SaveSettingPackage.BooleanPackage("id1", true, true)
        val json = Json.encodeToString(pack)
        val decoded = Json.decodeFromString<SaveSettingPackage>(json)
        
        assertTrue(decoded is SaveSettingPackage.BooleanPackage)
        assertEquals(pack, decoded)
    }

    @Test
    fun `test IntPackage serialization`() {
        val pack: SaveSettingPackage = SaveSettingPackage.IntPackage("id2", false, 42)
        val json = Json.encodeToString(pack)
        val decoded = Json.decodeFromString<SaveSettingPackage>(json)
        
        assertTrue(decoded is SaveSettingPackage.IntPackage)
        assertEquals(pack, decoded)
    }

    @Test
    fun `test StringListPackage serialization`() {
        val pack: SaveSettingPackage = SaveSettingPackage.StringListPackage("id3", true, listOf("a", "b", "c"))
        val json = Json.encodeToString(pack)
        val decoded = Json.decodeFromString<SaveSettingPackage>(json)
        
        assertTrue(decoded is SaveSettingPackage.StringListPackage)
        assertEquals(pack, decoded)
    }

    @Test
    fun `test ColorPackage serialization`() {
        val pack: SaveSettingPackage = SaveSettingPackage.ColorPackage("id4", true, Color.Red)
        val json = Json.encodeToString(pack)
        val decoded = Json.decodeFromString<SaveSettingPackage>(json)
        
        assertTrue(decoded is SaveSettingPackage.ColorPackage)
        assertEquals(pack, decoded)
    }
}
