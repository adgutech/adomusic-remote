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

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * @param activity, Activity
 * @param next, if the button is next, if false then it's considered previous
 */
class MusicSeekSkipTouchListener(val activity: FragmentActivity, val next: Boolean) :
    View.OnTouchListener {

    private var job: Job? = null
    private var counter = 0
    private var wasSeeking = false

    private var startX = 0f
    private var startY = 0f

    private val scaledTouchSlop = ViewConfiguration.get(activity).scaledTouchSlop

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                job = activity.lifecycleScope.launch(Dispatchers.Default) {
                    counter = 0
                    while (isActive) {
                        delay(500)
                        wasSeeking = true
                        var seekingDuration = AppRemoteHelper.trackProgressMillis
                        if (next) {
                            seekingDuration += 5000 * (counter.floorDiv(2) + 1)
                        } else {
                            seekingDuration -= 5000 * (counter.floorDiv(2) + 1)
                        }
                        withContext(Dispatchers.Main) {
                            AppRemoteHelper.seekTo(seekingDuration.toInt())
                        }
                        counter += 1
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                job?.cancel()
                val endX = event.x
                val endY = event.y
                if (!wasSeeking && isAClick(startX, endX, startY, endY)) {
                    if (next) {
                        AppRemoteHelper.playNextTrack()
                    } else {
                        AppRemoteHelper.playPreviousTrack()
                    }
                }

                wasSeeking = false
            }

            MotionEvent.ACTION_CANCEL -> {
                job?.cancel()
            }
        }
        return false
    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = abs(startX - endX)
        val differenceY = abs(startY - endY)
        return !(differenceX > scaledTouchSlop || differenceY > scaledTouchSlop)
    }
}