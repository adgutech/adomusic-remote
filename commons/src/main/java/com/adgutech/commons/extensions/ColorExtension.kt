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
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import com.adgutech.commons.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout

fun Int.ripAlpha(): Int {
    return ColorUtil.stripAlpha(this)
}

fun Dialog.colorControlNormal() = resolveColor(android.R.attr.colorControlNormal)

fun Toolbar.backgroundTintList() {
    val surfaceColor = ATHUtil.resolveColor(context, com.google.android.material.R.attr.colorSurface, Color.BLACK)
    val colorStateList = ColorStateList.valueOf(surfaceColor)
    backgroundTintList = colorStateList
}

fun Context.accentColor() = ThemeStore.accentColor(this)

fun Fragment.accentColor() = ThemeStore.accentColor(requireContext())

fun Context.surfaceColor() = resolveColor(com.google.android.material.R.attr.colorSurface, Color.WHITE)

fun Fragment.surfaceColor() = resolveColor(com.google.android.material.R.attr.colorSurface, Color.WHITE)

fun Context.surfaceColor(fallBackColor: Int) = resolveColor(com.google.android.material.R.attr.colorSurface, fallBackColor)

fun Fragment.surfaceColor(fallBackColor: Int) = resolveColor(com.google.android.material.R.attr.colorSurface, fallBackColor)

fun Context.textColorSecondary() = resolveColor(android.R.attr.textColorSecondary)

fun Fragment.textColorSecondary() = resolveColor(android.R.attr.textColorSecondary)

fun Context.colorControlNormal() = resolveColor(android.R.attr.colorControlNormal)

fun Fragment.colorControlNormal() = resolveColor(android.R.attr.colorControlNormal)

fun Context.colorBackground() = resolveColor(android.R.attr.colorBackground)

fun Fragment.colorBackground() = resolveColor(android.R.attr.colorBackground)

fun Context.textColorPrimary() = resolveColor(android.R.attr.textColorPrimary)

fun Fragment.textColorPrimary() = resolveColor(android.R.attr.textColorPrimary)

fun Context.defaultFooterColor() = resolveColor(R.attr.defaultFooterColor)

fun Fragment.defaultFooterColor() = resolveColor(R.attr.defaultFooterColor)

fun Context.resolveColor(@AttrRes attr: Int, fallBackColor: Int = 0) =
    ATHUtil.resolveColor(this, attr, fallBackColor)

fun Fragment.resolveColor(@AttrRes attr: Int, fallBackColor: Int = 0) =
    ATHUtil.resolveColor(requireContext(), attr, fallBackColor)

fun Dialog.resolveColor(@AttrRes attr: Int, fallBackColor: Int = 0) =
    ATHUtil.resolveColor(context, attr, fallBackColor)

// Don't apply accent colors if Material You is enabled
// Material Components will take care of applying material you colors
fun CheckBox.addAccentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    buttonTintList = ColorStateList.valueOf(ThemeStore.accentColor(context))
}

fun SeekBar.addAccentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val colorState = ColorStateList.valueOf(ThemeStore.accentColor(context))
    progressTintList = colorState
    thumbTintList = colorState
}

fun Slider.addAccentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val accentColor = ThemeStore.accentColor(context)
    trackActiveTintList = accentColor.colorStateList
    trackInactiveTintList = ColorUtil.withAlpha(accentColor, 0.5F).colorStateList
    thumbTintList = accentColor.colorStateList
}

fun Slider.accent(context: Context) {
    if (context.preference.isMaterialYou) return
    val accentColor = context.accentColor()
    thumbTintList = accentColor.colorStateList
    trackActiveTintList = accentColor.colorStateList
    trackInactiveTintList = ColorUtil.withAlpha(accentColor, 0.1F).colorStateList
}

fun Button.accentTextColor(context: Context) {
    if (context.preference.isMaterialYou) return
    setTextColor(context.accentColor())
}

fun MaterialButton.accentBackgroundColor(context: Context) {
    if (context.preference.isMaterialYou) return
    backgroundTintList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf()),
        intArrayOf(context.accentColor(), context.accentColor().addAlpha(0.12f)))
}

fun MaterialButton.accentOutlineColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = ThemeStore.accentColor(context)
    val colorStateList = ColorStateList.valueOf(color)
    iconTint = colorStateList
    strokeColor = colorStateList
    setTextColor(colorStateList)
    rippleColor = colorStateList
}

fun MaterialButton.elevatedAccentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = context.darkAccentColorVariant()
    rippleColor = ColorStateList.valueOf(color)
    setBackgroundColor(color)
    setTextColor(MaterialValueHelper.getPrimaryTextColor(context, color.isColorLight))
    iconTint = ColorStateList.valueOf(context.accentColor())
}

fun SeekBar.applyColor(@ColorInt color: Int) {
    thumbTintList = ColorStateList.valueOf(color)
    progressTintList = ColorStateList.valueOf(color)
    progressBackgroundTintList = ColorStateList.valueOf(color)
}

fun Slider.applyColor(@ColorInt color: Int) {
    ColorStateList.valueOf(color).run {
        thumbTintList = this
        trackActiveTintList = this
        trackInactiveTintList = ColorStateList.valueOf(color.addAlpha(0.1f))
        haloTintList = this
    }
}

fun ExtendedFloatingActionButton.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = ThemeStore.accentColor(context)
    val textColor = MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color))
    val colorStateList = ColorStateList.valueOf(color)
    val textColorStateList = ColorStateList.valueOf(textColor)
    backgroundTintList = colorStateList
    setTextColor(textColorStateList)
    iconTint = textColorStateList
}

fun FloatingActionButton.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = ThemeStore.accentColor(context)
    val textColor = MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color))
    backgroundTintList = ColorStateList.valueOf(color)
    imageTintList = ColorStateList.valueOf(textColor)
}

fun MaterialButton.applyColor(color: Int) {
    val backgroundColorStateList = ColorStateList.valueOf(color)
    val textColorColorStateList = ColorStateList.valueOf(
        MaterialValueHelper.getPrimaryTextColor(
            context,
            ColorUtil.isColorLight(color)
        )
    )
    backgroundTintList = backgroundColorStateList
    setTextColor(textColorColorStateList)
    iconTint = textColorColorStateList
}

fun MaterialButton.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    applyColor(ThemeStore.accentColor(context))
}

fun MaterialButton.applyElevatedColor(color: Int) {
    val colorStateList = ColorStateList.valueOf(color)
    iconTint = colorStateList
    setTextColor(colorStateList)
}

fun MaterialButton.applyOutlineColor(color: Int) {
    val colorStateList = ColorStateList.valueOf(color)
    iconTint = colorStateList
    strokeColor = colorStateList
    setTextColor(colorStateList)
    rippleColor = colorStateList
}

fun TextInputLayout.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val accentColor = ThemeStore.accentColor(context)
    val colorState = ColorStateList.valueOf(accentColor)
    boxStrokeColor = accentColor
    defaultHintTextColor = colorState
    isHintAnimationEnabled = true
}

fun LinearProgressIndicator.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = ThemeStore.accentColor(context)
    setIndicatorColor(color)
    trackColor = ColorUtil.withAlpha(color, 0.2f)
}

fun LinearProgressIndicator.applyColor(color: Int) {
    setIndicatorColor(color)
    trackColor = ColorUtil.withAlpha(color, 0.2f)
}

fun CircularProgressIndicator.accentColor(context: Context) {
    if (context.preference.isMaterialYou) return
    val color = ThemeStore.accentColor(context)
    setIndicatorColor(color)
    trackColor = ColorUtil.withAlpha(color, 0.2f)
}

fun CircularProgressIndicator.applyColor(color: Int) {
    setIndicatorColor(color)
    trackColor = ColorUtil.withAlpha(color, 0.2f)
}

fun AppCompatImageView.accentColor(): Int = ThemeStore.accentColor(context)

fun TextInputLayout.setTint(context: Context, background: Boolean = true) {
    if (context.preference.isMaterialYou) return
    val accentColor = ThemeStore.accentColor(context)
    val colorState = ColorStateList.valueOf(accentColor)

    if (background) {
        backgroundTintList = colorState
        defaultHintTextColor = colorState
    } else {
        boxStrokeColor = accentColor
        defaultHintTextColor = colorState
        isHintAnimationEnabled = true
    }
}

@Suppress("DEPRECATION")
fun ConstraintLayout.applyColor(context: Context, @ColorRes colorRes: Int) {
    val color = context.resources.getColor(colorRes)
    setBackgroundColor(color.addAlpha(0.12f))
}

@CheckResult
fun Drawable.tint(@ColorInt color: Int): Drawable {
    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    setTint(color)
    return tintedDrawable
}

@CheckResult
fun Drawable.tint(context: Context, @ColorRes color: Int): Drawable =
    tint(context.getColorCompat(color))

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

@ColorInt
fun Context.darkAccentColor(): Int {
    return ColorUtils.blendARGB(
        accentColor(),
        surfaceColor(),
        if (surfaceColor().isColorLight) 0.9f else 0.92f
    )
}

@ColorInt
fun Context.darkAccentColorVariant(): Int {
    return ColorUtils.blendARGB(
        accentColor(),
        surfaceColor(),
        if (surfaceColor().isColorLight) 0.9f else 0.95f
    )
}

@ColorInt
fun Context.accentColorVariant(): Int {
    return if (surfaceColor().isColorLight) {
        accentColor().darkerColor
    } else {
        accentColor().lighterColor
    }
}

fun CollapsingToolbarLayout.accentColor() {
    setBackgroundColor(ThemeStore.accentColor(context))
}

@SuppressLint("RestrictedApi")
fun Context.getColorStateListByAttr(@AttrRes attr: Int): ColorStateList =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getColorStateList(0) }

@ColorInt
fun Context.getColorByAttr(@AttrRes attr: Int): Int =
    getColorStateListByAttr(attr).defaultColor

inline val @receiver:ColorInt Int.isColorLight
    get() = ColorUtil.isColorLight(this)

inline val @receiver:ColorInt Int.lightColor
    get() = ColorUtil.withAlpha(this, 0.5F)

inline val @receiver:ColorInt Int.lighterColor
    get() = ColorUtil.lightenColor(this)

inline val @receiver:ColorInt Int.darkerColor
    get() = ColorUtil.darkenColor(this)

inline val Int.colorStateList: ColorStateList
    get() = ColorStateList.valueOf(this)

fun @receiver:ColorInt Int.addAlpha(alpha: Float): Int {
    return ColorUtil.withAlpha(this, alpha)
}