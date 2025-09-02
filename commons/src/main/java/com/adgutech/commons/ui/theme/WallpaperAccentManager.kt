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

import android.app.WallpaperManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import code.name.monkey.appthemehelper.ThemeStore
import com.adgutech.commons.extensions.preference
import com.adgutech.commons.hasVersionOreoMR1

class WallpaperAccentManager(val context: Context) {

    private val onColorsChangedListener by lazy {
        WallpaperManager.OnColorsChangedListener { _, _ ->
            updateColors()
        }
    }

    fun init() {
        if (hasVersionOreoMR1) {
            with(WallpaperManager.getInstance(context)) {
                updateColors()
                if (context.preference.isWallpaperAccent) {
                    addOnColorsChangedListener(
                        onColorsChangedListener,
                        Handler(Looper.getMainLooper())
                    )
                }
            }
        }
    }

    fun release() {
        if (hasVersionOreoMR1) {
            WallpaperManager.getInstance(context)
                .removeOnColorsChangedListener(onColorsChangedListener)
        }
    }

    private fun updateColors() {
        if (hasVersionOreoMR1) {
            val colors = WallpaperManager.getInstance(context)
                .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            if (colors != null) {
                val primaryColor = colors.primaryColor.toArgb()
                ThemeStore.editTheme(context).wallpaperColor(context, primaryColor).commit()
            }
        }
    }
}