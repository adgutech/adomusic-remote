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
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.models.PlaylistTrackParcelable
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.api.spotify.models.PlaylistSimple
import com.adgutech.adomusic.remote.api.spotify.models.PlaylistTrack
import com.adgutech.adomusic.remote.utils.Utils

/**
 * Created by Adolfo Gutiérrez on 04/27/25.
 */

interface PlaylistRepository {
    fun getMyPlaylists(): List<PlaylistParcelable>
    fun getPlaylist(userId: String, playlistId: String): Playlist
    fun getPlaylists(userId: String): List<PlaylistParcelable>
    fun getPlaylistTracks(userId: String, playlistId: String): List<PlaylistTrackParcelable>
    fun getPlaylistsWithSortOrder(): List<PlaylistParcelable>
}

class RealPlaylistRepository(
    private val context: Context,
    private val spotifyService: SpotifyService
) : PlaylistRepository {

    override fun getMyPlaylists(): List<PlaylistParcelable> {
        val playlistList = arrayListOf<PlaylistParcelable>()
        val myPlaylists = spotifyService.getMyPlaylists(getBody())
        val playlists = myPlaylists.items
        for (playlist in playlists) {
            playlistList.add(getPlaylistFromPlaylistSimpleImpl(playlist))
        }
        return playlistList
    }

    override fun getPlaylist(userId: String, playlistId: String): Playlist {
        return spotifyService.getPlaylist(playlistId)
    }

    override fun getPlaylists(userId: String): List<PlaylistParcelable> {
        val playlistList = arrayListOf<PlaylistParcelable>()
        val playlists = spotifyService.getPlaylists(userId)
        val playlistsItems = playlists.items
        for (playlist in playlistsItems) {
            playlistList.add(getPlaylistFromPlaylistSimpleImpl(playlist))
        }
        return playlistList
    }

    override fun getPlaylistTracks(userId: String, playlistId: String): List<PlaylistTrackParcelable> {
        val trackList = arrayListOf<PlaylistTrackParcelable>()
        val playlistTracks = spotifyService.getPlaylistTracks(userId, playlistId,  getBody())
        val tracks = playlistTracks.items
        for (track in tracks) {
            trackList.add(getTracksFromPlaylistTracksImpl(track))
        }
        return trackList
    }

    override fun getPlaylistsWithSortOrder(): List<PlaylistParcelable> {
        val playlists = getMyPlaylists()
        return when (context.preference.playlistSortOrder) {
            SortOrder.PlaylistSortOrder.PLAYLIST_DEFAULT -> {
                playlists.sortedBy { "" }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_A_Z -> {
                playlists.sortedBy { it.title }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_Z_A -> {
                playlists.sortedByDescending { it.title }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME -> {
                playlists.sortedBy { it.displayName }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME_DESC -> {
                playlists.sortedByDescending { it.displayName }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT -> {
                playlists.sortedBy { it.trackTotal }
            }
            SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT_DESC -> {
                playlists.sortedByDescending { it.trackTotal }
            }
            else -> playlists
        }
    }

    private fun getPlaylistFromPlaylistSimpleImpl(playlist: PlaylistSimple): PlaylistParcelable {
        val isCollaborative = playlist.collaborative
        val id = playlist.id
        val userId = playlist.owner.id
        val uri = playlist.uri
        val title = playlist.name
        val displayName = playlist.owner.display_name
        val images = playlist.images ?: listOf()
        val link = playlist.external_urls[SpotifyService.SPOTIFY_URL]
        val trackTotal = playlist.tracks.total
        val isPublic = playlist.is_public
        val snapshotId = playlist.snapshot_id
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

    private fun getTracksFromPlaylistTracksImpl(track: PlaylistTrack): PlaylistTrackParcelable {
        val addedAt = track.added_at
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
        val link = track.track.external_ids[SpotifyService.SPOTIFY_URL]
        val name = track.track.name
        val trackNumber = track.track.track_number
        val type = track.track.type
        val uri = track.track.uri
        return PlaylistTrackParcelable(
            addedAt,
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

    private fun getBody(): MutableMap<String, Any> {
        val options: MutableMap<String, Any> = HashMap()
        options[SpotifyService.LIMIT] = 50
        return options
    }
}