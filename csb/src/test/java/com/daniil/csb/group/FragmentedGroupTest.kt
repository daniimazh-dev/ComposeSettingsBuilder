package com.daniil.csb.group

import com.daniil.csb.CSB
import com.daniil.csb.registerSettingScreens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import android.content.Context
import io.mockk.every
import io.mockk.mockk

class FragmentedGroupTest {

    @Before
    fun setup() {
        val mockContext = mockk<Context>(relaxed = true)
        every { mockContext.applicationContext } returns mockContext
        CSB.init(mockContext)
    }

    @Test
    fun `test fragmented group switching`() {
        registerSettingScreens {
            createScreen("Main") {
                fragmentedGroup("frag_group") {
                    fragment("frag1") {
                        createSwitch("switch1")
                    }
                    fragment("frag2") {
                        createSwitch("switch2")
                    }
                }
            }
        }

        val fragGroup = CSB.navigationModel.findGroupById("frag_group") as FragmentedGroup
        assertEquals("frag1", fragGroup.activeFragmentId.value)
        
        val controller = CSB.fragmentController("frag_group")
        controller.changeFragment("frag2")
        
        assertEquals("frag2", fragGroup.activeFragmentId.value)
        assertNotNull(fragGroup.settings.find { it.id == "switch2" })
    }
}
