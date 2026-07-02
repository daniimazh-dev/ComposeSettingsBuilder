package com.daniil.csb.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.GroupItemClip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface SettingInterface <T> {
    val id: String
    val title: String
    val description: String?
    val defaultValue: T
    val value: StateFlow<T>
    val enabled: StateFlow<Boolean>
    var isSaveSetting: Boolean
    val focusState: MutableStateFlow<Boolean>


    fun saveOff() { isSaveSetting = false }
    fun saveOn() { isSaveSetting = true }

    fun focus(state: Boolean) { focusState.value = state }

    fun enabled(state: Boolean)

    fun changeValue(newValue: T)
    fun fetchValue(): StateFlow<T> = value
    fun resetToDefault() { changeValue(defaultValue) }

    fun saveLogic(serializer: KSerializer<T>? = null): SaveSettingPackage? {
        if (!isSaveSetting) return null
        if (serializer != null) return saveJson(serializer)
        val data = value.value ?: return null
        return when (data::class) {
            Unit::class -> SaveSettingPackage.UnitPackage(id, enabled.value)
            String::class -> SaveSettingPackage.StringPackage(id, enabled.value, data as String)
            Int::class -> SaveSettingPackage.IntPackage(id, enabled.value, data as Int)
            Float::class -> SaveSettingPackage.FloatPackage(id, enabled.value, data as Float)
            Boolean::class -> SaveSettingPackage.BooleanPackage(id, enabled.value, data as Boolean)
            else -> error("Save method for setting \"$id\" not found. Set serealizer paramerter")
        }
    }
    fun loadLogic(pack: SaveSettingPackage, serializer: KSerializer<T>? = null) {
        enabled(pack.enable) // Enable
        if (pack is SaveSettingPackage.UnitPackage) return // Pack is unit
        if (pack is SaveSettingPackage.JsonPackage) {
            if (serializer == null) error("Setting \"$id\" return JsonPackage but serializer for load not found")
            loadJson(pack, serializer)
        } else {
            @Suppress("UNCHECKED_CAST")
            if (isSaveSetting) changeValue(pack.value as T) // Set value
        }
    }

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
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        position: GroupItemClip? = null,
    )
}