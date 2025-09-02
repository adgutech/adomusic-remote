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

package com.adgutech.commons.utils

import android.content.Context
import android.os.Handler
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.google.android.material.textview.MaterialTextView

/** Utility methods for working with animations.  */
object AnimUtils {
    private var fastOutSlowIn: Interpolator? = null

    @JvmStatic
    fun getFastOutSlowInInterpolator(context: Context?): Interpolator? {
        if (fastOutSlowIn == null) {
            fastOutSlowIn =
                AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in)
        }
        return fastOutSlowIn
    }

    /**
     * Animates filenames textview to marquee after a delay. Make sure to set [ ][TextView.setSelected] to false in order to stop the marquee later
     */
    @JvmStatic
    fun marqueeAfterDelay(
        delayInMillis: Int,
        marqueeView: MaterialTextView,
    ) {
        Handler()
            .postDelayed(
                {
                    // marquee works only when text view has focus
                    marqueeView.isSelected = true
                },
                delayInMillis.toLong(),
            )
    }
}
