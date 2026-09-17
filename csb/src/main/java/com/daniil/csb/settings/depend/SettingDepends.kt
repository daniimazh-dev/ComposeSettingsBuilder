package com.daniil.csb.settings.depend

import com.daniil.csb.settings.settingcore.ComposeSetting

class DependsScope<S: ComposeSetting<*>>() {
    internal val dependsList = mutableListOf<Depends>()
    fun <T: ComposeSetting<*>> subscribe(id: String, subscribeScope: SubscribeScope<T>.() -> Unit) {
        val data = SubscribeScope<T>().apply(subscribeScope)
        dependsList.add(data.build(id))
    }
    fun self(selfScope: SelfScope<S>.() -> Unit) {
        val data = SelfScope<S>().apply(selfScope)
        dependsList.add(data.build())
    }
    internal fun getDepends() = dependsList
}

sealed class Depends() {
    abstract val onChangeValue: (ComposeSetting<*>) -> Unit
    abstract val onChangeEnabled: (Boolean) -> Unit
    abstract val onChangeVisible: (Boolean) -> Unit
    abstract val onAnyChange: (ComposeSetting<*>) -> Unit
    abstract val visibleIf: (ComposeSetting<*>) -> Boolean
    abstract val enableIf: (ComposeSetting<*>) -> Boolean
}
