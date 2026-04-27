package com.daniil.csb.classes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.util.ItemGroupPosition
import com.daniil.csb.screens.ScreenInstance
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KClass

class Custom<T : Any>(
    override var id: String,
    val defaultValue: T,
    override val title: String,
    override val description: String,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean,
    val ignoreGroupClip: Boolean = false,
    val onClick: () -> Unit,
    val content: (@Composable () -> Unit)?,
    val clazz: KClass<T>
) : SettingsSealed<T>() {
    private var _value = MutableStateFlow(this@Custom.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: T) {
        _value.value = newValue
    }

    override fun resetToDefault() { changeValue(this@Custom.defaultValue) }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        val data = value.value

        return when (clazz) {
            String::class -> SaveSettingPackage.StringPackage(id, enabled.value, data as String)
            Int::class -> SaveSettingPackage.IntPackage(id, enabled.value, data as Int)
            Float::class -> SaveSettingPackage.FloatPackage(id, enabled.value, data as Float)
            Boolean::class -> SaveSettingPackage.BooleanPackage(id, enabled.value, data as Boolean)
            else -> SaveSettingPackage.UnitPackage(id, enabled.value)
        }
    }

    class CustomBuilderScope<T>() {
        var defaultValue: T? = null
        var content: (@Composable () -> Unit)? = null
        var ignoreGroupClip: Boolean = false
        var onClick: () -> Unit = {}
        var title = "Custom"
        var description = ""
        var enabled = true
        var isSaveSetting = true
    }

    class Builder<T : Any>(
        val id: String,
        val clazz: KClass<T>,
        builderScope: CustomBuilderScope<T>.() -> Unit = {}
    ) {
        val scope = CustomBuilderScope<T>().apply(builderScope)
        fun create(): Custom<T> = with(scope) {
            return Custom(id, defaultValue!!, title, description, enabled,  isSaveSetting, ignoreGroupClip, onClick = { onClick() }, content, clazz)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(screen: ScreenInstance, position: ItemGroupPosition) {
        if (content == null) return
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        DefaultContainer(
            modifier = Modifier.fillMaxWidth(),
            focusState = focusState,
            enabled = enabled,
            itemGroupPosition = if (ignoreGroupClip) ItemGroupPosition.None else position,
            onClick = { onClick() }
        ) {
            content()
        }
    }
}

//inline fun <reified T : Any> CustomSetting(
//    id: String,
//    noinline builder: Custom.CustomBuilderScope<T>.() -> Unit
//): Custom<T> {
//    return Custom.Builder(id, T::class, builder).create()
//}
