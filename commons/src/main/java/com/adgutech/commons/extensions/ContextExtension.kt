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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.TintHelper
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Context.getTintedDrawable(@DrawableRes id: Int, @ColorInt color: Int): Drawable {
    return ContextCompat.getDrawable(this, id)?.tint(color)!!
}

fun Context.getIconRes(@DrawableRes drawable: Int): Drawable {
    return TintHelper.createTintedDrawable(this, drawable, // for playlist drawable
        ATHUtil.resolveColor(this, android.R.attr.colorControlNormal))
}

val Context.navigationBarHeight: Int
    get() {
        var result = 0
        val resourceId = resources
            .getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

@Dimension
fun Context.getDimensionPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

val Context.isLandscape: Boolean
    get() = (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)

val Context.isTablet: Boolean
    get() = (resources.configuration.smallestScreenWidthDp >= 600)

val Fragment.isLandscape: Boolean
    get() = requireContext().isLandscape

val Fragment.isTablet: Boolean
    get() = requireContext().isTablet

fun Context.hasEqualizer(): Boolean {
    val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
    val pm = packageManager
    val ri = pm.resolveActivity(effects, 0)
    return ri != null
}

@SuppressLint("RestrictedApi")
fun Context.obtainStyledAttributesCompat(
    set: AttributeSet? = null,
    @StyleableRes attrs: IntArray,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
): TintTypedArray =
    TintTypedArray.obtainStyledAttributes(this, set, attrs, defStyleAttr, defStyleRes)

@OptIn(ExperimentalContracts::class)
@SuppressLint("RestrictedApi")
inline fun <R> TintTypedArray.use(block: (TintTypedArray) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return try {
        block(this)
    } finally {
        recycle()
    }
}

fun Context.getPackageNameFromUri(): Uri {
    return Uri.parse("package:$packageName")
}

fun Context.gridCount(): Int {
    if (isTablet) {
        return if (isLandscape) 6 else 4
    }
    return if (isLandscape) 4 else 2
}

fun Fragment.gridCount(): Int {
    return requireContext().gridCount()
}