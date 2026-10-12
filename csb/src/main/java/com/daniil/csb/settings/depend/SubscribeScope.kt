package com.daniil.csb.settings.depend

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ReadOnlySetting
import com.daniil.csb.settings.settingcore.asReadOnly

@CsbDslMarkers
class SubscribeScope<S: ComposeSetting<*>>() {
    internal var onChangeValue: ((ReadOnlySetting<S>) -> Unit)? = null
    internal var onChangeEnabled: (state: Boolean) -> Unit = {}
    internal var onChangeVisible: (state: Boolean) -> Unit = {}
    internal var onAnyChange: ((ReadOnlySetting<S>) -> Unit)? = null

    fun onChangeValue(onChange: (ReadOnlySetting<S>) -> Unit) {
        this.onChangeValue = onChange
    }
    fun onChangeEnabled(onChange: (Boolean) -> Unit) {
        this.onChangeEnabled = onChange
    }
    fun onChangeVisible(onChange: (Boolean) -> Unit) {
        this.onChangeVisible = onChange
    }

    internal var visibleIf: ((ReadOnlySetting<S>) -> Boolean)? = null
    fun visibleIf(predicate: (ReadOnlySetting<S>) -> Boolean) {
        this.visibleIf = predicate
    }

    internal var enableIf: ((ReadOnlySetting<S>) -> Boolean)? = null
    fun enableIf(predicate: (ReadOnlySetting<S>) -> Boolean) {
        this.enableIf = predicate
    }

    fun onAnyChange(onChange: (ReadOnlySetting<S>) -> Unit) {
        this.onAnyChange = onChange
    }

    internal fun build(id: String): SubscribeData {
        return SubscribeData(
            id = id,
            visibleIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                visibleIf?.invoke(setting.asReadOnly() as ReadOnlySetting<S>) ?: true
            },
            enableIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                enableIf?.invoke(setting.asReadOnly() as ReadOnlySetting<S>) ?: true
            },
            onChangeValue = { setting ->
                @Suppress("UNCHECKED_CAST")
                onChangeValue?.invoke(setting.asReadOnly() as ReadOnlySetting<S>)
            },
            onChangeEnabled = onChangeEnabled,
            onChangeVisible = onChangeVisible,
            onAnyChange = { setting ->
                @Suppress("UNCHECKED_CAST")
                onAnyChange?.invoke(setting.asReadOnly() as ReadOnlySetting<S>)
            }
        )
    }
}

internal data class SubscribeData(
    val id: String,
    override val visibleIf: (ComposeSetting<*>) -> Boolean,
    override val enableIf: (ComposeSetting<*>) -> Boolean,
    override val onChangeValue: (ComposeSetting<*>) -> Unit,
    override val onChangeEnabled: (state: Boolean) -> Unit,
    override val onChangeVisible: (state: Boolean) -> Unit,
    override val onAnyChange: (ComposeSetting<*>) -> Unit
): Depends()
