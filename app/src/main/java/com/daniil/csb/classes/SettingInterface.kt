package com.daniil.csb.classes

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SettingInterface <T> {
    var id: String
    val title: String
    val description: String
    val value: StateFlow<T>
    val enabled: StateFlow<Boolean>

    fun enabled(state: Boolean)

    fun changeValue(newValue: T)
    fun fetchValue(): StateFlow<T>

    @Composable
    fun UI()
}
