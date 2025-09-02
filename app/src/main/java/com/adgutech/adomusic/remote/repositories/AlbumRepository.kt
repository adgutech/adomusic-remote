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

import android.content.Context
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.AlbumTrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.SavedAlbum
import com.adgutech.adomusic.remote.api.spotify.models.Track
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutiérrez on 03/14/2025.
 */

interface AlbumRepository {
    fun getAlbum(albumId: String): Album
    fun getAlbumTracks(albumId: String): List<AlbumTrackParcelable>
    fun getAlbumsWithSortOrder(): List<AlbumParcelable>
    fun getMySavedAlbums(): List<AlbumParcelable>
}

class RealAlbumRepository(
    private val context: Context,
    private val spotifyService: SpotifyService
) : AlbumRepository {

    override fun getAlbum(albumId: String): Album {
        return spotifyService.getAlbum(albumId)
    }

    override fun getAlbumTracks(albumId: String): List<AlbumTrackParcelable> {
        val trackList = arrayListOf<AlbumTrackParcelable>()
        val albumTracks = spotifyService.getAlbumTracks(albumId, getBody())
        val tracks = albumTracks.items
        for (track in tracks) {
            trackList.add(getTrackFromAlbumImpl(track))
        }
        return trackList
    }

    override fun getAlbumsWithSortOrder(): List<AlbumParcelable> {
        val albums = getMySavedAlbums()
        return when (context.preference.albumSortOrder) {
            SortOrder.AlbumSortOrder.ALBUM_DEFAULT -> {
                albums.sortedBy { "" }
            }

            SortOrder.AlbumSortOrder.ALBUM_A_Z -> {
                albums.sortedBy { it.name }
            }

            SortOrder.AlbumSortOrder.ALBUM_Z_A -> {
                albums.sortedByDescending { it.name }
            }

            SortOrder.AlbumSortOrder.ALBUM_ARTIST -> {
                albums.sortedBy { it.artist }
            }

            SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC -> {
                albums.sortedByDescending { it.artist }
            }

            SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE -> {
                albums.sortedBy { it.releaseDate }
            }

            SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE_DESC -> {
                albums.sortedByDescending { it.releaseDate }
            }

            else -> albums
        }
    }

    override fun getMySavedAlbums(): List<AlbumParcelable> {
        val albumList = arrayListOf<AlbumParcelable>()
        val mySavedAlbums = spotifyService.getMySavedAlbums(getBody())
        val albums = mySavedAlbums.items
        for (album in albums) {
            albumList.add(getAlbumFromSavedAlbumImpl(album))
        }
        return albumList
    }

    private fun getTrackFromAlbumImpl(track: Track): AlbumTrackParcelable {
        val artistId = track.artists[0].id
        val artists = track.artists
        val discNumber = track.disc_number
        val duration = track.duration_ms
        val id = track.id
        val isExplicit = track.explicit
        val link = track.external_urls[SpotifyService.SPOTIFY_URL]
        val name = track.name
        val trackNumber = track.track_number
        val type = track.type
        val uri = track.uri

        return AlbumTrackParcelable(
            artistId,
            artists,
            discNumber,
            duration,
            id,
            isExplicit,
            link,
            name,
            trackNumber,
            type,
            uri
        )
    }

    private fun getAlbumFromSavedAlbumImpl(album: SavedAlbum): AlbumParcelable {
        val addedAt = album.added_at
        val albumType = album.album.album_type
        val artist = album.album.artists[0].name
        val artistId = album.album.artists[0].id
        val artists = album.album.artists
        val copyrights = album.album.copyrights[0].text
        val id = album.album.id
        val imageUrl = Utils.getImageUrl(album.album.images)
        val name = album.album.name
        val releaseDate = album.album.release_date
        val releaseDatePrecision = album.album.release_date_precision
        val trackTotal = album.album.tracks.total
        val type = album.album.type
        val uri = album.album.uri
        return AlbumParcelable(
            addedAt,
            albumType,
            artist,
            artistId,
            artists,
            copyrights,
            id,
            imageUrl,
            name,
            releaseDate,
            releaseDatePrecision,
            trackTotal,
            type,
            uri
        )
    }

    private fun getBody(): MutableMap<String, Any> {
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = 50
        return options
    }
}