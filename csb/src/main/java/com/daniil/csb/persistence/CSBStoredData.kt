package com.daniil.csb.persistence

import com.daniil.csb.SaveSettingPackage
import kotlinx.serialization.Serializable

@Serializable
data class CSBStoredData(
    val screenSettings: Map<String, List<SaveSettingPackage>> = emptyMap()
)
