package com.adgutech.commons.extensions

import android.content.Context
import androidx.core.view.WindowInsetsCompat

fun WindowInsetsCompat?.getBottomInsets(context: Context): Int {
    return if (context.preference.isFullScreenMode) {
        return 0
    } else {
        this?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: context.navigationBarHeight
    }
}