package com.daniil.csb.settings.utils

abstract class ComposeSetting<T>(
    val independentObject: Boolean = false
): ComposeSettingInterface<T>