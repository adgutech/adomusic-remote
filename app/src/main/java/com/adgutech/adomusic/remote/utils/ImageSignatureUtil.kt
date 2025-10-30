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

package com.adgutech.adomusic.remote.utils

import android.content.Context
import com.bumptech.glide.signature.ObjectKey

class ImageSignatureUtil private constructor(context: Context) {

    private val albumSignature =
        context.getSharedPreferences(ALBUM_SIGNATURE, Context.MODE_PRIVATE)

    private val artistSignature =
        context.getSharedPreferences(ARTIST_SIGNATURE, Context.MODE_PRIVATE)

    private val trackSignature =
        context.getSharedPreferences(TRACK_SIGNATURE, Context.MODE_PRIVATE)

    private val playlistSignature =
        context.getSharedPreferences(PLAYLIST_SIGNATURE, Context.MODE_PRIVATE)

    private val userSignature =
        context.getSharedPreferences(USER_SIGNATURE, Context.MODE_PRIVATE)

    private val trackCoverSignature =
        context.getSharedPreferences(TRACK_COVER_SIGNATURE, Context.MODE_PRIVATE)

    private fun getAlbumSignatureRaw(raw: String?): Long {
        return albumSignature.getLong(raw, 0)
    }

    private fun getArtistSignatureRaw(raw: String?): Long {
        return artistSignature.getLong(raw, 0)
    }

    private fun getPlaylistSignatureRaw(raw: String?): Long {
        return playlistSignature.getLong(raw, 0)
    }

    private fun getTrackSignatureRaw(raw: String?): Long {
        return trackSignature.getLong(raw, 0)
    }

    private fun getTrackCoverSignatureRaw(raw: String?): Long {
        return trackCoverSignature.getLong(raw, 0)
    }

    private fun getUserSignatureRaw(raw: String?): Long {
        return userSignature.getLong(raw, 0)
    }

    fun getAlbumSignature(raw: String?): ObjectKey {
        return ObjectKey(getAlbumSignatureRaw(raw).toString())
    }

    fun getArtistSignature(raw: String?): ObjectKey {
        return ObjectKey(getArtistSignatureRaw(raw).toString())
    }

    fun getPlaylistSignature(raw: String?): ObjectKey {
        return ObjectKey(getPlaylistSignatureRaw(raw).toString())
    }

    fun getTrackSignature(raw: String?): ObjectKey {
        return ObjectKey(getTrackSignatureRaw(raw).toString())
    }

    fun getTrackCoverSignature(raw: String?): ObjectKey {
        return ObjectKey(getTrackCoverSignatureRaw(raw).toString())
    }

    fun getUserSignature(raw: String?): ObjectKey {
        return ObjectKey(getUserSignatureRaw(raw).toString())
    }

    companion object {
        private var INSTANCE: ImageSignatureUtil? = null
        fun getInstance(context: Context): ImageSignatureUtil {
            if (INSTANCE == null) {
                INSTANCE = ImageSignatureUtil(context)
            }
            return INSTANCE!!
        }

        private const val ALBUM_SIGNATURE = "album_signatures"
        private const val ARTIST_SIGNATURE = "artist_signatures"
        private const val TRACK_SIGNATURE = "track_signatures"
        private const val PLAYLIST_SIGNATURE = "playlist_signatures"
        private const val USER_SIGNATURE = "user_signatures"
        private const val TRACK_COVER_SIGNATURE = "track_cover_signatures"
    }
}