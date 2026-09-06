package com.daniil.csb.settings.settingcore

/**
 * A token confirming that the configuration was successfully registered in the builder.
 */
class SettingToken<out T : ComposeSetting<*>> internal constructor(val setting: T)