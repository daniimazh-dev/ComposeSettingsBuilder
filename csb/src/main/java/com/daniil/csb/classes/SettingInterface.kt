package com.daniil.csb.classes

import androidx.compose.runtime.Composable
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.GroupItemClip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SettingInterface <T> {
    val id: String
    val title: String
    val description: String?
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
    fun resetToDefault()
    fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.UnitPackage(id, enabled.value)
    }
    fun loadLogic(pack: SaveSettingPackage?) {
        if (pack == null) return // Not found save data
        enabled(pack.enable) // Enable
        if (pack is SaveSettingPackage.UnitPackage) return // Pack is unit
        @Suppress("UNCHECKED_CAST")
        if (isSaveSetting) changeValue(pack.value as T) // Set value
    }
    @Composable
    fun UI(position: GroupItemClip? = null)
}