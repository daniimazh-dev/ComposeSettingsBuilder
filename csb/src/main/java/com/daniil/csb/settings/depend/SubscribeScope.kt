package com.daniil.csb.settings.depend

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.settingcore.ComposeSetting

@CsbDslMarkers
class SubscribeScope<T: ComposeSetting<*>>() {
    internal var onChangeValue: ((ReadOnlySetting<T>) -> Unit)? = null
    var onChangeEnabled: (state: Boolean) -> Unit = {}
    var onSettingVisible: (state: Boolean) -> Unit = {}

    fun onChangeValue(onChange: (ReadOnlySetting<T>) -> Unit) {
        this.onChangeValue = onChange
    }

    internal var visibleIf: ((ReadOnlySetting<T>) -> Boolean)? = null
    fun visibleIf(predicate: (ReadOnlySetting<T>) -> Boolean) {
        this.visibleIf = predicate
    }

    internal var enableIf: ((ReadOnlySetting<T>) -> Boolean)? = null
    fun enableIf(predicate: (ReadOnlySetting<T>) -> Boolean) {
        this.enableIf = predicate
    }

    internal fun build(id: String): SubscribeData {
        return SubscribeData(
            id = id,
            visibleIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                visibleIf?.invoke(ReadOnlySetting(setting as T)) ?: true
            },
            enableIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                enableIf?.invoke(ReadOnlySetting(setting as T)) ?: true
            },
            onChangeValue = { setting ->
                @Suppress("UNCHECKED_CAST")
                onChangeValue?.invoke(ReadOnlySetting(setting as T))
            },
            onChangeEnabled = onChangeEnabled,
            onSettingVisible = onSettingVisible
        )
    }
}

internal data class SubscribeData(
    override val id: String,
    val visibleIf: (ComposeSetting<*>) -> Boolean,
    val enableIf: (ComposeSetting<*>) -> Boolean,
    val onChangeValue: (ComposeSetting<*>) -> Unit,
    val onChangeEnabled: (state: Boolean) -> Unit,
    val onSettingVisible: (state: Boolean) -> Unit
): Depends()
