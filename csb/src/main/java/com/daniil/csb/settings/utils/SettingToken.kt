package com.daniil.csb.settings.utils

/**
 * A token confirming that the configuration was successfully registered in the builder.
 */
class SettingToken<out T : ComposeSetting<*>> internal constructor(val setting: T)