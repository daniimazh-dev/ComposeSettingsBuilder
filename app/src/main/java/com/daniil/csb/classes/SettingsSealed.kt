package com.daniil.csb.classes

import com.daniil.csb.classes.SettingInterface
import kotlinx.coroutines.flow.MutableStateFlow

sealed class  SettingsSealed<T>: SettingInterface<T> {

}