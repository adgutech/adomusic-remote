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

package com.adgutech.adomusic.remote.repositories

import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.SavedTrack
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutierrez on 03/13/25.
 */

interface TrackRepository {
    fun getMySavedTracks(): List<TrackParcelable>
}

class RealTrackRepository(
    private val spotifyService: SpotifyService
) : TrackRepository {

    override fun getMySavedTracks(): List<TrackParcelable> {
        val trackList = arrayListOf<TrackParcelable>()
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = 50
        val mySavedTrack = spotifyService.getMySavedTracks(options)
        val tracks = mySavedTrack.items
        for (track in tracks) {
            trackList.add(getTracksFromMySavedTracksImpl(track))
        }
        return trackList
    }

    private fun getTracksFromMySavedTracksImpl(track: SavedTrack): TrackParcelable {
        //val addedAt = track.added_at
        val album = track.track.album.name
        val albumId = track.track.album.id
        val artist = track.track.artists[0].name
        val artistId = track.track.artists[0].id
        val artists = track.track.artists
        val discNumber = track.track.disc_number
        val duration = track.track.duration_ms
        val id = track.track.id
        val imageUrl = Utils.getImageUrl(track.track.album.images)
        val isExplicit = track.track.explicit
        val link = track.track.album.external_urls[SpotifyService.SPOTIFY_URL]!!
        val name = track.track.name
        val trackNumber = track.track.track_number
        val type = track.track.type
        val uri = track.track.uri
        return TrackParcelable(
            album,
            albumId,
            artist,
            artistId,
            artists,
            discNumber,
            duration,
            id,
            imageUrl,
            isExplicit,
            link,
            name,
            trackNumber,
            type,
            uri
        )
    }
}