package com.daniil.csb

import com.daniil.csb.screens.Screen
import com.daniil.csb.screens.ScreenAttribute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NavigationTest {

    private lateinit var navModel: SettingsNavigationModel

    @Before
    fun setup() {
        navModel = SettingsNavigationModel()
    }

    @Test
    fun `test initial primary screen setup`() {
        val screen1 = Screen.Builder("screen1").setGroupedContent(emptyList()).build()
        val screen2 = Screen.Builder("screen2").setGroupedContent(emptyList()).setAttribute(listOf(ScreenAttribute.Primary)).build()
        
        navModel.setScreensHeap(screen1, screen2)
        
        assertEquals("screen2", navModel.currentScreen.value?.id)
        assertEquals(1, navModel.screenStack.value.size)
        assertTrue(navModel.screenStack.value.contains(screen2))
    }

    @Test
    fun `test goToScreen updates current screen and stack`() {
        val screen1 = Screen.Builder("screen1").setGroupedContent(emptyList()).setAttribute(listOf(ScreenAttribute.Primary)).build()
        val screen2 = Screen.Builder("screen2").setGroupedContent(emptyList()).build()
        
        navModel.setScreensHeap(screen1, screen2)
        navModel.goToScreen(screen2)
        
        assertEquals("screen2", navModel.currentScreen.value?.id)
        assertEquals(2, navModel.screenStack.value.size)
        assertEquals(SettingsNavigationModel.LastNavigateAction.Forward, navModel.lastNavigateAction.value)
    }

    @Test
    fun `test goBack pops stack and updates current screen`() {
        val screen1 = Screen.Builder("screen1").setGroupedContent(emptyList()).setAttribute(listOf(ScreenAttribute.Primary)).build()
        val screen2 = Screen.Builder("screen2").setGroupedContent(emptyList()).build()
        
        navModel.setScreensHeap(screen1, screen2)
        navModel.goToScreen(screen2)
        navModel.goBack()
        
        assertEquals("screen1", navModel.currentScreen.value?.id)
        assertEquals(1, navModel.screenStack.value.size)
        assertEquals(SettingsNavigationModel.LastNavigateAction.Back, navModel.lastNavigateAction.value)
    }

    @Test
    fun `test findScreenById returns correct screen or throws`() {
        val screen1 = Screen.Builder("screen1").setGroupedContent(emptyList()).build()
        navModel.setScreensHeap(screen1)
        
        val found = navModel.findScreenById("screen1")
        assertEquals(screen1, found)
    }
}
