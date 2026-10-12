package com.daniil.csb.settings.settingcore

import androidx.compose.runtime.Composable
import com.daniil.csb.group.FragmentController
import com.daniil.csb.settings.Action
import com.daniil.csb.settings.CodePreview
import com.daniil.csb.settings.ColorPicker
import com.daniil.csb.settings.ComposableComponent
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

private const val deprecatedReason = "Recommended to change setting creation call"
interface SettingDslInterface {

//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Switch.create(id) { builder() }"))
    fun createSwitch(
        id: String,
        builder: Switch.SwitchBuilderScope.() -> Unit = { }
    ): SettingToken<Switch> =
        with(Switch) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "MultiplySelect.create(id) { builder() }"))
    fun createMultiplySelect(
        id: String,
        builder: MultiplySelect.MultiplySelectBuilderScope.() -> Unit = fun MultiplySelect.MultiplySelectBuilderScope.() {

        }
    ): SettingToken<MultiplySelect> =
        with(MultiplySelect) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "TimePicker.create(id) { builder() }"))
    fun createTimePicker(
        id: String,
        builder: TimePicker.TimePickerBuilderScope.() -> Unit = fun TimePicker.TimePickerBuilderScope.() {

        }
    ): SettingToken<TimePicker> =
        with(TimePicker) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "DatePicker.create(id) { builder() }"))
    fun createDatePicker(
        id: String,
        builder: DatePicker.DatePickerBuilderScope.() -> Unit = fun DatePicker.DatePickerBuilderScope.() {

        }
    ): SettingToken<DatePicker> =
        with(DatePicker) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Action.create(id) { builder() }"))
    fun createAction(
        id: String,
        builder: Action.ActionBuilderScope.() -> Unit = fun Action.ActionBuilderScope.() {

        }
    ): SettingToken<Action> = 
        with(Action) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "ColorPicker.create(id) { builder() }"))
    fun createColorPicker(
        id: String,
        builder: ColorPicker.ColorPickerBuilderScope.() -> Unit = fun ColorPicker.ColorPickerBuilderScope.() {

        }
    ): SettingToken<ColorPicker> =
        with(ColorPicker) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "ContentChoice.create(id) { builder() }"))
    fun createContentChoice(
        id: String,
        builder: ContentChoice.ChoiceContentBuilderScope.() -> ContentChoice.MoreThenZeroToken
    ): SettingToken<ContentChoice> =
        with(ContentChoice) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Counter.create(id) { builder() }"))
    fun createCounter(
        id: String,
        builder: Counter.CounterBuilderScope.() -> Unit = fun Counter.CounterBuilderScope.() {

        }
    ): SettingToken<Counter> =
        with(Counter) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Custom.create(id) { builder() }"))
    fun <T : Any> createCustomSetting(
        id: String,
        builder: Custom.CustomBuilderScope<T>.() -> Custom.SetContentToken
    ): SettingToken<Custom<T>> =
        with(Custom) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Info.create(id) { builder() }"))
    fun createInfo(
        id: String,
        builder: Info.InfoBuilderScope.() -> Unit = fun Info.InfoBuilderScope.() {

        }
    ): SettingToken<Info> =
        with(Info) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Redirect.create(id) { builder() }"))
    fun createRedirect(
        id: String,
        builder: Redirect.RedirectBuilderScope.() -> Redirect.InitRedirectToken = { setEmptyRedirect() }
    ): SettingToken<Redirect> =
        with(Redirect) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Select.create(id) { builder() }"))
    fun createSelect(
        id: String,
        builder: Select.SelectBuilderScope.() -> Select.MoreThenZeroOptionToken
    ): SettingToken<Select> =
        with(Select) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "Slider.create(id) { builder() }"))
    fun createSlider(
        id: String,
        builder: Slider.SliderBuilderScope.() -> Unit = fun Slider.SliderBuilderScope.() {

        }
    ): SettingToken<Slider> =
        with(Slider) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "RangeSlider.create(id) { builder() }"))
    fun createRangeSlider(
        id: String,
        builder: RangeSlider.RangeSliderBuilderScope.() -> Unit = fun RangeSlider.RangeSliderBuilderScope.() {

        }
    ): SettingToken<RangeSlider> =
        with(RangeSlider) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "TextField.create(id) { builder() }"))
    fun createTextField(
        id: String,
        builder: TextField.TextFieldBuilderScope.() -> Unit = fun TextField.TextFieldBuilderScope.() {

        }
    ): SettingToken<TextField> =
        with(TextField) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "PasswordField.create(id) { builder() }"))
    fun createPasswordField(
        id: String,
        builder: PasswordField.PasswordFieldBuilderScope.() -> Unit = fun PasswordField.PasswordFieldBuilderScope.() {

        }
    ): SettingToken<PasswordField> =
        with(PasswordField) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "SearchField.create(id) { builder() }"))
    fun createSearchField(
        id: String,
        builder: SearchField.SearchFieldBuilderScope.() -> Unit = fun SearchField.SearchFieldBuilderScope.() {

        }
    ): SettingToken<SearchField> =
        with(SearchField) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "RatingBar.create(id) { builder() }"))
    fun createRatingBar(
        id: String,
        builder: RatingBar.RatingBarBuilderScope.() -> Unit = fun RatingBar.RatingBarBuilderScope.() {

        }
    ): SettingToken<RatingBar> =
        with(RatingBar) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "ProgressBar.create(id) { builder() }"))
    fun crateProgressBar(
        id: String,
        builder: ProgressBar.ProgressBarBuilderScope.() -> Unit = fun ProgressBar.ProgressBarBuilderScope.() {

        }
    ): SettingToken<ProgressBar> =
        with(ProgressBar) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "TabBar.create(id) { builder() }"))
    fun createTabBar(
        id: String,
        builder: TabBar.TabBarBuilderScope.() -> TabBar.TabBarConfiguredToken
    ): SettingToken<TabBar> =
        with(TabBar) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "TabBar.create(id, controller)"))
    fun createTabBar(
        id: String,
        controller: FragmentController
    ): SettingToken<TabBar> =
        with(TabBar) { create(id, controller) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "CodePreview.create(id) { builder() }"))
    fun createCodePreview(
        id: String,
        builder: CodePreview.CodePreviewBuilderScope.() -> Unit = fun CodePreview.CodePreviewBuilderScope.() {

        }
    ): SettingToken<CodePreview> =
        with(CodePreview) { create(id, builder) }
//    @Deprecated(deprecatedReason, replaceWith = ReplaceWith(expression = "FilePicker.create(id) { builder() }"))
    fun <I, O> createFilePicker(
        id: String,
        builder: FilePicker.FilePickerBuilderScope<I, O>.() -> FilePicker.InitContractToken
    ): SettingToken<FilePicker<I, O>> =
        with(FilePicker) { create(id, builder) }
    fun createUI(
        id: String? = null,
        config: ComposableComponent.ComposableComponentConfigScope.() -> Unit = {},
        content: @Composable ComposableComponent.ComposableComponentScope.() -> Unit
    ): SettingToken<ComposableComponent>
        = with(ComposableComponent) { create(id, config, content) }


    fun <T : ComposeSetting<*>> T.register(): SettingToken<T>
}
