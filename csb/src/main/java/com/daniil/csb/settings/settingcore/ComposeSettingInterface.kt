package com.daniil.csb.settings.settingcore

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.settings.depend.Depends
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface ComposeSettingInterface<T> {
    val id: String
    val title: String
    val description: String?
    val defaultValue: T
    val value: StateFlow<T>
    val enabled: StateFlow<Boolean>
    var isSaveSetting: Boolean
    val customGrouping: GroupItemClip?
    val focusState: MutableStateFlow<Boolean>
    val onChangeValue: (T) -> Unit
    val depends: List<Depends>
    val visible: StateFlow<Boolean>

    fun show(state: Boolean)

    fun enabled(state: Boolean)

    fun saveOff() {
        isSaveSetting = false
    }

    fun saveOn() {
        isSaveSetting = true
    }

    fun focus(state: Boolean) {
        focusState.value = state
    }

    fun changeValue(newValue: T)
    fun fetchValue(): StateFlow<T> = value
    fun resetToDefault() {
        changeValue(defaultValue)
    }

    fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
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

    fun loadLogic(pack: SaveSettingPackage) {
        enabled(pack.enable) // Enable
        if (pack is SaveSettingPackage.UnitPackage) return // Pack is unit
        @Suppress("UNCHECKED_CAST")
        if (isSaveSetting) changeValue(pack.value as T) // Set value
    }

    fun saveJson(serializer: KSerializer<T>?): SaveSettingPackage? {
        if (!isSaveSetting) return null
        if (serializer == null) { return this.saveLogic() }
        return SaveSettingPackage.JsonPackage(
            id = id,
            enable = enabled.value,
            value = Json.encodeToString(serializer, value.value)
        )
    }

    fun loadJson(pack: SaveSettingPackage, serializer: KSerializer<T>?) {
        if (serializer == null) { loadLogic(pack); return }
        enabled(pack.enable)
        if (pack is SaveSettingPackage.JsonPackage && isSaveSetting) {
            try {
                val newValue = Json.decodeFromString(serializer, pack.value)
                enabled(pack.enable)
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
    /** C - [ComposeSetting], S - BuilderScope of [ComposeSetting] */
    interface Factory<C : ComposeSetting<*>, out S : SettingDefaultScope<C>> {
        fun SettingDslInterface.create(id: String, scope: S.() -> Unit): SettingToken<C>
    }
    interface FactoryWithToken<C : ComposeSetting<*>, out S : SettingDefaultScope<C>, T: SettingConfiguredToken> {
        fun SettingDslInterface.create(id: String, scope: S.() -> T): SettingToken<C>
    }
}
