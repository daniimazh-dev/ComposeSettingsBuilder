package com.daniil.csb

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.viewModelScope
import com.daniil.csb.local.SettingsSerializer
import com.daniil.csb.persistence.CSBStoredData
import com.daniil.csb.persistence.SaveSettingPackage
import com.daniil.csb.group.FragmentController
import com.daniil.csb.group.GroupController
import com.daniil.csb.screens.ScreenAttribute
import com.daniil.csb.screens.ScreenBuilder
import com.daniil.csb.screens.ScreenController
import com.daniil.csb.settings.utils.ComposeSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object CSB {
    private const val DEFAULT_PATH = "csb"
    private var isIgnoreFlags = false

    class DefaultCSBTranslator: CSBTranslator {
        @Composable
        override fun translate(key: String): String {
            val prefix = "res:"
            return if (key.startsWith(prefix)) {
                val resId = key.substringAfter(prefix).toIntOrNull()
                if (resId != null) stringResource(resId) else key
            } else key
        }
    }
    internal var translator: CSBTranslator = DefaultCSBTranslator()

    @Composable
    internal fun translator(input: String): String {
        return translator.translate(input)
    }

    class CSBConfigureScope internal constructor() {
        var savePatch = DEFAULT_PATH
        var primaryScreenId: String? = null
        var debugMode: Boolean = false

        var translator: CSBTranslator? = null

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
            this.translator?.let { this@CSB.translator = it }
            val build = CSBConfig(
                savePatch = savePatch,
                configFlags = configFlags ?: arrayListOf(),
                primaryScreenId = primaryScreenId,
                debugMode = debugMode
            )
            return build
        }
    }

    private fun defaultConfig(): CSBConfig = with(ScreenBuilder()) {
        val scope = CSBConfigureScope().apply {
            /* Default config */
        }
        return@with scope.createCSBConfig()
    }

    fun config(scope: CSBConfigureScope.() -> Unit) {
        val data = CSBConfigureScope().apply(scope)
        this.config = data.createCSBConfig()
        this.executeArray = data.executeArray
    }

    fun config(config: CSBConfig) {
        this.config = config
    }

    internal fun getConfigFlags(): ArrayList<String> =
        if (isIgnoreFlags) arrayListOf() else config.configFlags

    var config: CSBConfig = defaultConfig()
    private var _applicationContext: Context? = null
    private val context: Context
        get() = _applicationContext
            ?: error("CSB is not initialized. Ensure CSBInitializer is in your Manifest or call CSB.init(context).")

    private val Context.csbDataStore: DataStore<CSBStoredData> by dataStore(
        fileName = "csb_settings.json",
        serializer = SettingsSerializer
    )

    private val globalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val navigationModel: SettingsNavigationModel by lazy {
        SettingsNavigationModel()
    }
    var executeArray: ArrayList<String> = arrayListOf()
        private set

    fun executeNow(expression: String) {
        val ignoreFlag = expression.substringAfter("@") == "IgnoreFlags"
        val oldEnableIgnore = isIgnoreFlags
        if (ignoreFlag) isIgnoreFlags = true
        try {
            val pureExpression =
                if (ignoreFlag) expression.substringBeforeLast("@") else expression
            val action = pureExpression.substringBefore('(')
            val param = if (pureExpression.contains('(') && pureExpression.contains(')')) {
                pureExpression.substring(
                    pureExpression.indexOf('(') + 1,
                    pureExpression.indexOf(')')
                )
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
                    "saveData" -> navigationModel.viewModelScope.launch { save() }
                    "loadData" -> load()
                    "resetToDefault" -> resetToDefault(param[0])
                    "resetAllToDefaults" -> resetAllSettingsToDefault()
                    "storedMode" -> storedMode(param[0], param[1].toBoolean())
                    "navigateToSetting" -> navigateToSetting(param[0])
                    "enable" -> enable(param[0], param[1].toBoolean())
                    "navigateToScreen" -> navigateToScreen(param[0])
                    "navigateToGroup" -> navigateToGroup(param[0])
                    "disableGroup" -> groupController(param[0]).isDisable(param[1].toBoolean())
                    "hideGroup" -> groupController(param[0]).isShow(!param[1].toBoolean())
                    else -> error("Action \"$action\" not found")
                }
            } catch (_: IndexOutOfBoundsException) {
            }
        } finally {
            isIgnoreFlags = oldEnableIgnore
        }
    }
    internal fun executeConfigAction() {
        executeArray.forEach { executeNow(it) }
    }


    internal fun init(context: Context) {
        if (_applicationContext != null) return
        val app = context.applicationContext as? Application
        _applicationContext = app ?: context.applicationContext ?: context

        app?.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                globalScope.launch { save() }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


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
        return navigationModel.screenHeap.value.flatMap { it.settings.flatMap { it.settings } }
    }

    inline fun <reified T> getValue(id: String): StateFlow<T> {
        val result = findSettingById(id)
        if (result.isFailure) {
            @Suppress("UNCHECKED_CAST")
            val nullable: T = try {
                null as T
            } catch (_: Throwable) {
                val errorMessage = """
                        Type "${T::class.qualifiedName}" not cast to null.
                        Setting "$id" not found but enabled "ignoreSettingNotFoundError" flag is set so return type is null.
                        But the requested is type: "${T::class.simpleName}"
                        Set the returned type to nullable: "${T::class.simpleName}?".
                    """.trimIndent()
                error(errorMessage)
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


    fun groupController(id: String): GroupController {
        return navigationModel.groupController(id)
    }

    fun fragmentController(id: String): FragmentController {
        return navigationModel.fragmentController(id)
    }

    fun screenController(id: String): ScreenController {
        return navigationModel.screenController(id)
    }


    internal fun load() = with(navigationModel.viewModelScope) {
        if ("disableStored".isInFlag()) return@with
        launch {
            when {
                "useJsonSaveMethod".isInFlag() ->
                    if ("useOneFileJsonSaveMethod".isInFlag()) loadWithJsonOneFile() else loadDataWithJson()
                else -> loadWithDataStore()
            }
        }
    }

    internal fun save() = with(navigationModel.viewModelScope) {
        if ("disableStored".isInFlag()) return@with
        launch {
            when {
                "useJsonSaveMethod".isInFlag() ->
                    if ("useOneFileJsonSaveMethod".isInFlag()) saveWithJsonOneFile() else saveDataWithJson()
                else -> saveDataWithDataStore()
            }
        }
    }

    internal suspend fun loadDataWithJson() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = context.filesDir.toPath().resolve(config.savePatch)
        if (!patch.exists()) {
            patch.createDirectories()
        }
        navigationModel.screenHeap.value.forEach { screenInstance ->
            val file = patch.resolve("csb_${screenInstance.id}")
            if (!file.exists()) return@forEach
            val json = file.readText()
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

    internal suspend fun loadWithJsonOneFile() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = context.filesDir.toPath().resolve(config.savePatch)
        val file = patch.resolve("csb_settings.json")
        if (!file.exists()) return@withContext

        val json = file.readText()
        val storedData = try {
            Json.decodeFromString<CSBStoredData>(json)
        } catch (e: Exception) {
            Log.d("CSB", "Error loading settings from single file", e)
            return@withContext
        }

        storedData.screenSettings.forEach { (_, packages) ->
            packages.forEach { pack ->
                try {
                    val setting = findSettingById(pack.id).getOrNull() ?: return@forEach
                    setting.loadLogic(pack)
                } catch (_: Exception) {
                }
            }
        }
    }

    internal suspend fun saveWithJsonOneFile() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = context.filesDir.toPath().resolve(config.savePatch)
        if (!patch.exists()) patch.createDirectories()
        val file = patch.resolve("csb_settings.json")

        val updatedMap = mutableMapOf<String, List<SaveSettingPackage>>()
        for (screen in navigationModel.screenHeap.value) {
            if (screen.attribute?.contains(ScreenAttribute.Unstored) == true) continue

            val jsonPackageList: List<SaveSettingPackage> = screen.settings.flatMap { it.settings }
                .mapNotNull { it.saveLogic() }

            if (jsonPackageList.isNotEmpty()) {
                updatedMap[screen.id] = jsonPackageList
            }
        }

        val json = Json.encodeToString(CSBStoredData(updatedMap))
        file.writeText(json)
    }

    internal suspend fun saveDataWithJson() = withContext(Dispatchers.IO) {
        if ("disableStored".isInFlag()) return@withContext
        val patch = context.filesDir.toPath().resolve(config.savePatch)
        if (!patch.exists()) {
            patch.createDirectories()
        }
        for (screen in navigationModel.screenHeap.value) {
            if (screen.attribute?.contains(ScreenAttribute.Unstored) == true) continue
            val file = patch.resolve("csb_${screen.id}")

            val jsonPackageList: List<SaveSettingPackage> = screen.settings.flatMap { it.settings }
                .mapNotNull { it.saveLogic() }

            if (jsonPackageList.isNotEmpty()) {
                val json = Json.encodeToString(jsonPackageList)
                file.writeText(json)
            }
        }
    }

    internal suspend fun loadWithDataStore() = withContext(Dispatchers.IO) {
        val storedData = context.csbDataStore.data.first()

        navigationModel.screenHeap.value.forEach { screenInstance ->
            val packages = storedData.screenSettings[screenInstance.id] ?: emptyList()
            packages.forEach { pack ->
                try {
                    val setting = findSettingById(pack.id).getOrNull() ?: return@forEach
                    setting.loadLogic(pack)
                } catch (_: Exception) {
                }
            }
        }
    }

    internal suspend fun saveDataWithDataStore() = withContext(Dispatchers.IO) {
        context.csbDataStore.updateData { currentData ->
            val updatedMap = currentData.screenSettings.toMutableMap()

            for (screen in navigationModel.screenHeap.value) {
                if (screen.attribute?.contains(ScreenAttribute.Unstored) == true) {
                    updatedMap.remove(screen.id)
                    continue
                }

                val jsonPackageList: List<SaveSettingPackage> =
                    screen.settings.flatMap { it.settings }
                        .mapNotNull { it.saveLogic() }

                if (jsonPackageList.isNotEmpty()) {
                    updatedMap[screen.id] = jsonPackageList
                } else {
                    updatedMap.remove(screen.id)
                }
            }
            currentData.copy(screenSettings = updatedMap)
        }
    }
}