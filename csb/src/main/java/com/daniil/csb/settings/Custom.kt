package com.daniil.csb.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.KSerializer

class Custom<T : Any> internal constructor(
    override var id: String,
    override val defaultValue: T,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    override var isSaveSetting: Boolean,
    val groupClip: GroupItemClip?,
    val onClick: () -> Unit,
    val content: (@Composable () -> Unit)?,
    override val onChangeValue: (T) -> Unit,
    val serializer: KSerializer<T>? = null
) : ComposeSetting<T>() {
    private var _value = MutableStateFlow(this@Custom.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun loadLogic(pack: SaveSettingPackage, serializer: KSerializer<T>?) {
        super.loadLogic(pack, this.serializer)
    }

    override fun saveLogic(serializer: KSerializer<T>?): SaveSettingPackage? {
        return super.saveLogic(this.serializer)
    }

    override fun changeValue(newValue: T) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    @CsbDslMarkers
    class CustomBuilderScope<T>() {
        var defaultValue: T? = null
        var content: (@Composable () -> Unit)? = null
        var onChangeValue: (T) -> Unit = {}
        var groupClip: GroupItemClip? = null
        var onClick: () -> Unit = {}
        var title: String? = null
        var description: String? = null
        var enabled = true
        var isSaveSetting = true
        var serializer: KSerializer<T>? = null
    }

    class Builder<T : Any>(
        val id: String,
        builderScope: CustomBuilderScope<T>.() -> Unit = {}
    ) {
        val scope = CustomBuilderScope<T>().apply(builderScope)
        fun create(): Custom<T> = with(scope) {
            defaultValue ?: error("Default value must be not null in setting $id")
            return Custom(id, defaultValue!!, title ?: id, description, enabled,  isSaveSetting, groupClip, onClick, content, onChangeValue, serializer)
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        if (content == null) return
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        DefaultContainer(
            modifier = modifier.fillMaxWidth(),
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = position ?: groupClip,
            onClick = onClick
        ) {
            content()
        }
    }
}


