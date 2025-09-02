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

import com.adgutech.adomusic.remote.TOP_20_PLAYED_LIMIT
import com.adgutech.adomusic.remote.TOP_50_PLAYED_LIMIT
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Track
import com.adgutech.adomusic.remote.preferences.Preferences
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutierrez on 03/07/25.
 */

interface TopPlayedRepository {
    fun getTopArtists(): List<ArtistParcelable>
    fun getTopTracks(): List<TrackParcelable>
}

class RealTopPlayedRepository(
    private val preference: Preferences,
    private val spotifyService: SpotifyService
) : TopPlayedRepository {

    override fun getTopArtists(): List<ArtistParcelable> {
        val artistList = arrayListOf<ArtistParcelable>()
        val topArtists = spotifyService
            .getTopArtists(getTopPlayedBody(TOP_20_PLAYED_LIMIT, preference.artistTimeRange))
        val artists = topArtists!!.items
        for (artist in artists) {
            val id = artist.id
            val name = artist.name
            val imageUrl = Utils.getImageUrl(artist.images)
            val followers = artist.followers.total
            val popularity = artist.popularity
            artistList.add(ArtistParcelable(id, name, imageUrl, followers, popularity))
        }
        return artistList
    }

    override fun getTopTracks(): List<TrackParcelable> {
        val topTracksList = arrayListOf<TrackParcelable>()
        val topTracks = spotifyService
            .getTopTracks(getTopPlayedBody(TOP_50_PLAYED_LIMIT, preference.trackTimeRange))
        val tracks = topTracks.items
        for (track in tracks) {
            topTracksList.add(getTracksFromTopTracksImpl(track))
        }
        return topTracksList
    }

    private fun getTopPlayedBody(limit: Int, timeRange: String): MutableMap<String, Any> {
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = limit
        options[SpotifyService.TIME_RANGE] = timeRange
        return options
    }

    private fun getTracksFromTopTracksImpl(track: Track): TrackParcelable {
        val album = track.album.name
        val albumId = track.album.id
        val artist = track.artists[0].name
        val artistId = track.artists[0].id
        val artists = track.artists
        val discNumber = track.disc_number
        val duration = track.duration_ms
        val id = track.id
        val imageUrl = Utils.getImageUrl(track.album.images)
        val isExplicit = track.explicit
        val link = track.album.external_urls[SpotifyService.SPOTIFY_URL]
        val name = track.name
        val trackNumber = track.track_number
        val type = track.type
        val uri = track.uri
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
            link!!,
            name,
            trackNumber,
            type,
            uri
        )
    }
}