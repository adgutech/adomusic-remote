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

package com.adgutech.adomusic.remote.volume

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.getSystemService

class AudioVolumeObserver(private val context: Context) {
    private val mAudioManager: AudioManager =
        context.getSystemService()!!
    private var contentObserver: AudioVolumeContentObserver? = null

    fun register(audioStreamType: Int, listener: OnAudioVolumeChangedListener) {
        val handler = Handler(Looper.getMainLooper())
        // with this handler AudioVolumeContentObserver#onChange()
        //   will be executed in the main thread
        // To execute in another thread you can use a Looper
        // +info: https://stackoverflow.com/a/35261443/904907
        contentObserver = AudioVolumeContentObserver(
            handler,
            mAudioManager,
            audioStreamType,
            listener
        )
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            contentObserver!!
        )
    }

    fun unregister() {
        if (contentObserver != null) {
            context.contentResolver.unregisterContentObserver(contentObserver!!)
            contentObserver = null
        }
    }
}