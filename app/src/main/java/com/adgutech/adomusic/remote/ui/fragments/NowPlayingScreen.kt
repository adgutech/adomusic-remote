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

package com.adgutech.adomusic.remote.ui.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.adgutech.adomusic.remote.R

enum class NowPlayingScreen(
    @param:StringRes @field:StringRes
    val titleRes: Int,
    @param:DrawableRes @field:DrawableRes val drawableResId: Int,
    val id: Int
) {
    Blur(R.string.blur, R.drawable.np_blur, 2),
    Color(R.string.color, R.drawable.np_color, 3),
    Material(R.string.material, R.drawable.np_material, 5),
    MD3(R.string.md3, R.drawable.np_normal, 7),
    Normal(R.string.normal, R.drawable.np_normal, 0),
    Peek(R.string.peek, R.drawable.np_peek, 6),
    Plain(R.string.plain, R.drawable.np_plain, 1),
    Simple(R.string.simple, R.drawable.np_simple, 4)
}
