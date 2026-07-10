package com.daniil.csb

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.daniil.csb.screens.ScreenBuilder

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

fun registerSettingScreens(
    screenBuilderScope: ScreenBuilder.() -> Unit
) {
    val data = ScreenBuilder().apply(screenBuilderScope)
    CSB.navigationModel.setScreensHeap(*data.screenHeap.toTypedArray())
    CSB.load()
    CSB.executeConfigAction()
}