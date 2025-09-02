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

package com.adgutech.adomusic.remote.helpers

/**
 * Created by Adolfo Gutiérrez on 05/27/2025.
 */

class SortOrder {

    /**
     * Album sort order entries.
     */
    interface AlbumSortOrder {

        companion object {

            /* Artist sort order default */
            const val ALBUM_DEFAULT = "album_default"

            /* Album sort order A-Z */
            const val ALBUM_A_Z = "album_a_z"

            /* Album sort order A-Z */
            const val ALBUM_Z_A = "album_z_a"

            /* Album sort order artist */
            const val ALBUM_ARTIST = "album_artist"

            /* Album sort order artist */
            const val ALBUM_ARTIST_DESC = "album_artist_desc"

            /* Album sort order release date */
            const val ALBUM_RELEASE_DATE = "album_release_date"

            /* Album sort order release date */
            const val ALBUM_RELEASE_DATE_DESC = "album_release_date_desc"
        }
    }

    /**
     * Artist sort order entries.
     */
    interface ArtistSortOrder {

        companion object {

            /* Artist sort order default */
            const val ARTIST_DEFAULT = "artist_default"

            /* Artist sort order A-Z */
            const val ARTIST_A_Z = "artist_a_z"

            /* Artist sort order Z-A */
            const val ARTIST_Z_A = "artist_z_a"

            /* Artist sort order most followed */
            const val ARTIST_MOST_FOLLOWED = "artist_most_followed"

            /* Artist sort order most followed */
            const val ARTIST_MOST_FOLLOWED_DESC = "artist_most_followed_desc"
        }
    }

    /**
     * Playlist sort order entries.
     */
    interface PlaylistSortOrder {

        companion object {

            /* Playlist sort order default */
            const val PLAYLIST_DEFAULT = "playlist_default"

            /* Playlist sort order A-Z */
            const val PLAYLIST_A_Z = "playlist_a_z"

            /* Playlist sort order Z-A */
            const val PLAYLIST_Z_A = "playlist_z_a"

            /* Playlist sort order display name */
            const val PLAYLIST_DISPLAY_NAME = "playlist_display_name"

            /* Playlist sort order display name */
            const val PLAYLIST_DISPLAY_NAME_DESC = "playlist_display_name_desc"

            /* Playlist sort order number of tracks */
            const val PLAYLIST_TRACK_COUNT = "track_count"

            /* Playlist sort order number of tracks */
            const val PLAYLIST_TRACK_COUNT_DESC = "track_count_desc"
        }
    }
}