package com.daniil.csb.classes

abstract class ComposeSetting<T>(): SettingInterface<T>
abstract class ComposeGroup(): ComposeSetting<Unit>()