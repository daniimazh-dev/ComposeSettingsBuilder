package com.daniil.csb

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CSBInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let { CSB.init(it) }
        return true
    }

    override fun query(uri: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, p1: ContentValues?): Uri? = null
    override fun delete(uri: Uri, p1: String?, p2: Array<out String>?): Int = 0
    override fun update(uri: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0
}

class SettingViewModel(
    val navigationSettingViewModel: SettingsNavigationModel? = null,
    application: Application,
): AndroidViewModel(application) {
    companion object {
        val NAVIGATION_MODEL_KEY = object : CreationExtras.Key<SettingsNavigationModel> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] ?: error("Application not found in CreationExtras")
                val navigation = this[NAVIGATION_MODEL_KEY] ?: CSB.navigationModel
                SettingViewModel(navigation, application)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
//            val context = getApplication<Application>().applicationContext
//            navigationSettingViewModel?.initialize()
        }
    }

    fun create(
        context: Context,
        settingViewModel: SettingsNavigationModel,
        coroutineScope: CoroutineScope
    ) {
//        settingViewModel.initialize(context)
    }
}

fun ComponentActivity.settingViewModel(): Lazy<SettingViewModel> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val extras = MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(SettingViewModel.NAVIGATION_MODEL_KEY, CSB.navigationModel)
        }
        ViewModelProvider(this.viewModelStore, SettingViewModel.Factory, extras)[SettingViewModel::class.java]
    }
}
