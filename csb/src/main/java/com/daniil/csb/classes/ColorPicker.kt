package com.daniil.csb.classes


import android.content.ClipData
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.daniil.csb.utils.FancyTabBar
import com.daniil.csb.utils.FancyTabBarData
import com.daniil.csb.R
import com.daniil.csb.SaveSettingPackage
import com.daniil.csb.classes.utils.SettingBuilder
import com.daniil.csb.classes.utils.GroupItemClip
import com.daniil.csb.settingui.DefaultSettingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor

class ColorPicker(
    override var id: String,
    val defaultValue: Color,
    override val title: String,
    override val description: String?,
    enabled: Boolean = true,
    var onChangeValue: (Color) -> Unit = {},
    override var isSaveSetting: Boolean = true
) : ComposeSetting<Color>() {
    constructor(
        id: String,
        defaultValueInt: Int,
        title: String,
        description: String?,
        enabled: Boolean = true,
        onChangeValue: (Color) -> Unit = {},
        isSaveSetting: Boolean = true
    ) : this(id, defaultValue = Color(defaultValueInt), title, description, enabled, onChangeValue, isSaveSetting)


    private var _value = MutableStateFlow(this@ColorPicker.defaultValue)
    override val value = _value.asStateFlow()

    private var _enable = MutableStateFlow(enabled)
    override val enabled = _enable.asStateFlow()

    override fun enabled(state: Boolean) {
        _enable.value = state
    }

    override fun changeValue(newValue: Color) {
        onChangeValue(newValue)
        _value.value = newValue
    }

    override fun resetToDefault() { changeValue(this@ColorPicker.defaultValue) }

    override fun saveLogic(): SaveSettingPackage? {
        if (!isSaveSetting) return null
        return SaveSettingPackage.IntPackage(
            id = id,
            enable = enabled.value,
            value = value.value.toArgb()
        )
    }

    override fun loadLogic(pack: SaveSettingPackage?) {
        if (pack == null) return
        val data = pack.value as Int
        changeValue(Color(data))
    }

    class ColorPickerBuilderScope() {
        var defaultValue: Color? = null
        var defaultValueInt: Int? = null
        var onChangeValue: (Color) -> Unit = {}
        var title: String? = null
        var description: String? = null
        var enabled = true
        var isSaveSetting = true
    }

    class Builder(
        val id: String,
        builderScope: ColorPickerBuilderScope.() -> Unit = {}
    ) {
        val scope = ColorPickerBuilderScope().apply(builderScope)
        fun create(): ColorPicker = with(scope) {
            val res = when {
                defaultValueInt != null -> ColorPicker(
                    id,
                    defaultValueInt!!,
                    title ?: id,
                    description,
                    enabled,
                    onChangeValue,
                    isSaveSetting
                )

                defaultValue != null -> ColorPicker(
                    id,
                    defaultValue!!,
                    title ?: id,
                    description,
                    enabled,
                    onChangeValue,
                    isSaveSetting
                )

                else -> error("Not found redirect parameter")
            }
            return res
        }
    }

    override val focusState = MutableStateFlow(false)
    @Composable
    override fun UI(modifier: Modifier, position: GroupItemClip?) {
        val focusState by this.focusState.collectAsState()
        val enabled by this.enabled.collectAsState()
        val value by this.value.collectAsState()
        var alertOpen by retain { mutableStateOf(false) }

        DefaultSettingUI(
            modifier = modifier,
            isFocused = focusState,
            groupItemClip = position,
            enabled = enabled,
            title = { if (!title.isBlank()) Text(title) },
            description = { description?.let { Text(it) } },
            display = {
                FilledIconButton(
                    enabled = enabled,
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(containerColor = value),
                    onClick = {
                        alertOpen = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.palette_icon),
                        contentDescription = "Open picker"
                    )
                }
            },
            onClick = { alertOpen = true }
        )

        if (alertOpen) {
            ColorPickerDialog(
                initialColor = value,
                onColorSelected = {
                    changeValue(it)
                    alertOpen = false
                },
                onDismissRequest = {
                    alertOpen = false
                }
            )
        }
    }
}


private fun hsvToColor(h: Float, s: Float, v: Float): Color {
    val hsv = floatArrayOf(
        h.coerceIn(0f, 360f),
        s.coerceIn(0f, 1f),
        v.coerceIn(0f, 1f)
    )
    return Color(AndroidColor.HSVToColor(hsv))
}

private enum class PickerMode() {
    Choice,
    Editor
}

@Composable
private fun ColorPickerDialog(
    initialColor: Color = Color.Red,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    var pickerMode by remember {
        mutableStateOf(PickerMode.Choice)
    }

    var color by remember {
        mutableStateOf(initialColor)
    }

    var hue by remember {
        mutableFloatStateOf(0f)
    }

    var satLight by remember {
        mutableFloatStateOf(0.5f)
    }

    var rText by remember { mutableStateOf("") }
    var gText by remember { mutableStateOf("") }
    var bText by remember { mutableStateOf("") }

    fun updateRgbTexts(color: Color) {
        rText = (color.red * 255).toInt().toString()
        gText = (color.green * 255).toInt().toString()
        bText = (color.blue * 255).toInt().toString()
    }

    fun updateFromHSV(newHue: Float, newSatLight: Float) {
        hue = newHue
        satLight = newSatLight

        color =
            if (newSatLight < 0.5f) {
                val t = newSatLight / 0.5f
                lerp(
                    Color.White,
                    hsvToColor(newHue, 1f, 1f),
                    t
                )
            } else {
                val t = (newSatLight - 0.5f) / 0.5f
                lerp(
                    hsvToColor(newHue, 1f, 1f),
                    Color.Black,
                    t
                )
            }

        updateRgbTexts(color)
    }

    fun updateFromRGB(
        redText: String,
        greenText: String,
        blueText: String
    ) {
        val red = redText.toIntOrNull()?.coerceIn(0, 255) ?: 0
        val green = greenText.toIntOrNull()?.coerceIn(0, 255) ?: 0
        val blue = blueText.toIntOrNull()?.coerceIn(0, 255) ?: 0

        color = Color(
            red = red / 255f,
            green = green / 255f,
            blue = blue / 255f
        )

        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)

        hue = hsv[0]

        satLight =
            if (hsv[2] >= 1f) {
                hsv[1] * 0.5f
            } else {
                0.5f + ((1f - hsv[2]) * 0.5f)
            }
    }

    LaunchedEffect(Unit) {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(initialColor.toArgb(), hsv)

        hue = hsv[0]

        satLight =
            if (hsv[2] >= 1f) {
                hsv[1] * 0.5f
            } else {
                0.5f + ((1f - hsv[2]) * 0.5f)
            }

        updateRgbTexts(initialColor)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Select color")
        },
        text = {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .verticalScroll(rememberScrollState()),
            ) {

                FancyTabBar(
                    modifier = Modifier.fillMaxWidth(),
                    selectedIndex = pickerMode.ordinal,
                    entries = listOf(
                        FancyTabBarData(
                            id = PickerMode.Choice.name,
                            name = "HSV",
                            painterId = R.drawable.palette_icon
                        ),
                        FancyTabBarData(
                            id = PickerMode.Editor.name,
                            name = "RGB",
                            painterId = R.drawable.edit_icon
                        )
                    ),
                    onSelected = {
                        pickerMode = PickerMode.valueOf(it)
                    }
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            color,
                            MaterialTheme.shapes.medium
                        )
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    modifier = Modifier.padding(bottom = 4.dp),
                    targetState = pickerMode,
                    transitionSpec = {
                        if (pickerMode == PickerMode.Choice) {
                            slideInHorizontally(animationSpec = tween(300)) { it / -1 }
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { it / 1 })
                        } else {
                            slideInHorizontally(animationSpec = tween(300)) { it / 1 }
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { it / -1 })
                        }

                    },
                ) { mode ->

                    if (mode == PickerMode.Choice) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val clipboardManager = LocalClipboard.current

                            val hex by remember(color) {
                                mutableStateOf(
                                    color.toArgb().ushr(8).toUInt().toString(16).uppercase()
                                        .padStart(6, '0')
                                )
                            }
                            Text(
                                modifier = Modifier.clickable {
                                    coroutine.launch {
                                        val data = ClipData.newPlainText("color", "#$hex")
                                        clipboardManager.setClipEntry(ClipEntry(data))
                                    }
                                },
                                text = "HEX: #$hex",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CustomGradientSlider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(24.dp),
                                    value = hue / 360f,
                                    onValueChange = {
                                        updateFromHSV(
                                            newHue = it * 360f,
                                            newSatLight = satLight
                                        )
                                    },
                                    gradient = Brush.horizontalGradient(
                                        listOf(
                                            Color.Red,
                                            Color.Yellow,
                                            Color.Green,
                                            Color.Cyan,
                                            Color.Blue,
                                            Color.Magenta,
                                            Color.Red
                                        )
                                    )
                                )
                                CustomGradientSlider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(24.dp),
                                    value = satLight,
                                    onValueChange = {
                                        updateFromHSV(
                                            newHue = hue,
                                            newSatLight = it
                                        )
                                    },
                                    gradient = Brush.horizontalGradient(
                                        listOf(
                                            Color.White,
                                            hsvToColor(hue, 1f, 1f),
                                            Color.Black
                                        )
                                    )
                                )
                            }

                        }
                    } else {

                        @Composable
                        fun RowScope.RgbInput(
                            label: String,
                            value: String,
                            onChange: (String) -> Unit
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = value,
                                onValueChange = { input ->
                                    if (
                                        input.isEmpty() ||
                                        (
                                                input.all { it.isDigit() } &&
                                                        input.length <= 3
                                                )
                                    ) {
                                        onChange(input)
                                    }
                                },
                                singleLine = true,
                                label = {
                                    Text(label)
                                }
                            )
                        }

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            RgbInput(
                                label = "R",
                                value = rText
                            ) { newValue ->
                                rText = newValue
                                updateFromRGB(
                                    rText,
                                    gText,
                                    bText
                                )
                            }

                            RgbInput(
                                label = "G",
                                value = gText
                            ) { newValue ->
                                gText = newValue
                                updateFromRGB(
                                    rText,
                                    gText,
                                    bText
                                )
                            }

                            RgbInput(
                                label = "B",
                                value = bText
                            ) { newValue ->
                                bText = newValue
                                updateFromRGB(
                                    rText,
                                    gText,
                                    bText
                                )
                            }

                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(color)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CustomGradientSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    gradient: Brush,
    modifier: Modifier = Modifier,
    height: Dp = 28.dp,
    thumbRadius: Dp = 12.dp,
) {

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(gradient)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val x = change.position.x
                        val fraction = (x / size.width).coerceIn(0f, 1f)
                        onValueChange(fraction)
                    },
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val thumbX = constraints.maxWidth * value

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (thumbX - thumbRadius.toPx()).roundToInt(), 0
//                        (constraints.maxHeight / 2 - thumbRadius.toPx()).roundToInt()
                    )
                }
                .size(thumbRadius * 1.8f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
        )
    }
}

fun SettingBuilder.createColorPicker(
    id: String,
    builder: ColorPicker.ColorPickerBuilderScope.() -> Unit = { defaultValue = Color.Blue }
): ColorPicker {
    return ColorPicker.Builder(id, builder).create()
}