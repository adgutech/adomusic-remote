/*
 * Copyright (C) 2022-2025 Adolfo Gutiérrez <adgutech@gmail.com>
 * and Contributors.
 *
 * This file is part of Adgutech.
 *
 *  Adgutech is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.adgutech.adomusic.remote.extensions

import android.util.Log
import com.adgutech.adomusic.remote.BuildConfig

fun Any.logD(message: Any?) {
    logD(message.toString())
}

fun Any.logD(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(name, message)
    }
}

fun Any.logE(message: String) {
    Log.e(name, message)
}

fun Any.logE(e: Exception) {
    Log.e(name, e.message ?: "Error")
}

fun Any.logI(message: String) {
    Log.i(name, message)
}

fun Any.logI(e: Exception) {
    Log.i(name, e.message ?: "Info")
}

fun Any.logV(message: Any?) {
    logV(message.toString())
}

fun Any.logV(message: String) {
    if (BuildConfig.DEBUG) {
        Log.v(name, message)
    }
}

fun Any.logW(message: String) {
    Log.w(name, message)
}

fun Any.logW(e: Exception) {
    Log.w(name, e.message ?: "Warning")
}

private val Any.name: String get() = this::class.java.simpleName