package com.daniil.csb

import kotlinx.serialization.Serializable


@Serializable
sealed class SaveSettingPackage {
    abstract val id: String
    abstract val enable: Boolean
    abstract val value: Any
    // Default
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
    ) : SaveSettingPackage()
    // List
    @Serializable
    data class StringListPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: List<String>
    ) : SaveSettingPackage()

    data class IntListPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: List<Int>
    ) : SaveSettingPackage()


    // Other
    @Serializable
    data class UnitPackage(
        override val id: String,
        override val enable: Boolean,
        override val value: Unit = Unit
    ) : SaveSettingPackage()


}

