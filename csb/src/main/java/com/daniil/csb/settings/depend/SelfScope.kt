package com.daniil.csb.settings.depend

import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.settingcore.ComposeSetting
import com.daniil.csb.settings.settingcore.ReadOnlySetting


@CsbDslMarkers
class SelfScope<S: ComposeSetting<*>>() {
    internal var onChangeValue: ((S) -> Unit)? = null
    internal var onChangeEnabled: (Boolean) -> Unit = {}
    internal var onChangeVisible: (Boolean) -> Unit = {}
    internal var onAnyChange: ((S) -> Unit)? = null

    fun onChangeValue(onChange: (S) -> Unit) {
        this.onChangeValue = onChange
    }
    fun onChangeEnabled(onChange: (Boolean) -> Unit) {
        this.onChangeEnabled = onChange
    }
    fun onChangeVisible(onChange: (Boolean) -> Unit) {
        this.onChangeVisible = onChange
    }
    fun onAnyChange(onChange: (S) -> Unit) {
        this.onAnyChange = onChange
    }

    internal var visibleIf: ((ReadOnlySetting<S>) -> Boolean)? = null
    fun visibleIf(predicate: (ReadOnlySetting<S>) -> Boolean) {
        this.visibleIf = predicate
    }

    internal var enableIf: ((ReadOnlySetting<S>) -> Boolean)? = null
    fun enableIf(predicate: (ReadOnlySetting<S>) -> Boolean) {
        this.enableIf = predicate
    }

    internal fun build(): SelfData {
        return SelfData(
            onChangeValue =  { setting ->
                @Suppress("UNCHECKED_CAST")
                onChangeValue?.invoke(setting as S)
            },
            onChangeEnabled = onChangeEnabled,
            onChangeVisible = onChangeVisible,
            onAnyChange = { setting ->
                @Suppress("UNCHECKED_CAST")
                onAnyChange?.invoke(setting as S)
            },
            visibleIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                visibleIf?.invoke(ReadOnlySetting(setting as S)) ?: true
            },
            enableIf = { setting ->
                @Suppress("UNCHECKED_CAST")
                enableIf?.invoke(ReadOnlySetting(setting as S)) ?: true
            },
        )
    }
}

internal data class SelfData(
    override val onChangeValue: (ComposeSetting<*>) -> Unit,
    override val onChangeEnabled: (Boolean) -> Unit,
    override val onChangeVisible: (Boolean) -> Unit,
    override val onAnyChange: (ComposeSetting<*>) -> Unit,
    override val visibleIf: (ComposeSetting<*>) -> Boolean,
    override val enableIf: (ComposeSetting<*>) -> Boolean,
): Depends()
