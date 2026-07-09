package com.daniil.csb

import kotlinx.serialization.Serializable

@Serializable
data class CSBConfig(
    val savePatch: String,
    val configFlags: ArrayList<String>,
    val primaryScreenId: String?
)
internal fun String.isInFlag(): Boolean = this in CSB.getConfigFlags()

internal val allFlags = setOf(
    // [flag:] Set specific / experimental parameter
    "ignoreSettingNotFoundError",
    "allowDisplayAbstractScreen",
    "disableContainerGroupRound",
    "enableDebugMode",
    "useExperimentalApi", // TODO
    "enableSwitchWithNavigate", // TODO
    "enablePredictiveBackHandler", // TODO
    "disableScroll",
    "useOneFileJsonSaveMethod", // TODO
    "useJsonSaveMethod", // TODO
    "disableStored",
)