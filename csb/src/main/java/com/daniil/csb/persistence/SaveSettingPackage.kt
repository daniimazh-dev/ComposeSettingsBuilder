package com.daniil.csb.persistence

import androidx.compose.ui.graphics.Color
import com.daniil.csb.utils.ColorSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed class SaveSettingPackage {
    abstract val id: String
    abstract val enable: Boolean
    abstract val value: Any
    // Primitive
    @Serializable
    data class BooleanPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Boolean
    ) : SaveSettingPackage()

    @Serializable
    data class StringPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: String
    ) : SaveSettingPackage()

    @Serializable
    data class CharPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Char
    ) : SaveSettingPackage()

    @Serializable
    data class FloatPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Float
    ) : SaveSettingPackage()

    @Serializable
    data class IntPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Int
    ): SaveSettingPackage()
    @Serializable

    data class LongPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Long
    ): SaveSettingPackage()

    @Serializable
    data class DoublePackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Double
    ): SaveSettingPackage()


    // tuple
    data class PairPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Pair<String, String>,
    ): SaveSettingPackage()

    @Serializable
    data class TriplePackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Triple<String, String, String>,
    ): SaveSettingPackage()

    // List
    @Serializable
    data class StringListPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: List<String>
    ) : SaveSettingPackage()

    @Serializable
    data class IntListPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: List<Int>
    ) : SaveSettingPackage()

    @Serializable
    data class FloatListPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: List<Float>
    ) : SaveSettingPackage()

    @Serializable
    data class JsonPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: String,
    ) : SaveSettingPackage()

    // Other
    @Serializable
    data class ColorPackage(
        override val id: String,
        override val enable: Boolean,
        @Serializable(with = ColorSerializer::class)
        override val value: Color,
    ) : SaveSettingPackage()

    @Serializable
    data class UnitPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Unit = Unit
    ) : SaveSettingPackage()
}