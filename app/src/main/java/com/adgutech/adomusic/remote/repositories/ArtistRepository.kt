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
import com.adgutech.adomusic.remote.ALBUM
import com.adgutech.adomusic.remote.APPEARS_ON
import com.adgutech.adomusic.remote.COMPILATION
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SINGLE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.ArtistTrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.api.spotify.models.Track
import com.adgutech.adomusic.remote.ui.fragments.artists.AlbumTypeFilter
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutierrez on 03/13/25.
 */

interface ArtistRepository {
    fun getArtist(artistId: String): Artist
    fun getArtistAlbums(artistId: String): List<ArtistAlbumParcelable>
    fun getArtistTopTrack(artistId: String): List<ArtistTrackParcelable>
    fun getArtistsWithSortOrder(): List<ArtistParcelable>
    fun getFollowedArtists(): List<ArtistParcelable>
    fun getAlbumTypes(artistId: String, albumType: String): List<ArtistAlbumParcelable>
}

class RealArtistRepository(
    private val context: Context,
    private val spotifyService: SpotifyService
) : ArtistRepository {

    override fun getArtist(artistId: String): Artist {
        return spotifyService.getArtist(artistId)
    }

    override fun getArtistAlbums(artistId: String): List<ArtistAlbumParcelable> {
        val albumList = arrayListOf<ArtistAlbumParcelable>()
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = 5
        val artistAlbums = spotifyService.getArtistAlbums(artistId, options)
        val albums = artistAlbums.items
        for (album in albums) {
            albumList.add(getAlbumFromArtistAlbumImpl(album))
        }
        return albumList
    }

    override fun getArtistTopTrack(artistId: String): List<ArtistTrackParcelable> {
        val trackList = arrayListOf<ArtistTrackParcelable>()
        val artistTopTrack = spotifyService.getArtistTopTrack(artistId)
        val tracks = artistTopTrack.tracks
        for (track in tracks) {
            trackList.add(getTrackFromArtistTopTrackImpl(track))
        }
        return trackList
    }

    override fun getArtistsWithSortOrder(): List<ArtistParcelable> {
        val artists = getFollowedArtists()
        return when (context.preference.artistSortOrder) {
            SortOrder.ArtistSortOrder.ARTIST_DEFAULT -> {
                artists.sortedBy { "" }
            }
            SortOrder.ArtistSortOrder.ARTIST_A_Z -> {
                artists.sortedBy { it.name }
            }
            SortOrder.ArtistSortOrder.ARTIST_Z_A -> {
                artists.sortedByDescending { it.name }
            }
            SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED -> {
                artists.sortedByDescending { it.followers }
            }
            SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED_DESC -> {
                artists.sortedBy { it.followers }
            }
            else -> artists
        }
    }

    override fun getFollowedArtists(): List<ArtistParcelable> {
        val artistList = arrayListOf<ArtistParcelable>()
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = 50
        val followedArtists = spotifyService.getFollowedArtists(options)
        val artists = followedArtists.artists.items
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

    override fun getAlbumTypes(artistId: String, albumType: String): List<ArtistAlbumParcelable> {
        val albumList = arrayListOf<ArtistAlbumParcelable>()
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.INCLUDE_GROUPS] = albumType
        options[SpotifyService.LIMIT] = 50
        val artistAlbums = spotifyService.getArtistAlbums(artistId, options)
        val albums = artistAlbums.items
        for (album in albums) {
            albumList.add(getAlbumFromArtistAlbumImpl(album))
        }
        return albumList
    }

    fun getArtistAlbumsTypes(artistId: String, albumType: AlbumTypeFilter): MutableList<Any> {
        val albumTypeList = mutableListOf<Any>()

        /** Album **/
        val album: List<ArtistAlbumParcelable> =
            if (albumType == AlbumTypeFilter.ALBUM || albumType == AlbumTypeFilter.NO_FILTER) {
                getAlbumTypes(artistId, ALBUM)
            } else {
                emptyList()
            }
        if (album.isNotEmpty()) {
            albumTypeList.add(context.resources.getString(R.string.album_type_album))
            albumTypeList.addAll(album)
        }

        /** Single **/
        val single: List<ArtistAlbumParcelable> =
            if (albumType == AlbumTypeFilter.SINGLE || albumType == AlbumTypeFilter.NO_FILTER) {
                getAlbumTypes(artistId, SINGLE)
            } else {
                emptyList()
            }
        if (single.isNotEmpty()) {
            albumTypeList.add(context.resources.getString(R.string.album_type_single))
            albumTypeList.addAll(single)
        }

        /** Compilation **/
        val compilation: List<ArtistAlbumParcelable> =
            if (albumType == AlbumTypeFilter.COMPILATION || albumType == AlbumTypeFilter.NO_FILTER) {
                getAlbumTypes(artistId, COMPILATION)
            } else {
                emptyList()
            }
        if (compilation.isNotEmpty()) {
            albumTypeList.add(context.resources.getString(R.string.album_type_compilation))
            albumTypeList.addAll(compilation)
        }

        /** Appears on **/
        val appearsOn: List<ArtistAlbumParcelable> =
            if (albumType == AlbumTypeFilter.APPEARS_ON || albumType == AlbumTypeFilter.NO_FILTER) {
                getAlbumTypes(artistId, APPEARS_ON)
            } else {
                emptyList()
            }
        if (appearsOn.isNotEmpty()) {
            albumTypeList.add(context.resources.getString(R.string.album_type_appears_on))
            albumTypeList.addAll(appearsOn)
        }

        return albumTypeList
    }

    private fun getAlbumFromArtistAlbumImpl(album: Album): ArtistAlbumParcelable {
        val albumType = album.album_type
        val artist = album.artists[0].name
        val artistId = album.artists[0].id
        val artists = album.artists
        val id = album.id
        val imageUrl = Utils.getImageUrl(album.images)
        val name = album.name
        val releaseDate = album.release_date
        val type = album.type
        val uri = album.uri
        return ArtistAlbumParcelable(
            albumType,
            artist,
            artistId,
            artists,
            id,
            imageUrl,
            name,
            releaseDate,
            type,
            uri
        )
    }

    private fun getTrackFromArtistTopTrackImpl(track: Track): ArtistTrackParcelable {
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
        val link = track.external_ids[SpotifyService.SPOTIFY_URL]
        val name = track.name
        val trackNumber = track.track_number
        val type = track.type
        val uri = track.uri
        return ArtistTrackParcelable(
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