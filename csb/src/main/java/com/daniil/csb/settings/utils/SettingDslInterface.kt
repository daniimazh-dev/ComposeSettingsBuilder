package com.daniil.csb.settings.utils

import com.daniil.csb.group.FragmentController
import com.daniil.csb.settings.Action
import com.daniil.csb.settings.CodePreview
import com.daniil.csb.settings.ColorPicker
import com.daniil.csb.settings.ContentChoice
import com.daniil.csb.settings.Counter
import com.daniil.csb.settings.Custom
import com.daniil.csb.settings.DatePicker
import com.daniil.csb.settings.FilePicker
import com.daniil.csb.settings.Info
import com.daniil.csb.settings.MultiplySelect
import com.daniil.csb.settings.PasswordField
import com.daniil.csb.settings.ProgressBar
import com.daniil.csb.settings.RangeSlider
import com.daniil.csb.settings.RatingBar
import com.daniil.csb.settings.Redirect
import com.daniil.csb.settings.SearchField
import com.daniil.csb.settings.Select
import com.daniil.csb.settings.Slider
import com.daniil.csb.settings.Switch
import com.daniil.csb.settings.TabBar
import com.daniil.csb.settings.TextField
import com.daniil.csb.settings.TimePicker

interface SettingDslInterface {
    fun createSwitch(
        id: String,
        builder: Switch.SwitchBuilderScope.() -> Unit = { }
    ): SettingToken<Switch> =
        with(Switch) { create(id, builder) }

    fun createMultiplySelect(
        id: String,
        builder: MultiplySelect.MultiplySelectBuilderScope.() -> Unit = {}
    ): SettingToken<MultiplySelect> =
        with(MultiplySelect) { create(id, builder) }

    fun createTimePicker(
        id: String,
        builder: TimePicker.TimePickerBuilderScope.() -> Unit = {}
    ): SettingToken<TimePicker> =
        with(TimePicker) { create(id, builder) }

    fun createDatePicker(
        id: String,
        builder: DatePicker.DatePickerBuilderScope.() -> Unit = {}
    ): SettingToken<DatePicker> =
        with(DatePicker) { create(id, builder) }

    fun createAction(
        id: String,
        builder: Action.ActionBuilderScope.() -> Unit = {}
    ): SettingToken<Action> = 
        with(Action) { create(id, builder) }

    fun createColorPicker(
        id: String,
        builder: ColorPicker.ColorPickerBuilderScope.() -> Unit = {}
    ): SettingToken<ColorPicker> =
        with(ColorPicker) { create(id, builder) }

    fun createContentChoice(
        id: String,
        builder: ContentChoice.ChoiceContentBuilderScope.() -> ContentChoice.MoreThenZeroComponentToken
    ): SettingToken<ContentChoice> =
        with(ContentChoice) { create(id, builder) }

    fun createCounter(
        id: String,
        builder: Counter.CounterBuilderScope.() -> Unit = {}
    ): SettingToken<Counter> =
        with(Counter) { create(id, builder) }

    fun <T : Any> createCustomSetting(
        id: String,
        builder: Custom.CustomBuilderScope<T>.() -> Custom.SetContentToken
    ): SettingToken<Custom<T>> =
        with(Custom) { create(id, builder) }

    fun createInfo(
        id: String,
        builder: Info.InfoBuilderScope.() -> Unit = { }
    ): SettingToken<Info> =
        with(Info) { create(id, builder) }

    fun createRedirect(
        id: String,
        builder: Redirect.RedirectBuilderScope.() -> Redirect.InitRedirectToken = { setEmptyRedirect() }
    ): SettingToken<Redirect> =
        with(Redirect) { create(id, builder) }

    fun createSelect(
        id: String,
        builder: Select.SelectBuilderScope.() -> Select.MoreThenZeroOptionToken
    ): SettingToken<Select> =
        with(Select) { create(id, builder) }

    fun createSlider(
        id: String,
        builder: Slider.SliderBuilderScope.() -> Unit = {}
    ): SettingToken<Slider> =
        with(Slider) { create(id, builder) }

    fun createRangeSlider(
        id: String,
        builder: RangeSlider.RangeSliderBuilderScope.() -> Unit = {}
    ): SettingToken<RangeSlider> =
        with(RangeSlider) { create(id, builder) }

    fun createTextField(
        id: String,
        builder: TextField.TextFieldBuilderScope.() -> Unit = {}
    ): SettingToken<TextField> =
        with(TextField) { create(id, builder) }
    fun createPasswordField(
        id: String,
        builder: PasswordField.PasswordFieldBuilderScope.() -> Unit = {}
    ): SettingToken<PasswordField> =
        with(PasswordField) { create(id, builder) }

    fun createSearchField(
        id: String,
        builder: SearchField.SearchFieldBuilderScope.() -> Unit = {}
    ): SettingToken<SearchField> =
        with(SearchField) { create(id, builder) }
    fun createRatingBar(
        id: String,
        builder: RatingBar.RatingBarBuilderScope.() -> Unit = {}
    ): SettingToken<RatingBar> =
        with(RatingBar) { create(id, builder) }

    fun crateProgressBar(
        id: String,
        builder: ProgressBar.ProgressBarBuilderScope.() -> Unit = {}
    ): SettingToken<ProgressBar> =
        with(ProgressBar) { create(id, builder) }

    fun createTabBar(
        id: String,
        builder: TabBar.TabBarBuilderScope.() -> TabBar.TabBarConfiguredToken
    ): SettingToken<TabBar> =
        with(TabBar) { create(id, builder) }

    fun createTabBar(
        id: String,
        controller: FragmentController
    ): SettingToken<TabBar> =
        with(TabBar) { create(id, controller) }

    fun createCodePreview(
        id: String,
        builder: CodePreview.CodePreviewBuilderScope.() -> Unit = {}
    ): SettingToken<CodePreview> =
        with(CodePreview) { create(id, builder) }

    fun <I, O> createFilePicker(
        id: String,
        builder: FilePicker.FilePickerBuilderScope<I, O>.() -> FilePicker.InitContractToken
    ): SettingToken<FilePicker<I, O>> =
        with(FilePicker) { create(id, builder) }
    
    fun <T : ComposeSetting<*>> T.register(): SettingToken<T>
}
