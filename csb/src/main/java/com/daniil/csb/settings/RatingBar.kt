package com.daniil.csb.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.R
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.ComposeSettingInterface
import com.daniil.csb.settings.utils.GroupItemClip
import com.daniil.csb.settings.utils.SettingDefaultScope
import com.daniil.csb.settings.utils.SettingDslInterface
import com.daniil.csb.settings.utils.SettingToken
import com.daniil.csb.settingui.DefaultContainer
import com.daniil.csb.settingui.DefaultSettingUI
import com.daniil.csb.settingui.LocalSettingsStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RatingBar internal constructor(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val defaultValue: Int,
    val stars: Int,
    val size: Dp = 32.dp,
    enabled: Boolean,
    val ratingItem: (@Composable (isActive: Boolean, number: Int) -> Unit)?,
    override var isSaveSetting: Boolean = true,
    override val onChangeValue: (Int) -> Unit,
    override val customGrouping: GroupItemClip? = null
) : ComposeSetting<Int>() {
    private val _value = MutableStateFlow(defaultValue)
    override val value = _value.asStateFlow()

    private val _enabled = MutableStateFlow(enabled)
    override val enabled = _enabled.asStateFlow()

    override fun enabled(state: Boolean) {
        _enabled.value = state
    }

    override fun changeValue(newValue: Int) {
        _value.value = newValue.coerceIn(0, stars)
        onChangeValue(newValue.coerceIn(0, stars))
    }

    @CsbDslMarkers
    class RatingBarBuilderScope : SettingDefaultScope() {
        var stars = 10
        var defaultValue = 0
        var title: String? = null
        var description: String? = null
        var size: Dp = 32.dp
        val ratingItem: (@Composable (isActive: Boolean, number: Int) -> Unit)? = null
        var onChangeValue: (Int) -> Unit = {}
    }

    companion object : ComposeSettingInterface.Factory<RatingBar, RatingBarBuilderScope> {
        override fun SettingDslInterface.create(
            id: String,
            scope: RatingBarBuilderScope.() -> Unit
        ): SettingToken<RatingBar> {
            val data = RatingBarBuilderScope().apply(scope)
            return with(data) {
                RatingBar(
                    id = id,
                    title = title ?: id,
                    description = description,
                    defaultValue = defaultValue,
                    stars = stars,
                    enabled = enabled,
                    size = size,
                    isSaveSetting = isSaveSetting,
                    ratingItem = ratingItem,
                    onChangeValue = onChangeValue,
                    customGrouping = customGrouping
                ).register()
            }
        }

    }


    override val focusState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @Composable
    override fun UI(
        modifier: Modifier,
        position: GroupItemClip?
    ) {
        val style = LocalSettingsStyle.current
        val enabled by this.enabled.collectAsState()
        val focusState by this.focusState.collectAsState()
        val value by this.value.collectAsState()
        DefaultContainer(
            modifier = modifier,
            isFocused = focusState,
            enabled = enabled,
            groupItemClip = position,
            paddingValues =
                PaddingValues(
                    horizontal = style.horizontalPadding,
                    vertical = style.verticalPadding
                ),
            onClick = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = style.minHeight)
            ) {

                if (!title.isBlank()) Text(text = CSB.translator(title), style = style.titleStyle)

                description?.let { Text(text = CSB.translator(it), style = style.descriptionStyle) }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(stars) {
                        val isActive = it + 1 <= value
                        val activeTint = Color.Yellow
                        val unactiveTint = LocalContentColor.current
                        val animateColor by animateColorAsState(
                            if (isActive) activeTint else unactiveTint
                        )
                        IconButton(
                            modifier = Modifier.size(size),
                            onClick = {
                                if (it + 1 == value) {
                                    changeValue(it)
                                } else {
                                    changeValue(it + 1)
                                }
                            }
                        ) {
                            if (ratingItem == null) {
                                when (isActive) {
                                    true -> {
                                        Icon(
                                            modifier = Modifier.size(size / 1.5f),
                                            painter = painterResource(R.drawable.star_filled),
                                            contentDescription = "Active point",
                                            tint = animateColor
                                        )
                                    }

                                    false -> {
                                        Icon(
                                            modifier = Modifier.size(size / 1.5f),
                                            painter = painterResource(R.drawable.star),
                                            contentDescription = "Unactive point",
                                            tint = animateColor
                                        )
                                    }
                                }
                            } else {
                                ratingItem(isActive, it + 1)
                            }
                        }
                    }
                }
            }
        }


    }

}