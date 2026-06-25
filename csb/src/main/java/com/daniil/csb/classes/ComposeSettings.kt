package com.daniil.csb.classes

abstract class ComposeSetting<T>(
    val independentObject: Boolean = false
): SettingInterface<T>