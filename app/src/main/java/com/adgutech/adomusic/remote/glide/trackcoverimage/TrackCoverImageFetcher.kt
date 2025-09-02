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

package com.adgutech.adomusic.remote.glide.trackcoverimage

import android.graphics.Bitmap
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.spotify.protocol.types.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class TrackCoverImageFetcher(
    val model: TrackCoverImage
) : DataFetcher<Bitmap>, CoroutineScope by GlideScope() {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        val track = AppRemoteHelper.currentTrack!!
        launch {
            try {
                AppRemoteHelper.getImage(track.imageUri, Image.Dimension.LARGE) { bitmap ->
                    callback.onDataReady(bitmap)
                }
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }

    }

    override fun cleanup() {}

    override fun cancel() {
        cancel(null)
    }

    override fun getDataClass(): Class<Bitmap> {
        return Bitmap::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }
}

private val glideDispatcher: CoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

@Suppress("FunctionName")
internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + glideDispatcher)