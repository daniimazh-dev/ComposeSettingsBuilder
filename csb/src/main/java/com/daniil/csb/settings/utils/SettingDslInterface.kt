package com.daniil.csb.settings.utils

import com.daniil.csb.screens.ContentConfiguredToken
import com.daniil.csb.settings.Action
import com.daniil.csb.settings.ColorPicker
import com.daniil.csb.settings.ContentChoice
import com.daniil.csb.settings.Counter
import com.daniil.csb.settings.Custom
import com.daniil.csb.settings.Info
import com.daniil.csb.settings.MultiplySelect
import com.daniil.csb.settings.Redirect
import com.daniil.csb.settings.Select
import com.daniil.csb.settings.Slider
import com.daniil.csb.settings.Switch
import com.daniil.csb.settings.TextField
import com.daniil.csb.settings.TimePicker

interface SettingDslInterface {
    fun createSwitch(
        id: String,
        builder: Switch.SwitchBuilderScope.() -> Unit = { }
    ): SettingToken<Switch> =
        Switch.Builder(id, builder).create().register()

    fun createMultiplySelect(
        id: String,
        builder: MultiplySelect.MultiplySelectBuilderScope.() -> Unit
    ): SettingToken<MultiplySelect> =
        MultiplySelect.Builder(id, builder).create().register()

    fun createTimePicker(
        id: String,
        builder: TimePicker.TimePickerBuilderScope.() -> Unit= {}
    ): SettingToken<TimePicker> =
        TimePicker.Builder(id, builder).create().register()

    fun createAction(
        id: String,
        builder: Action.ActionBuilderScope.() -> Unit = {}
    ): SettingToken<Action> =
        Action.Builder(id, builder).create().register()

    fun createColorPicker(
        id: String,
        builder: ColorPicker.ColorPickerBuilderScope.() -> Unit = {}
    ): SettingToken<ColorPicker> =
        ColorPicker.Builder(id, builder).create().register()

    fun createContentChoice(
        id: String,
        builder: ContentChoice.ChoiceContentBuilderScope.() -> Unit
    ): SettingToken<ContentChoice> =
        ContentChoice.Builder(id, builder).create().register()

    fun createCounter(
        id: String,
        builder: Counter.CounterBuilderScope.() -> Unit = {}
    ): SettingToken<Counter> =
        Counter.Builder(id, builder).create().register()

    fun <T : Any> createCustomSetting(
        id: String,
        builder: Custom.CustomBuilderScope<T>.() -> ContentConfiguredToken
    ): SettingToken<Custom<T>> =
        Custom.Builder(id, builder).create().register()

    fun createInfo(
        id: String,
        builder: Info.InfoBuilderScope.() -> Unit = {}
    ): SettingToken<Info> =
        Info.Builder(id, builder).create().register()

    fun createRedirect(
        id: String,
        builder: Redirect.RedirectBuilderScope.() -> Unit
    ): SettingToken<Redirect> =
        Redirect.Builder(id, builder).create().register()

    fun createSelect(
        id: String,
        builder: Select.SelectBuilderScope.() -> Unit
    ): SettingToken<Select> =
        Select.Builder(id, builder).create().register()

    fun createSlider(
        id: String,
        builder: Slider.SliderBuilderScope.() -> Unit
    ): SettingToken<Slider> =
        Slider.Builder(id, builder).create().register()

    fun createTextField(
        id: String,
        builder: TextField.TextFieldBuilderScope.() -> Unit
    ): SettingToken<TextField> =
        TextField.Builder(id, builder).create().register()

    fun <T : ComposeSetting<*>> T.register(): SettingToken<T>
}

