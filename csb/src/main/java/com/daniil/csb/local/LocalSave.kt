package com.daniil.csb.local

import com.daniil.csb.persistence.SaveSettingPackage
import kotlinx.serialization.Serializable

@Serializable
data class LocalSave(
    val savePackages: List<SaveSettingPackage?>,
)