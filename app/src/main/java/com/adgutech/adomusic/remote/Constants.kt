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

package com.adgutech.adomusic.remote

import androidx.annotation.ColorInt
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.APP_REMOTE_CONTROL
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.PLAYLIST_MODIFY_PRIVATE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.PLAYLIST_MODIFY_PUBLIC
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.PLAYLIST_READ_COLLABORATIVE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.PLAYLIST_READ_PRIVATE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.STREAMING
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.UGC_IMAGE_UPLOAD
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_FOLLOW_MODIFY
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_FOLLOW_READ
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_LIBRARY_MODIFY
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_LIBRARY_READ
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_MODIFY_PLAYBACK_STATE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_CURRENTLY_PLAYING
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_EMAIL
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_PLAYBACK_POSITION
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_PLAYBACK_STATE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_PRIVATE
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_READ_RECENTLY_PLAYED
import com.adgutech.adomusic.remote.api.spotify.SpotifyScopes.USER_TOP_READ

const val CLIENT_ID = "07eea963d6b84925aee7e0d5550df153"
const val SPOTIFY_PACKAGE = "com.spotify.music"

@SuppressWarnings("SpellCheckingInspection")
const val REDIRECT_URI = "adomusicremote://callback"

const val SPOTIFY_APPLICATIONS_MANAGER = "https://www.spotify.com/mx/account/apps/"
const val SPOTIFY_PREFERENCE = "https://open.spotify.com/preferences"

const val ADOMUSIC_REMOTE_PRO_PRODUCT_ID = "adomusic_remote_pro"

const val ORDER_HISTORY_GOOGLE_PLAY =
    "https://play.google.com/store/account/orderhistory"

/**
 * Request code that will be passed together with authentication result to the onAuthenticationResult
 */
const val REQUEST_CODE = 1337

val scopesList = arrayOf(
    //Images
    UGC_IMAGE_UPLOAD,
    //Spotify Connect
    USER_READ_PLAYBACK_STATE,
    USER_MODIFY_PLAYBACK_STATE,
    USER_READ_CURRENTLY_PLAYING,
    //Playback
    APP_REMOTE_CONTROL,
    STREAMING,
    //Playlists
    PLAYLIST_READ_PRIVATE,
    PLAYLIST_READ_COLLABORATIVE,
    PLAYLIST_MODIFY_PRIVATE,
    PLAYLIST_MODIFY_PUBLIC,
    //Follow
    USER_FOLLOW_MODIFY,
    USER_FOLLOW_READ,
    //Listening History
    USER_READ_PLAYBACK_POSITION,
    USER_TOP_READ,
    USER_READ_RECENTLY_PLAYED,
    //Library
    USER_LIBRARY_MODIFY,
    USER_LIBRARY_READ,
    //Users
    USER_READ_EMAIL,
    USER_READ_PRIVATE
)

//Limits
const val SEARCH_LIMIT = 10
const val TOP_20_PLAYED_LIMIT = 20
const val TOP_50_PLAYED_LIMIT = 50
const val TOP_PLAYED_HOME_LIMIT = 5

// Album type
const val ALBUM = "album"
const val APPEARS_ON = "appears_on"
const val COMPILATION = "compilation"
const val SINGLE = "single"

const val EXTRA_ALBUM_ID = "extra_album_id"
const val EXTRA_ARTIST_ID = "extra_artist_id"
const val EXTRA_ARTIST_NAME = "extra_artist_name"
const val EXTRA_CURRENTLY_TRACK = "extra_currently_track"
const val EXTRA_PLAYLIST = "extra_playlist"
const val EXTRA_PLAYLIST_ID = "extra_playlist_id"
const val EXTRA_PLAYLISTS = "extra_playlists"
const val EXTRA_TRACK = "extra_track"
const val EXTRA_TRACK_URI = "extra_track_uri"
const val EXTRA_USER_ID = "extra_user_id"

const val USER_COLLECTION = ":collection"

@ColorInt
const val WHITE: Int = 0xFFFFFFFF.toInt()

@ColorInt
const val BLACK: Int = 0xFF000000.toInt()