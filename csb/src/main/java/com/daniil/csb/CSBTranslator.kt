package com.daniil.csb

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

interface CSBTranslator {
    @Composable
    fun translate(key: String): String
}