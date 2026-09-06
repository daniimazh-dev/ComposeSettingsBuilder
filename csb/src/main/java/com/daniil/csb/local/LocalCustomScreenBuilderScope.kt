package com.daniil.csb.local

import androidx.compose.runtime.Composable
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.settingcore.SettingBuilder
import com.daniil.csb.screens.CustomScreen
import com.daniil.csb.screens.ScreenBuilderScope
import com.daniil.csb.screens.ContentConfiguredToken


@CsbDslMarkers
class LocalCustomScreenBuilderScope internal constructor(
    val localController: LocalSettingsController
) : SettingBuilder() {
    var content: @Composable CustomScreen.CustomContentScreenScope.() -> Unit = { AllSettings() }
        private set

    fun setContent(content: @Composable (CustomScreen.CustomContentScreenScope.() -> Unit)): ContentConfiguredToken {
        this.content = content
        return ContentConfiguredToken()
    }
    fun useEmptyContent(): ContentConfiguredToken = ContentConfiguredToken()

}

@CsbDslMarkers
class LocalScreenBuilderScope internal constructor(
    val localController: LocalSettingsController,
    id: String = "local_settings"
) : ScreenBuilderScope(id)