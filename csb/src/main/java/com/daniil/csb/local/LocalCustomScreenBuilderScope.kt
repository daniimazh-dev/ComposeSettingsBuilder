package com.daniil.csb.local

import androidx.compose.runtime.Composable
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.SettingBuilder
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.ScreenBuilderScope
import com.daniil.csb.screens.ContentConfiguredToken


@CsbDslMarkers
class LocalCustomScreenBuilderScope internal constructor(
    val localSettingsController: LocalSettingsController
) : SettingBuilder() {
    var content: @Composable CustomScreen.CustomScreenScope.() -> Unit = { AllSettings() }
        private set

    fun setContent(content: @Composable (CustomScreen.CustomScreenScope.() -> Unit)): ContentConfiguredToken {
        this.content = content
        return ContentConfiguredToken()
    }
    fun useDefaultContent(): ContentConfiguredToken = ContentConfiguredToken()

}

@CsbDslMarkers
class LocalScreenBuilderScope internal constructor(
    val localSettingsController: LocalSettingsController
) : ScreenBuilderScope()