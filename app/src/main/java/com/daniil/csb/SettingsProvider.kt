package com.daniil.csb

import android.content.Context
import com.daniil.csb.classes.SettingsSealed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object SettingsProvider {
    private var PATH_DIRECTION = "csb"
    lateinit var navigationModel: SettingsNavigationModel
    fun innit(model: SettingsNavigationModel) {
        navigationModel = model
        PATH_DIRECTION = model.config.storageDirection
    }

    fun findById(id: String): SettingsSealed<*> {
        val setting = navigationModel.screenHeap.value
            .flatMap { it.settings.values }.flatten()
            .find { it.id == id } ?: error("Setting $id not found")
        return setting
    }

    inline fun <reified T> getValue(id: String): StateFlow<T> {
        val setting = findById(id)
        if (setting.value.value is T) {
            @Suppress("UNCHECKED_CAST")
            return setting.value as StateFlow<T>
        }
        error("Type mismatch")
    }

    inline fun <reified T> setValue(id: String, newValue: T) {
        val setting = findById(id)

        @Suppress("UNCHECKED_CAST")
        val target = setting as? SettingsSealed<T>
        target?.changeValue(newValue) ?: error("Type mismatch $id")
    }

    fun enable(id: String, state: Boolean) {
        val setting = findById(id)
        setting.enabled(state)
    }

    suspend fun loadData(context: Context) = withContext(Dispatchers.IO) {
        val patch = File(context.filesDir, PATH_DIRECTION)
        if (!patch.exists()) {
            patch.mkdir()
        }

        navigationModel.screenHeap.value.forEach { screenInstance ->

            val file = File(patch, "csb_${screenInstance.id}")
            if (!file.exists()) return@forEach
            val json = file.bufferedReader().use { it.readText() }
            val packages = Json.decodeFromString<List<SaveSettingPackage>>(json)
            packages.forEach { pack ->
                try {
                    val setting = findById(pack.id)
                    setting.loadLogic(pack)
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun saveData(context: Context) = withContext(Dispatchers.IO) {
        val patch = File(context.filesDir, PATH_DIRECTION)
        if (!patch.exists()) {
            patch.mkdir()
        }

        navigationModel.screenHeap.value.forEach { screenInstance ->
            val file = File(patch, "csb_${screenInstance.id}")
            if (!file.exists()) {
                file.createNewFile()
            }

            val jsonPackageList: List<SaveSettingPackage> = screenInstance.settings.values
                .flatten()
                .mapNotNull { it.saveLogic() }

            if (jsonPackageList.isNotEmpty()) {
                val json = Json.encodeToString(jsonPackageList)
                file.writeText(json)
            }
        }
    }

}