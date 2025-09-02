package com.adgutech.adomusic.remote.extensions

import android.os.Bundle
import android.os.Parcelable
import com.adgutech.adomusic.remote.application.appClassLoader

fun <T : Parcelable> Bundle.getParcelableSafe(key: String?): T? {
    classLoader = appClassLoader
    return getParcelable(key)
}