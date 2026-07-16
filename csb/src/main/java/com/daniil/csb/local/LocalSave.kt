package com.daniil.csb.local

import com.daniil.csb.persistence.SaveSettingPackage

data class LocalSave(
    val savePackages: List<SaveSettingPackage?>,
)