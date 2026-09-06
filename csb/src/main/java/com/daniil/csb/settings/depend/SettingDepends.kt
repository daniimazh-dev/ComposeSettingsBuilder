package com.daniil.csb.settings.depend

import com.daniil.csb.settings.settingcore.ComposeSetting

class DependsScope() {
    internal val dependsList = mutableListOf<Depends>()
    fun <T: ComposeSetting<*>> subscribe(id: String, subscribeScope: SubscribeScope<T>.() -> Unit) {
        val data = SubscribeScope<T>().apply(subscribeScope)
        dependsList.add(data.build(id))
    }
    internal fun getDepends() = dependsList
}

sealed class Depends() {
    abstract val id: String
}