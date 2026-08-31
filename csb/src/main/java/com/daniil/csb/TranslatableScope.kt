package com.daniil.csb

import androidx.annotation.StringRes

interface TranslatableScope {
    fun res(@StringRes id: Int): String = "res:$id"
}