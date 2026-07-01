package com.daniil.csb

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.daniil.csb.classes.ComposeSetting
import com.daniil.csb.screens.ScreenAttribute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object CSB {
    private var _applicationContext: Context? = null
    private val context: Context get() = _applicationContext ?: error("CSB is not initialized. Ensure CSBInitializer is in your Manifest or call CSB.init(context).")

    private val globalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val navigationModel: SettingsNavigationModel by lazy {
        SettingsNavigationModel()
    }
    internal fun init(context: Context) {
        if (_applicationContext != null) return
        val app = context.applicationContext as Application
        _applicationContext = app

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                globalScope.launch { saveData() }
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
    private var PATH_DIRECTION = "csb"
    fun findSettingById(id: String): ComposeSetting<*> {
        return navigationModel.findSettingById(id)
    }
    fun getAllSettings(): List<ComposeSetting<*>> {
        return navigationModel.screenHeap.value.flatMap { it.settings.values.flatten() }
    }

    inline fun <reified T> getValue(id: String): StateFlow<T> {
        val setting = findSettingById(id)
        if (setting.value.value is T) {
            @Suppress("UNCHECKED_CAST")
            return setting.value as StateFlow<T>
        }
        error("Type mismatch")
    }

    inline fun <reified T> setValue(id: String, newValue: T) {
        val setting = findSettingById(id)

        @Suppress("UNCHECKED_CAST")
        val target = setting as? ComposeSetting<T>
        target?.changeValue(newValue) ?: error("Type mismatch $id")
    }

    fun enable(id: String, state: Boolean) {
        val setting = findSettingById(id)
        setting.enabled(state)
    }

    fun resetToDefault(id: String) {
        val setting = findSettingById(id)
        setting.resetToDefault()
    }
    fun resetAllToDefault() {
        getAllSettings().forEach { it.resetToDefault() }
    }

    fun storedMode(id: String, state: Boolean) {
        val setting = findSettingById(id)
        if (state) setting.saveOn() else setting.saveOff()
    }

    fun navigateToSetting(id: String) {
        navigationModel.navigateToSetting(id)
    }
    fun navigateToGroup(groupId: String) {
        navigationModel.navigateToGroup(groupId)
    }
    fun navigateToScreen(screenId: String) {
        navigationModel.goToScreen(screenId)
    }

    fun hideGroup(screenId: String, groupId: String, isHide: Boolean) {
        navigationModel.hideGroup(screenId, groupId, isHide)
    }
    fun hideGroup(groupId: String, isHide: Boolean) {
        val screenId = CSB.navigationModel.currentScreen.value?.id ?: error("Screen not yet initialized")
        navigationModel.hideGroup(screenId, groupId, isHide)
    }
    fun disableGroup(screenId: String, groupId: String, isDisable: Boolean) {
        navigationModel.disableGroup(screenId, groupId, isDisable)
    }
    fun disableGroup(groupId: String, isDisable: Boolean) {
        val screenId = CSB.navigationModel.currentScreen.value?.id ?: error("Screen not yet initialized")
        navigationModel.disableGroup(screenId, groupId, isDisable)
    }

    internal suspend fun loadData() = withContext(Dispatchers.IO) {
        val patch = File(context.filesDir, PATH_DIRECTION)
        if (!patch.exists()) {
            patch.mkdir()
        }

        navigationModel.screenHeap.value.forEach { screenInstance ->

            val file = File(patch, "csb_${screenInstance.id}")
            if (!file.exists()) return@forEach
            val json = file.bufferedReader().use { it.readText() }
            val packages = try {
                Json.decodeFromString<List<SaveSettingPackage>>(json)
            } catch (_: Exception) {
                Log.d("CSB", "Error load settings")
                emptyList()
            }

            packages.forEach { pack ->
                try {
                    val setting = findSettingById(pack.id)
                    setting.loadLogic(pack)
                } catch (_: Exception) {}
            }
        }
    }

    internal suspend fun saveData() = withContext(Dispatchers.IO) {
        val patch = File(context.filesDir, PATH_DIRECTION)
        if (!patch.exists()) {
            patch.mkdir()
        }
        for (screen in navigationModel.screenHeap.value) {
            if (screen.attribute?.contains(ScreenAttribute.Unstored) == true) continue
            val file = File(patch, "csb_${screen.id}")
            if (!file.exists()) {
                file.createNewFile()
            }

            val jsonPackageList: List<SaveSettingPackage> = screen.settings.values
                .flatten()
                .mapNotNull { it.saveLogic() }

            if (jsonPackageList.isNotEmpty()) {
                val json = Json.encodeToString(jsonPackageList)
                file.writeText(json)
            }
        }
    }
}