package com.daniil.csb

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.screens.ScreenBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

object CSB {
    lateinit var config: CSBConfig
    private var _applicationContext: Context? = null
    private val context: Context
        get() = _applicationContext
            ?: error("CSB is not initialized. Ensure CSBInitializer is in your Manifest or call CSB.init(context).")

    private val globalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val navigationModel: SettingsNavigationModel by lazy {
        SettingsNavigationModel()
    }
    lateinit var executeArray: ArrayList<String>

    internal fun executeConfigAction() {
        fun execute(expression: String) {
            val ignoreFlag = expression.substringAfter("@") == "IgnoreFlags"
            val oldEnableIgnore = isIgnoreFlags
            if (ignoreFlag) isIgnoreFlags = true
            try {
                val pureExpression = if (ignoreFlag) expression.substringBeforeLast("@") else expression
                val action = pureExpression.substringBefore('(')
                val param = if (pureExpression.contains('(') && pureExpression.contains(')')) {
                    pureExpression.substring(pureExpression.indexOf('(') + 1, pureExpression.indexOf(')'))
                        .split(',').map { it.trim() }
                } else emptyList()
                try {
                    when (action) {
                        "setValue" -> {
                            val setting = findSettingById(param[0]).getOrNull() ?: return
                            val value = param.getOrElse(2) {
                                setValue(setting.id, param[1])
                                return
                            }
                            when (param[1]) {
                                "Float", "float" -> setValue(setting.id, value.toFloat())
                                "Long", "long" -> setValue(setting.id, value.toLong())
                                "Boolean", "bool" -> setValue(setting.id, value.toBoolean())
                                "Int", "int" -> setValue(setting.id, value.toInt())
                                else -> setValue(setting.id, value)
                            }
                        }
                        "saveData" -> navigationModel.viewModelScope.launch { saveData() }
                        "loadData" -> suspendLoadData()
                        "resetToDefault" -> resetToDefault(param[0])
                        "resetAllToDefaults" -> resetAllSettingsToDefault()
                        "storedMode" -> storedMode(param[0], param[1].toBoolean())
                        "navigateToSetting" -> navigateToSetting(param[0])
                        "enable" -> enable(param[0], param[1].toBoolean())
                        "navigateToScreen" -> navigateToScreen(param[0])
                        "navigateToGroup" -> navigateToGroup(param[0])
                        "disableGroup" -> disableGroup(param[0], param[1].toBoolean())
                        "hideGroup" -> hideGroup(param[0], param[1].toBoolean())
                        else -> error("Action \"$action\" not found")
                    }
                } catch (_: IndexOutOfBoundsException) {
                }
            } finally {
                isIgnoreFlags = oldEnableIgnore
            }
        }

        executeArray.forEach { execute(it) }
    }

    private var isIgnoreFlags = false

    context(_: ScreenBuilder)
    fun config(scope: CSBConfigureScope.() -> Unit) {
        val data = CSBConfigureScope().apply(scope)
        this.config = data.createCSBConfig()
        this.executeArray = data.executeArray
    }

    context(_: ScreenBuilder)
    fun config(config: CSBConfig) {
        this.config = config
        this.executeArray
    }

    internal fun getConfigFlags(): ArrayList<String> =
        if (isIgnoreFlags) arrayListOf() else config.configFlags

    class CSBConfigureScope internal constructor() {
        var savePatch = DEFAULT_PATH
        var primaryScreenId: String? = null
        private var configFlags: ArrayList<String>? = arrayListOf()
        internal val executeArray = arrayListOf<String>()
        fun flag(flag: String) {
            if (configFlags == null) return
            if (flag in allFlags) {
                if (flag in configFlags!!) return
                configFlags!!.add(flag)
            } else error("Flag \"${flag}\" not found")
        }

        fun execute(expression: String) = executeArray.add(expression)
        fun execute(action: String, vararg param: String) {
            executeArray.add("$action(${param.joinToString(",")})")
        }


        operator fun String.unaryPlus() {
            val expression = this.substringAfter(':')
            when (val prefix = this.substringBefore(':')) {
                "f", "flag" -> flag(expression)
                "var", "variable" -> {
                    val varName = this.substring(this.indexOf(':') + 1, this.indexOf('=')).trim()
                    val value = expression.substringAfter('=').trim()
                    val result = changeVariable(varName, value)
                    if (!result) error("Not found variable $varName")
                }

                "e", "exec", "execute" -> execute(expression)
                else -> error("Not found command prefix: \"$prefix\" in \"$this\" config command")
            }
        }

        private fun changeVariable(name: String, value: String): Boolean {
            when (name) {
                "IgnoreFlags" -> isIgnoreFlags = value.toBoolean()
                "savePatch" -> savePatch = value
                "primaryScreenId" -> primaryScreenId = value
                "configFlags" -> when {
                    value == "null" -> configFlags = null
                    value in arrayOf("empty", "Unit", "[]") -> configFlags?.clear()
                    value.startsWith('[') && value.endsWith(']') -> {
                        val flags = value
                            .replace("[", "")
                            .replace("]", "")
                            .split(',')
                            .filterNot { it.isBlank() }
                        flags.forEach { flag(it) }
                    }
                }

                else -> return false
            }
            return true
        }

        internal fun createCSBConfig(): CSBConfig {
            val build = CSBConfig(
                savePatch = savePatch,
                configFlags = configFlags ?: arrayListOf(),
                primaryScreenId = primaryScreenId
            )
            return build
        }
    }

    private fun defaultConfig() = with(ScreenBuilder()) {
        config {
            +"var:IgnoreFlags=false"
            // Default param
        }
    }

    internal fun init(context: Context) {

        defaultConfig()
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

    private const val DEFAULT_PATH = "csb"
    fun findSettingById(id: String): Result<ComposeSetting<*>> {
        return try {
            Result.success(navigationModel.findSettingById(id))
        } catch (e: Exception) {
            if ("ignoreSettingNotFoundError".isInFlag()) {
                Result.failure(e)
            } else throw e
        }
    }

    fun getAllSettings(): List<ComposeSetting<*>> {
        return navigationModel.screenHeap.value.flatMap { it.settings.values.flatten() }
    }

    inline fun <reified T> getValue(id: String): StateFlow<T> {
        val result = findSettingById(id)
        if (result.isFailure) {
            @Suppress("UNCHECKED_CAST")
            val nullable: T = try {
                null as T
            } catch (_: Throwable) {
                val errorMassage = """
                        Type "${T::class.qualifiedName}" not cast to null.
                        Setting "$id" not found but enabled "ignoreSettingNotFoundError" flag is set so return type is null.
                        But the requested is type: "${T::class.simpleName}"
                        Set the returned type to nullable: "${T::class.simpleName}?".
                    """.trimIndent()
                error(errorMassage)
            }
            return MutableStateFlow(nullable)
        }
        val setting = result.getOrThrow()
        if (setting.value.value is T || (setting.value.value == null && null is T)) {
            @Suppress("UNCHECKED_CAST")
            return setting.value as StateFlow<T>
        }
        error("Type mismatch for $id")
    }

    inline fun <reified T> setValue(id: String, newValue: T) {
        val setting = findSettingById(id).getOrNull() ?: return

        @Suppress("UNCHECKED_CAST")
        val target = setting as? ComposeSetting<T>
        target?.changeValue(newValue) ?: error("Type mismatch $id")
    }

    fun enable(id: String, state: Boolean) {
        val setting = findSettingById(id).getOrNull() ?: return
        setting.enabled(state)
    }

    fun resetToDefault(id: String) {
        val setting = findSettingById(id).getOrNull() ?: return
        setting.resetToDefault()
    }

    fun resetAllSettingsToDefault() {
        getAllSettings().forEach { it.resetToDefault() }
    }

    fun storedMode(id: String, state: Boolean) {
        val setting = findSettingById(id).getOrNull() ?: return
        if (state) setting.saveOn() else setting.saveOff()
    }

    fun navigateToSetting(id: String) {
        try {
            navigationModel.navigateToSetting(id)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun navigateToGroup(groupId: String) {
        try {
            navigationModel.navigateToGroup(groupId)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun navigateToScreen(screenId: String) {
        try {
            navigationModel.goToScreen(screenId)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun hideGroup(screenId: String, groupId: String, isHide: Boolean) {
        try {
            navigationModel.hideGroup(screenId, groupId, isHide)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun hideGroup(groupId: String, isHide: Boolean) {
        try {
            val screenId =
                CSB.navigationModel.currentScreen.value?.id ?: error("Screen not yet initialized")
            navigationModel.hideGroup(screenId, groupId, isHide)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun disableGroup(screenId: String, groupId: String, isDisable: Boolean) {
        try {
            navigationModel.disableGroup(screenId, groupId, isDisable)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    fun disableGroup(groupId: String, isDisable: Boolean) {
        try {
            val screenId =
                CSB.navigationModel.currentScreen.value?.id ?: error("Screen not yet initialized")
            navigationModel.disableGroup(screenId, groupId, isDisable)
        } catch (e: Exception) {
            if (!"ignoreSettingNotFoundError".isInFlag()) throw e
        }
    }

    internal fun suspendLoadData() {
        navigationModel.viewModelScope.launch { loadData() }
    }

    internal suspend fun loadData() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = File(context.filesDir, config.savePatch)
        if (!patch.exists()) {
            patch.mkdir()
        }
        navigationModel.screenHeap.value.forEach { screenInstance ->

            val file = File(patch, "csb_${screenInstance.id}")
            if (!file.exists()) return@forEach
            val json = file.bufferedReader().use { it.readText() }
            val packages = try {
                Json.decodeFromString<List<SaveSettingPackage?>>(json)
            } catch (_: Exception) {
                Log.d("CSB", "Error load settings")
                emptyList()
            }

            packages.filterNotNull().forEach { pack ->
                try {
                    val setting = findSettingById(pack.id).getOrNull() ?: return@forEach
                    setting.loadLogic(pack)
                } catch (_: Exception) {
                }
            }
        }
    }

    internal suspend fun saveData() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = File(context.filesDir, config.savePatch)
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