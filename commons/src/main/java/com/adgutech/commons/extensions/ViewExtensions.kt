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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.TintHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.progressindicator.CircularProgressIndicator
import dev.chrisbanes.insetter.applyInsetter

const val ANIM_DURATION = 300L

fun View.isVisible() {
    visibility = View.VISIBLE
}

fun View.isGone() {
    visibility = View.GONE
}

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun EditText.appHandleColor(context: Context): EditText {
    if (context.preference.isMaterialYou) return this
    TintHelper.colorHandles(this, ThemeStore.accentColor(context))
    return this
}

fun NavigationBarView.setItemColors(@ColorInt normalColor: Int, @ColorInt selectedColor: Int) {
    val csl = ColorStateList(
        arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
        intArrayOf(normalColor, selectedColor)
    )
    itemIconTintList = csl
    itemTextColor = csl
}

fun NavigationView.setItemColors(@ColorInt normalColor: Int, @ColorInt selectedColor: Int) {
    val csl = ColorStateList(
        arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
        intArrayOf(normalColor, selectedColor)
    )
    itemIconTintList = csl
    itemTextColor = csl
}

/**
 * Potentially animate showing a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot of the view, and animate this in, only changing the visibility (and
 * thus layout) when the animation completes.
 */
fun NavigationBarView.show() {
    if (this is NavigationRailView) return
    if (isVisible) return

    val parent = parent as ViewGroup
    // View needs to be laid out to create a snapshot & know position to animate. If view isn't
    // laid out yet, need to do this manually.
    if (!isLaidOut) {
        measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    drawable.setBounds(left, parent.height, right, parent.height + height)
    parent.overlay.add(drawable)
    ValueAnimator.ofInt(parent.height, top).apply {
        duration = ANIM_DURATION
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.accelerate_decelerate
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
            isVisible = true
        }
        start()
    }
}

/**
 * Potentially animate hiding a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot, instantly hide the view (so content lays out to fill), then animate
 * out the snapshot.
 */
fun NavigationBarView.hide() {
    if (this is NavigationRailView) return
    if (isGone) return

    if (!isLaidOut) {
        isGone = true
        return
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    val parent = parent as ViewGroup
    drawable.setBounds(left, top, right, bottom)
    parent.overlay.add(drawable)
    isGone = true
    ValueAnimator.ofInt(top, parent.height).apply {
        duration = ANIM_DURATION
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.accelerate_decelerate
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
        }
        start()
    }
}

fun BottomSheetBehavior<*>.peekHeightAnimate(value: Int): Animator {
    return ObjectAnimator.ofInt(this, "peekHeight", value)
        .apply {
            duration = ANIM_DURATION
            start()
        }
}

fun MaterialCardView.animateRadius(cornerRadius: Float, pause: Boolean = true) {
    ValueAnimator.ofFloat(radius, cornerRadius).apply {
        addUpdateListener { radius = animatedValue as Float }
        start()
    }
    ValueAnimator.ofInt(measuredWidth, if (pause) (height * 1.5).toInt() else height).apply {
        addUpdateListener {
            updateLayoutParams<ViewGroup.LayoutParams> { width = animatedValue as Int }
        }
        start()
    }
}

fun MaterialCardView.animateToCircle() {
    animateRadius(measuredHeight / 2F, pause = false)
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService<InputMethodManager>()
                imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

/**
 * This will draw our view above the navigation bar instead of behind it by adding margins.
 */
fun View.drawAboveSystemBars(context: Context, onlyPortrait: Boolean = true) {
    if (context.preference.isFullScreenMode) return
    if (onlyPortrait && context.isLandscape) return
    applyInsetter {
        type(navigationBars = true) {
            margin()
        }
    }
}

/**
 * This will draw our view above the navigation bar instead of behind it by adding padding.
 */
fun View.drawAboveSystemBarsWithPadding(context: Context) {
    if (context.preference.isFullScreenMode) return
    applyInsetter {
        type(navigationBars = true) {
            padding()
        }
    }
}

var View.scrollIndicatorsCompat: Int
    get() = ViewCompat.getScrollIndicators(this)
    set(value) {
        ViewCompat.setScrollIndicators(this, value)
    }

fun CircularProgressIndicator.showCircularProgress(context: Context, isIndeterminate: Boolean) {
    this.isIndeterminate = isIndeterminate
    isVisible = isIndeterminate
    accentColor(context)
}