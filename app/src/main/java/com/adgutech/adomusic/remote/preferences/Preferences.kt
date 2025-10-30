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

package com.adgutech.adomusic.remote.preferences

import android.content.Context
import androidx.core.content.edit
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.ui.fragments.NowPlayingScreen
import com.adgutech.adomusic.remote.ui.fragments.players.CoverLyricsType
import com.adgutech.commons.extensions.getStringOrDefault
import com.adgutech.commons.preference.PreferenceBase

/**
 * Created by Adolfo Gutiérrez on 03/24/25.
 */

class Preferences(context: Context) : PreferenceBase(context) {

    companion object {
        fun newInstance(context: Context) = Preferences(context)
        const val ACCESS_TOKEN = "access_token"
        const val ACCESS_TOKEN_EXPIRATION_TIME = "access_token_expiration_time"
        const val ADAPTIVE_COLOR = "adaptive_color"
        const val ADJUST_VOLUME = "adjust_volume"
        const val ALBUM_SORT_ORDER = "album_sort_order"
        const val ARTIST_SORT_ORDER = "artist_sort_order"
        const val PLAYLIST_SORT_ORDER = "playlist_sort_order"
        const val ARTIST_TIME_RANGE = "artist_time_range"
        const val TRACK_TIME_RANGE = "track_time_range"
        const val CIRCLE_PLAY_BUTTON = "circle_play_button"
        const val EXPAND_PANEL = "expand_playback_panel"
        const val EXTRA_CONTROLS = "extra_controls"
        const val EQUALIZER_BANDS = "equalizer_bands"
        const val EQUALIZER_ENABLED = "equalizer_enabled"
        const val EQUALIZER_PRESET = "equalizer_preset"
        const val LYRICS_SCREEN_ON = "lyrics_screen_on"
        const val LYRICS_TYPE = "lyrics_type"
        const val NEW_BLUR_AMOUNT = "new_blur_amount"
        const val NOW_PLAYING_SCREEN = "now_playing_screen"
        const val PAUSE_ON_ZERO_VOLUME = "pause_on_zero_volume"
        const val SHOW_ITEM_LIMIT_ALERT = "show_item_limit_alert"
        const val SHOW_LYRICS = "show_lyrics"
        const val SNOW_FALL = "snow_fall"
        const val VOLUME_VISIBILITY_MODE = "volume_visibility_mode"
        const val USER_LOGGED = "user_logged"
    }

    var accessToken: String
        get() = preference.getStringOrDefault(ACCESS_TOKEN, "access_token")
        set(value) = preference.edit { putString(ACCESS_TOKEN, value) }

    var accessTokenExpirationTime: Long
        get() = preference.getLong(ACCESS_TOKEN_EXPIRATION_TIME, 0L)
        set(value) = preference.edit { putLong(ACCESS_TOKEN_EXPIRATION_TIME, value) }

    var albumSortOrder: String
        get() = preference.getStringOrDefault(
            ALBUM_SORT_ORDER,
            SortOrder.AlbumSortOrder.ALBUM_DEFAULT
        )
        set(value) = preference.edit {
            putString(ALBUM_SORT_ORDER, value)
        }

    var artistSortOrder: String
        get() = preference.getStringOrDefault(
            ARTIST_SORT_ORDER,
            SortOrder.ArtistSortOrder.ARTIST_DEFAULT
        )
        set(value) = preference.edit {
            putString(ARTIST_SORT_ORDER, value)
        }

    var playlistSortOrder: String
        get() = preference.getStringOrDefault(
            PLAYLIST_SORT_ORDER,
            SortOrder.PlaylistSortOrder.PLAYLIST_DEFAULT
        )
        set(value) = preference.edit {
            putString(PLAYLIST_SORT_ORDER, value)
        }

    val blurAmount: Int
        get() = preference.getInt(NEW_BLUR_AMOUNT, 25)

    val isAdaptiveColor
        get() = preference.getBoolean(ADAPTIVE_COLOR, false)

    val isAdjustVolume: Boolean
        get() = preference.getBoolean(ADJUST_VOLUME, false)

    val isCirclePlayButton: Boolean
        get() = preference.getBoolean(CIRCLE_PLAY_BUTTON, false)

    val isExpandPanel: Boolean
        get() = preference.getBoolean(EXPAND_PANEL, false)

    val isExtraControls: Boolean
        get() = preference.getBoolean(EXTRA_CONTROLS, true)

    val isPauseOnZeroVolume get() = preference.getBoolean(PAUSE_ON_ZERO_VOLUME, false)

    val isVolumeVisibilityMode: Boolean
        get() = preference.getBoolean(VOLUME_VISIBILITY_MODE, false)

    var isUserLogged: Boolean
        get() = preference.getBoolean(USER_LOGGED, false)
        set(value) = preference.edit { putBoolean(USER_LOGGED, value) }

    var showLyrics: Boolean
        get() = preference.getBoolean(SHOW_LYRICS, false)
        set(value) = preference.edit { putBoolean(SHOW_LYRICS, value) }

    val artistTimeRange: String
        get() = preference.getStringOrDefault(ARTIST_TIME_RANGE, "medium_term")

    val trackTimeRange: String
        get() = preference.getStringOrDefault(TRACK_TIME_RANGE, "medium_term")

    var equalizerPreset : Int
        get() = preference.getInt(EQUALIZER_PRESET, 0)
        set(value) = preference.edit { putInt(EQUALIZER_PRESET, value) }

    var equalizerBands: String
        get() = preference.getStringOrDefault(EQUALIZER_BANDS, "")
        set(value) = preference.edit { putString(EQUALIZER_BANDS, value) }

    var isEqualizerEnabled: Boolean
        get() = preference.getBoolean(EQUALIZER_ENABLED, false)
        set(value) = preference.edit { putBoolean(EQUALIZER_ENABLED, value) }

    val isLyricsScreenOn: Boolean
        get() = preference.getBoolean(LYRICS_SCREEN_ON, false)

    var isShowItemLimitAlert: Boolean
        get() = preference.getBoolean(SHOW_ITEM_LIMIT_ALERT, true)
        set(value) = preference.edit { putBoolean(SHOW_ITEM_LIMIT_ALERT, value) }

    val isSnowFall: Boolean
        get() = preference.getBoolean(SNOW_FALL, false)

    val lyricsType: CoverLyricsType
        get() = if (preference.getString(LYRICS_TYPE, "0") == "0") {
            CoverLyricsType.REPLACE_COVER
        } else {
            CoverLyricsType.OVER_COVER
        }

    var nowPlayingScreen: NowPlayingScreen
        get() {
            val id: Int = preference.getInt(NOW_PLAYING_SCREEN, 0)
            for (nowPlayingScreen in NowPlayingScreen.entries) {
                if (nowPlayingScreen.id == id) {
                    return nowPlayingScreen
                }
            }
            return NowPlayingScreen.Blur
        }
        set(value) = preference.edit { putInt(NOW_PLAYING_SCREEN, value.id) }
}