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
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey

class TrackCoverImageLoader : ModelLoader<TrackCoverImage, Bitmap> {

    override fun buildLoadData(
        model: TrackCoverImage,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<Bitmap> {
        return LoadData(ObjectKey(model.bitmap), TrackCoverImageFetcher(model))
    }

    override fun handles(model: TrackCoverImage): Boolean {
        return true
    }
}

class Factory : ModelLoaderFactory<TrackCoverImage, Bitmap> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<TrackCoverImage, Bitmap> {
        return TrackCoverImageLoader()
    }

    override fun teardown() {}
}