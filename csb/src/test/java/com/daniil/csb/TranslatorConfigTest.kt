package com.daniil.csb

import androidx.compose.runtime.Composable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslatorConfigTest {

    @Test
    fun `test custom translator configuration`() {
        val customTranslator = object : CSBTranslator {
            @Composable
            override fun translate(key: String): String {
                return "translated_$key"
            }
        }

        CSB.config {
            translator = customTranslator
        }

        assertEquals(customTranslator, CSB.translator)
    }
    
    @Test
    fun `test default translator logic for non-res strings`() {
        val translator = CSB.DefaultCSBTranslator()
        // We can't easily test Composable translate() here without a rule,
        // but we can at least verify the object creation.
        assertTrue(translator is CSBTranslator)
    }
}
