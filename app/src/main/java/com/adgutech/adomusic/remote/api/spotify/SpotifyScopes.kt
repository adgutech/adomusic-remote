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

package com.adgutech.adomusic.remote.api.spotify

/**
 * Created by Adolfo Gutiérrez on 05/26/2025.
 */

object SpotifyScopes {

    // Images
    const val UGC_IMAGE_UPLOAD = "ugc-image-upload"

    // Spotify Connect
    const val USER_READ_PLAYBACK_STATE = "user-read-playback-state"
    const val USER_MODIFY_PLAYBACK_STATE = "user-modify-playback-state"
    const val USER_READ_CURRENTLY_PLAYING = "user-read-currently-playing"

    // Playback
    const val APP_REMOTE_CONTROL = "app-remote-control"
    const val STREAMING = "streaming"

    // Playlists
    const val PLAYLIST_READ_PRIVATE = "playlist-read-private"
    const val PLAYLIST_READ_COLLABORATIVE = "playlist-read-collaborative"
    const val PLAYLIST_MODIFY_PRIVATE = "playlist-modify-private"
    const val PLAYLIST_MODIFY_PUBLIC = "playlist-modify-public"

    // Follow
    const val USER_FOLLOW_MODIFY = "user-follow-modify"
    const val USER_FOLLOW_READ = "user-follow-read"

    // Listening History
    const val USER_READ_PLAYBACK_POSITION = "user-read-playback-position"
    const val USER_TOP_READ = "user-top-read"
    const val USER_READ_RECENTLY_PLAYED = "user-read-recently-played"

    // Library
    const val USER_LIBRARY_MODIFY = "user-library-modify"
    const val USER_LIBRARY_READ = "user-library-read"

    // Users
    const val USER_READ_EMAIL = "user-read-email"
    const val USER_READ_PRIVATE = "user-read-private"

    // Open Access
    const val USER_SOA_LINK = "user-soa-link"
    const val USER_SOA_UNLINK = "user-soa-unlink"
    const val SOA_MANAGE_ENTITLEMENTS = "soa-manage-entitlements"
    const val SOA_MANAGE_PARTNER = "soa-manage-partner"
    const val SOA_CREATE_PARTNER = "soa-create-partner"
}