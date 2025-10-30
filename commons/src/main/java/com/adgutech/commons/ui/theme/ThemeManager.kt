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

package com.adgutech.commons.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.os.PowerManager
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import com.adgutech.commons.R
import com.adgutech.commons.extensions.preference
import com.adgutech.commons.ui.theme.ThemeMode.*

val Context.generalThemeValue
    get() = preference.getGeneralThemeValue(isSystemDarkModeEnabled())

fun Context.getNightMode(): Int = when (generalThemeValue) {
    LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    DARK -> AppCompatDelegate.MODE_NIGHT_YES
    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}

@StyleRes
fun Context.getThemeResValue(): Int =
    if (preference.isMaterialYou) {
        if (generalThemeValue == BLACK) R.style.Theme_AppTheme_MD3_Black
        else R.style.Theme_AppTheme_MD3
    } else {
        when (generalThemeValue) {
            LIGHT -> R.style.Theme_AppTheme_Light
            DARK -> R.style.Theme_AppTheme_Base
            BLACK -> R.style.Theme_AppTheme_Black
            AUTO -> R.style.Theme_AppTheme_FollowSystem
        }
    }

fun Context.isSystemDarkModeEnabled(): Boolean {
    val isBatterySaverEnabled =
        (getSystemService<PowerManager>())?.isPowerSaveMode ?: false
    val isDarkModeEnabled =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    return isBatterySaverEnabled or isDarkModeEnabled
}