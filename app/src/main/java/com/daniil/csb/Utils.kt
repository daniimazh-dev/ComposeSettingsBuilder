package com.daniil.csb

import com.daniil.csb.classes.SettingsSealed

fun <T> List<SettingsSealed<T>>.findById(id: String): SettingsSealed<T>? {
    return this.find { it.id == id }
}