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

package com.adgutech.commons.extensions

import android.database.Cursor

/**
 * Created by Adolfo Gutierrez on 03/24/25.
 */

// exception is rethrown manually in order to have a readable stacktrace

fun Cursor.getInt(columnName: String): Int {
    try {
        return getInt(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getLong(columnName: String): Long {
    try {
        return getLong(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getString(columnName: String): String {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}