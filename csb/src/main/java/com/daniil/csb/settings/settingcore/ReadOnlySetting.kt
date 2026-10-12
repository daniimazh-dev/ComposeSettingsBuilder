package com.daniil.csb.settings.settingcore

class ReadOnlySetting<out T : ComposeSetting<*>> internal constructor(
    val setting: T
) {
    val id: String get() = setting.id
    val enabled: Boolean get() = setting.enabled.value
    val visible: Boolean get() = setting.visible.value
    val title: String  get() = setting.title
    val description: String? get() = setting.description
}

val <V, T : ComposeSetting<V>> ReadOnlySetting<T>.value: V
    get() = setting.value.value

val <V, T : ComposeSetting<V>> ReadOnlySetting<T>.defaultValue: V
    get() = setting.defaultValue


fun <T> ComposeSetting<T>.asReadOnly(): ReadOnlySetting<ComposeSetting<T>> {
    return ReadOnlySetting(this)
}
