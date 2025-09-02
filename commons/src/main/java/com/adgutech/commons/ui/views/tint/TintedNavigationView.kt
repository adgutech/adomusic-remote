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

package com.adgutech.commons.ui.views.tint

import android.content.Context
import android.util.AttributeSet
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.ATHUtil
import com.adgutech.commons.extensions.preference
import com.adgutech.commons.extensions.setItemColors
import com.google.android.material.navigation.NavigationView

class TintedNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : NavigationView(context, attrs, defStyleAttr) {
    init {
        if (!context.preference.isMaterialYou) {
            val iconColor = ATHUtil.resolveColor(context, android.R.attr.colorControlNormal)
            val accentColor = ThemeStore.accentColor(context)
            setItemColors(iconColor, accentColor)
        }
    }
}