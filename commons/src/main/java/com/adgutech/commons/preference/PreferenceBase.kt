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

package com.adgutech.commons.preference

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.adgutech.commons.R
import com.adgutech.commons.extensions.defaultSharedPreferences
import com.adgutech.commons.extensions.getStringOrDefault
import com.adgutech.commons.hasVersionOreoMR1
import com.adgutech.commons.hasVersionS
import com.adgutech.commons.ui.theme.ThemeMode
import com.adgutech.commons.ui.views.TopAppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

open class PreferenceBase(context: Context) {

    protected val preference = context.defaultSharedPreferences

    companion object {
        fun newInstance(context: Context) = PreferenceBase(context)
        const val ACCENT_COLOR = "accent_color"
        const val BLACK_THEME = "black_theme"
        const val CUSTOM_FONT = "custom_font"
        const val DESATURATED_COLOR = "desaturated_color"
        const val GENERAL_THEME = "general_theme"
        const val MATERIAL_YOU = "material_you"
        const val LANGUAGE_CODE = "language_code"
        const val LOCALE_AUTO_STORE_ENABLED = "locale_auto_store_enabled"
        const val LAST_USED_TAB = "last_used_tab"
        const val KEEP_SCREEN_ON = "keep_screen_on"
        const val APPBAR_MODE = "appbar_mode"
        const val FULL_SCREEN_MODE = "full_screen_mode"
        const val REMEMBER_LAST_TAB = "remember_last_tab"
        const val SHOW_WHEN_LOCKED = "show_when_locked"
        const val TAB_TITLES_MODE = "tab_titles_mode"
        const val WALLPAPER_ACCENT = "wallpaper_accent"
    }

    val appBarMode: TopAppBarLayout.AppBarMode
        get() = if (preference.getString(APPBAR_MODE, "1") == "0") {
            TopAppBarLayout.AppBarMode.COLLAPSING
        } else {
            TopAppBarLayout.AppBarMode.SIMPLE
        }

    var isBlackMode
        get() = preference.getBoolean(BLACK_THEME, false)
        set(value) {
            preference.edit {
                putBoolean(BLACK_THEME, value)
            }
        }

    val isCustomFont
        get() = preference.getBoolean(
            CUSTOM_FONT, false
        )

    var isDesaturatedColor
        get() = preference.getBoolean(
            DESATURATED_COLOR, false
        )
        set(value) = preference.edit {
            putBoolean(DESATURATED_COLOR, value)
        }

    val isFullScreenMode
        get() = preference.getBoolean(
            FULL_SCREEN_MODE, false
        )

    var isLocaleAutoStorageEnabled: Boolean
        get() = preference.getBoolean(
            LOCALE_AUTO_STORE_ENABLED,
            false
        )
        set(value) = preference.edit {
            putBoolean(LOCALE_AUTO_STORE_ENABLED, value)
        }

    val isMaterialYou
        get() = preference.getBoolean(MATERIAL_YOU, hasVersionS)

    val isKeepScreenOn get() = preference.getBoolean(KEEP_SCREEN_ON, false)

    val isRememberLastTab: Boolean
        get() = preference.getBoolean(REMEMBER_LAST_TAB, true)

    val isShowWhenLockedEnabled get() = preference.getBoolean(SHOW_WHEN_LOCKED, false)

    val isWallpaperAccent: Boolean
        get() = preference.getBoolean(WALLPAPER_ACCENT, hasVersionOreoMR1 && !hasVersionS)

    var languageCode: String
        get() = preference.getStringOrDefault(LANGUAGE_CODE, "auto")
        set(value) = preference.edit { putString(LANGUAGE_CODE, value) }

    var lastTab: Int
        get() = preference
            .getInt(LAST_USED_TAB, 0)
        set(value) = preference.edit { putInt(LAST_USED_TAB, value) }

    val tabTitleMode: Int
        get() {
            return when (preference.getStringOrDefault(
                TAB_TITLES_MODE, "0"
            ).toInt()) {
                0 -> BottomNavigationView.LABEL_VISIBILITY_AUTO
                1 -> BottomNavigationView.LABEL_VISIBILITY_LABELED
                2 -> BottomNavigationView.LABEL_VISIBILITY_SELECTED
                3 -> BottomNavigationView.LABEL_VISIBILITY_UNLABELED
                else -> BottomNavigationView.LABEL_VISIBILITY_LABELED
            }
        }

    fun registerOnSharedPreferenceChangedListener(
        listener: OnSharedPreferenceChangeListener
    ) = preference.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterOnSharedPreferenceChangedListener(
        listener: OnSharedPreferenceChangeListener
    ) = preference.unregisterOnSharedPreferenceChangeListener(listener)

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String =
            preference.getStringOrDefault(GENERAL_THEME, "auto")
        return if (isBlackMode && isSystemDark && themeMode != "light") {
            ThemeMode.BLACK
        } else {
            if (isBlackMode && themeMode == "dark") {
                ThemeMode.BLACK
            } else {
                when (themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    "auto" -> ThemeMode.AUTO
                    else -> ThemeMode.AUTO
                }
            }
        }
    }

    fun themeResFromPrefValue(themePrefValue: String): Int {
        return when (themePrefValue) {
            "light" -> R.style.Theme_AppTheme_Light
            "dark" -> R.style.Theme_AppTheme
            else -> R.style.Theme_AppTheme
        }
    }
}