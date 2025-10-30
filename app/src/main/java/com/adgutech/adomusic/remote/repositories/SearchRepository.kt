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
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SEARCH_LIMIT
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.AlbumSimple
import com.adgutech.adomusic.remote.api.spotify.models.PlaylistSimple
import com.adgutech.adomusic.remote.api.spotify.models.PlaylistTracksInformation
import com.adgutech.adomusic.remote.api.spotify.models.Track
import com.adgutech.adomusic.remote.api.spotify.models.UserPublic
import com.adgutech.adomusic.remote.ui.fragments.search.SearchFilter
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutierrez on 05/31/2025.
 */

interface SearchRepository {
    fun getSearchAlbums(query: String): List<AlbumParcelable>
    fun getSearchArtists(query: String): List<ArtistParcelable>
    fun getSearchPlaylists(query: String): List<PlaylistParcelable>
    fun getSearchTracks(query: String): List<TrackParcelable>
}

class RealSearchRepository(
    private val spotifyService: SpotifyService
) : SearchRepository {

    override fun getSearchAlbums(query: String): List<AlbumParcelable> {
        val albumList = arrayListOf<AlbumParcelable>()
        val searchAlbums = spotifyService.searchAlbums(query, getSearchLimit())
        val albums = searchAlbums.albums.items
        for (album in albums) {
            albumList.add(getAlbumFromSavedAlbumImpl(album))
        }
        return albumList
    }

    override fun getSearchArtists(query: String): List<ArtistParcelable> {
        val artistList = arrayListOf<ArtistParcelable>()
        val searchArtists = spotifyService.searchArtists(query, getSearchLimit())
        val artists = searchArtists.artists.items
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

    override fun getSearchPlaylists(query: String): List<PlaylistParcelable> {
        val playlistList = arrayListOf<PlaylistParcelable>()
        val searchPlaylists = spotifyService.searchPlaylists(query, getSearchLimit())
        val playlists = searchPlaylists.playlists.items
        for (playlist in playlists) {
            if (playlist != null) {
                if (
                    playlist.id != null ||
                    playlist.uri != null ||
                    playlist.name != null ||
                    playlist.images != null ||
                    playlist.owner != null ||
                    playlist.owner.id != null ||
                    playlist.owner.display_name != null ||
                    playlist.snapshot_id != null ||
                    playlist.tracks != null ||
                    playlist.tracks.total == 0
                ) {
                    playlistList.add(
                        getPlaylistFromPlaylistSimpleImpl(playlist)
                    )
                }
            }
        }
        return playlistList
    }

    override fun getSearchTracks(query: String): List<TrackParcelable> {
        val trackList = arrayListOf<TrackParcelable>()
        val searchTracks = spotifyService.searchTracks(query, getSearchLimit())
        val tracks = searchTracks.tracks.items
        for (track in tracks) {
            trackList.add(getTracksFromSearchTracksImpl(track))
        }
        return trackList
    }

    fun searchAll(
        context: Context,
        query: String?,
        searchFilter: SearchFilter
    ): MutableList<Any> {
        val results = mutableListOf<Any>()
        if (query.isNullOrEmpty()) return results
        query.let { searchString ->

            /** Tracks **/
            val tracks: List<TrackParcelable> =
                if (searchFilter == SearchFilter.TRACKS || searchFilter == SearchFilter.NO_FILTER) {
                    getSearchTracks(searchString)
                } else {
                    emptyList()
                }
            if (tracks.isNotEmpty()) {
                results.add(context.resources.getString(R.string.title_songs))
                results.addAll(tracks)
            }

            /** Albums **/
            val albums: List<AlbumParcelable> =
                if (searchFilter == SearchFilter.ALBUMS || searchFilter == SearchFilter.NO_FILTER) {
                    getSearchAlbums(searchString)
                } else {
                    emptyList()
                }
            if (albums.isNotEmpty()) {
                results.add(context.resources.getString(R.string.title_albums))
                results.addAll(albums)
            }

            /** Artists **/
            val artists: List<ArtistParcelable> =
                if (searchFilter == SearchFilter.ARTISTS || searchFilter == SearchFilter.NO_FILTER) {
                    getSearchArtists(searchString)
                } else {
                    emptyList()
                }
            if (artists.isNotEmpty()) {
                results.add(context.resources.getString(R.string.title_artists))
                results.addAll(artists)
            }

            /** Playlists **/
            val playlists: List<PlaylistParcelable> =
                if (searchFilter == SearchFilter.PLAYLISTS || searchFilter == SearchFilter.NO_FILTER) {
                    getSearchPlaylists(searchString)
                } else {
                    emptyList()
                }
            if (playlists.isNotEmpty()) {
                results.add(context.resources.getString(R.string.title_playlists))
                results.addAll(playlists)
            }
        }
        return results
    }

    private fun getAlbumFromSavedAlbumImpl(album: AlbumSimple): AlbumParcelable {

        val albumType = album.album_type
        val id = album.id
        val imageUrl = Utils.getImageUrl(album.images)
        val name = album.name
        val type = album.type
        val uri = album.uri
        return AlbumParcelable(
            "",
            albumType,
            "",
            "",
            listOf(),
            "",
            id,
            imageUrl,
            name,
            "",
            "",
            -1,
            type,
            uri
        )
    }

    private fun getPlaylistFromPlaylistSimpleImpl(playlist: PlaylistSimple): PlaylistParcelable {
        val isCollaborative = playlist.collaborative
        val id = playlist.id
        val uri = playlist.uri
        val title = playlist.name
        val images = playlist.images
        val isPublic = playlist.is_public
        val link = playlist.external_urls[SpotifyService.SPOTIFY_URL]
        val owner = playlist.owner ?: UserPublic()
        val userId = owner.id
        val displayName = owner.display_name
        val snapshotId = playlist.snapshot_id
        val trackInfo = playlist.tracks ?: PlaylistTracksInformation()
        val trackTotal = trackInfo.total
        val type = playlist.type
        return PlaylistParcelable(
            isCollaborative,
            id,
            userId,
            uri,
            title,
            displayName,
            images,
            link,
            trackTotal,
            isPublic,
            snapshotId,
            type
        )
    }

    private fun getTracksFromSearchTracksImpl(track: Track): TrackParcelable {
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

    private fun getSearchLimit(): MutableMap<String, Any> {
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = SEARCH_LIMIT
        return options
    }
}