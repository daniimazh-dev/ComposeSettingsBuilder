package com.daniil.csb.classes

import com.daniil.csb.SaveSettingPackage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class ComposeSetting<T>(
    val independentObject: Boolean = false
): SettingInterface<T> {

    fun saveJson(serializer: KSerializer<T>): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.JsonPackage(
            id = id,
            enable = enabled.value,
            value = Json.encodeToString(serializer, value.value)
        )
    }

    fun loadJson(pack: SaveSettingPackage, serializer: KSerializer<T>) {
        enabled(pack.enable)
        if (pack is SaveSettingPackage.JsonPackage && isSaveSetting) {
            try {
                val newValue = Json.decodeFromString(serializer, pack.value)
                changeValue(newValue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}