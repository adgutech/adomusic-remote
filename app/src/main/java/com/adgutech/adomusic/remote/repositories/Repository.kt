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
import com.adgutech.adomusic.remote.TOP_PLAYED_HOME_LIMIT
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.logV
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.AlbumTrackParcelable
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.ArtistTrackParcelable
import com.adgutech.adomusic.remote.models.Home
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.models.PlaylistTrackParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.models.TrackQueue
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.api.spotify.models.Pager
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.api.spotify.models.PlaylistTrack
import com.adgutech.adomusic.remote.api.spotify.models.UserPrivate
import com.adgutech.adomusic.remote.ui.fragments.artists.AlbumTypeFilter
import com.adgutech.adomusic.remote.ui.fragments.home.LIKED_SONGS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_ARTISTS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_TRACKS
import com.adgutech.adomusic.remote.ui.fragments.search.SearchFilter
import com.adgutech.commons.extensions.showToast
import com.google.gson.GsonBuilder
import okhttp3.RequestBody
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by Adolfo Gutierrez on 03/06/25.
 */

interface Repository {
    suspend fun addToMySavedAlbums(albumId: String)
    suspend fun addToQueue(uri: String, name: String)
    suspend fun addTrackToPlaylist(playlistId: String, name: String, uris: List<String>)
    suspend fun containsMySavedAlbums(albumId: String): Result<Array<Boolean>>
    suspend fun changePlaylistDetails(playlistId: String, name: String)
    suspend fun changePlaylistDetails(playlistId: String, name: String, description: String)
    suspend fun isFollowingArtists(artistId: String): Result<Array<Boolean>>
    suspend fun createPlaylist(userId: String, name: String)
    suspend fun followArtists(artistId: String)
    suspend fun followPlaylist(name: String, playlistId: String)
    suspend fun getAlbumTracks(albumId: String): Result<List<AlbumTrackParcelable>>
    suspend fun getArtistAlbums(artistId: String): Result<List<ArtistAlbumParcelable>>
    suspend fun getArtistTopTracks(artistId: String): Result<List<ArtistTrackParcelable>>
    suspend fun getArtistAlbumsTypes(artistId: String, albumType: AlbumTypeFilter): Result<MutableList<Any>>
    suspend fun getFollowedArtists(): Result<List<ArtistParcelable>>
    suspend fun getLikedSongs(): Home
    suspend fun getMyPlaylists(): Result<List<PlaylistParcelable>>
    suspend fun getMyPlaylistsForDialog(): List<PlaylistParcelable>
    suspend fun getPlaylists(userId: String): List<PlaylistParcelable>
    suspend fun getMySavedAlbums(): Result<List<AlbumParcelable>>
    suspend fun getMySavedTracks(): Result<List<TrackParcelable>>
    suspend fun getMe(): Result<UserPrivate>
    suspend fun getPlaylist(userId: String, playlistId: String): Result<Playlist>
    suspend fun getPlaylistTracks(userId: String, playlistId: String): Result<List<PlaylistTrackParcelable>>
    suspend fun getTopArtists(): Result<List<ArtistParcelable>>
    suspend fun getTopTracks(): Result<List<TrackParcelable>>
    suspend fun getAlbumDetails(albumId: String): Result<Album>
    suspend fun getArtistDetails(artistId: String): Result<Artist>
    suspend fun getTopArtistsHome(): Home
    suspend fun getTopTracksHome(): Home
    suspend fun getSearch(query: String?, searchFilter: SearchFilter): Result<MutableList<Any>>
    suspend fun getUserQueue(): Result<List<TrackQueue>>
    suspend fun homeSections(): List<Home>
    suspend fun removeFromMySavedAlbums(albumId: String)
    suspend fun unfollowArtists(artistId: String)
    suspend fun unfollowPlaylist(name: String, playlistId: String)
    suspend fun uploadImageToPlaylist(playlistId: String, contentType: String, image: RequestBody)
}

class RealRepository(
    private val context: Context,
    private val spotifyService: SpotifyService,
    private val albumsRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val realArtistRepository: RealArtistRepository,
    private val playerRepository: PlayerRepository,
    private val playlistRepository: PlaylistRepository,
    private val realSearchRepository: RealSearchRepository,
    private val topPlayedRepository: TopPlayedRepository,
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) : Repository {

    override suspend fun addTrackToPlaylist(playlistId: String, name: String, uris: List<String>) {
        val options: MutableMap<String, Any> = HashMap()
        options["uris"] = uris
        options["position"] = 0
        spotifyService.addTracksToPlaylist(
            playlistId,
            null,
            options,
            object : Callback<Pager<PlaylistTrack>> {
                override fun success(t: Pager<PlaylistTrack>?, response: Response?) {
                    context.showToast(
                        context.getString(
                            R.string.text_added_track_count_to_playlist, uris.size, name
                        )
                    )
                    logD("Tracks added to $name playlist: ${response?.body}")
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to add track to playlist: $error")
                }
            })
    }

    override suspend fun addToMySavedAlbums(albumId: String) {
        spotifyService.addToMySavedAlbums(albumId, object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("Album added to library: ${response?.body}")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error adding album to library: $error")
            }
        })
    }

    override suspend fun addToQueue(uri: String, name: String) {
        spotifyService.addToQueue(uri, "", object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("$name added to queue: ${response?.body}")
                context.showToast(
                    String.format(context.getString(R.string.text_added_to_queue), name)
                )
            }

            override fun failure(error: RetrofitError?) {
                logE("Error adding to queue: $error")
            }
        })
    }

    override suspend fun containsMySavedAlbums(albumId: String): Result<Array<Boolean>> {
        return try {
            Success(spotifyService.containsMySavedAlbums(albumId))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun changePlaylistDetails(playlistId: String, name: String) {
        val option: MutableMap<String, Any> = HashMap()
        option["name"] = name
        spotifyService.changePlaylistDetails(playlistId, option,
            object : Callback<com.adgutech.adomusic.remote.api.spotify.models.Result> {
                override fun success(
                    t: com.adgutech.adomusic.remote.api.spotify.models.Result?,
                    response: Response?
                ) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val body = gson.toJson(option)
                    logV("the $name playlist edited successfully: $body}")
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to edit playlist: $error")
                }
            })
    }

    override suspend fun changePlaylistDetails(
        playlistId: String,
        name: String,
        description: String
    ) {
        val option: MutableMap<String, Any> = HashMap()
        option["name"] = name
        option["description"] = description
        spotifyService.changePlaylistDetails(playlistId, option,
            object : Callback<com.adgutech.adomusic.remote.api.spotify.models.Result> {
                override fun success(
                    t: com.adgutech.adomusic.remote.api.spotify.models.Result?,
                    response: Response?
                ) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val body = gson.toJson(option)
                    logV("the $name playlist edited successfully: $body")
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to edit playlist: $error")
                }
            })
    }

    override suspend fun isFollowingArtists(artistId: String): Result<Array<Boolean>> {
        return try {
            Success(spotifyService.isFollowingArtists(artistId))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun createPlaylist(userId: String, name: String) {
        val option: MutableMap<String, Any> = HashMap()
        option["name"] = name
        option["description"] = ""
        spotifyService.createPlaylist(userId, option, object : Callback<Playlist> {
            override fun success(t: Playlist?, response: Response?) {
                context.showToast(String.format(context.getString(R.string.text_playlist_created), name))
                val gson = GsonBuilder().setPrettyPrinting().create()
                val body = gson.toJson(option)
                logD("the $name playlist created successfully: $body")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to create playlist: $error")
            }
        })
    }

    override suspend fun followArtists(artistId: String) {
        spotifyService.followArtists(artistId, object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("Artist followed: ${response?.body}")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to follow artist: $error")
            }
        })
    }

    override suspend fun followPlaylist(name: String, playlistId: String) {
        spotifyService.followPlaylist(
            playlistId,
            object : Callback<com.adgutech.adomusic.remote.api.spotify.models.Result> {
                override fun success(
                    t: com.adgutech.adomusic.remote.api.spotify.models.Result?,
                    response: Response?
                ) {
                    context.showToast(
                        String.format(
                            context.getString(R.string.text_playlist_added_to_library),
                            name
                        )
                    )
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to add playlist to library: $error")
                }
            })
    }

    override suspend fun getAlbumTracks(albumId: String): Result<List<AlbumTrackParcelable>> {
        return try {
            Success(albumsRepository.getAlbumTracks(albumId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getArtistAlbums(artistId: String): Result<List<ArtistAlbumParcelable>> {
        return try {
            Success(artistRepository.getArtistAlbums(artistId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getArtistTopTracks(artistId: String): Result<List<ArtistTrackParcelable>> {
        return try {
            Success(artistRepository.getArtistTopTrack(artistId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getArtistAlbumsTypes(
        artistId: String,
        albumType: AlbumTypeFilter
    ): Result<MutableList<Any>> {
        return try {
            Success(realArtistRepository.getArtistAlbumsTypes(artistId, albumType))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getFollowedArtists(): Result<List<ArtistParcelable>> {
        return try {
            Success(artistRepository.getArtistsWithSortOrder())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getLikedSongs(): Home {
        val tracks = try {
            Success(trackRepository.getMySavedTracks().take(TOP_PLAYED_HOME_LIMIT))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
        return Home(tracks, LIKED_SONGS, R.string.title_liked_songs)
    }

    override suspend fun getMyPlaylists(): Result<List<PlaylistParcelable>> {
        return try {
            Success(playlistRepository.getPlaylistsWithSortOrder())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getMyPlaylistsForDialog(): List<PlaylistParcelable> {
        val playlistList = arrayListOf<PlaylistParcelable>()
        when (val result = getMyPlaylists()) {
            is Loading -> {}
            is Success -> playlistList.addAll(result.data)
            is Error -> {}
        }
        playlistList.sortBy { it.title }
        return playlistList
    }

    override suspend fun getPlaylists(userId: String): List<PlaylistParcelable> {
        return playlistRepository.getPlaylists(userId)
    }

    override suspend fun getMySavedAlbums(): Result<List<AlbumParcelable>> {
        return try {
            Success(albumsRepository.getAlbumsWithSortOrder())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getMySavedTracks(): Result<List<TrackParcelable>> {
        return try {
            Success(trackRepository.getMySavedTracks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getMe(): Result<UserPrivate> {
        return try {
            Success(userRepository.getMe())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getPlaylist(userId: String, playlistId: String): Result<Playlist> {
        return try {
            Success(playlistRepository.getPlaylist(userId, playlistId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getPlaylistTracks(
        userId: String,
        playlistId: String
    ): Result<List<PlaylistTrackParcelable>> {
        return try {
            val playlistTracks = playlistRepository.getPlaylistTracks(userId, playlistId)
            Success(playlistTracks)
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTopArtists(): Result<List<ArtistParcelable>> {
        return try {
            Success(topPlayedRepository.getTopArtists())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTopTracks(): Result<List<TrackParcelable>> {
        return try {
            Success(topPlayedRepository.getTopTracks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getAlbumDetails(albumId: String): Result<Album> {
        return try {
            Success(albumsRepository.getAlbum(albumId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getArtistDetails(artistId: String): Result<Artist> {
        return try {
            Success(artistRepository.getArtist(artistId))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTopArtistsHome(): Home {
        val artists = try {
            Success(topPlayedRepository.getTopArtists().take(TOP_PLAYED_HOME_LIMIT))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
        return Home(artists, TOP_ARTISTS, R.string.title_top_artists)
    }

    override suspend fun getTopTracksHome(): Home {
        val tracks = try {
            Success(topPlayedRepository.getTopTracks().take(TOP_PLAYED_HOME_LIMIT))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
        return Home(tracks, TOP_TRACKS, R.string.title_top_tracks)
    }

    override suspend fun getSearch(query: String?, searchFilter: SearchFilter): Result<MutableList<Any>> {
        return try {
            Success(realSearchRepository.searchAll(context, query, searchFilter))
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getUserQueue(): Result<List<TrackQueue>> {
        return try {
            Success(playerRepository.getUserQueue())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun homeSections(): List<Home> {
        val homeSections = mutableListOf<Home>()
        val sections: List<Home> = listOf(
            getLikedSongs(),
            getTopArtistsHome(),
            getTopTracksHome()
        )
        for (section in sections) {
            when (val result = section.arrayList) {
                is Loading -> {}
                is Success -> {
                    if (result.data.isNotEmpty()) {
                        homeSections.add(section)
                    }
                }
                is Error -> {}
            }
        }
        return homeSections
    }

    override suspend fun removeFromMySavedAlbums(albumId: String) {
        spotifyService.removeFromMySavedAlbums(albumId, object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("Album removed from library: ${response?.body}")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error removing album from library: $error")
            }
        })
    }

    override suspend fun unfollowArtists(artistId: String) {
        spotifyService.unfollowArtists(artistId, object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("Artist unfollowed: ${response?.body}")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to unfollow artist: $error")
            }
        })
    }

    override suspend fun unfollowPlaylist(name: String, playlistId: String) {
        spotifyService.unfollowPlaylist(playlistId,
            object : Callback<com.adgutech.adomusic.remote.api.spotify.models.Result> {
                override fun success(
                    t: com.adgutech.adomusic.remote.api.spotify.models.Result?,
                    response: Response?
                ) {
                    context.showToast(String.format(context.getString(R.string.text_you_unfollowed), name))
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to unfollow playlist: $error")
                }
            })
    }

    override suspend fun uploadImageToPlaylist(playlistId: String, contentType: String, image: RequestBody) {
        spotifyService.uploadImageToPlaylist(playlistId, contentType, image, object : Callback<Any> {
            override fun success(t: Any?, response: Response?) {
                logD("Image uploaded to playlist successfully. data: $contentType")
            }

            override fun failure(error: RetrofitError?) {
                val response = error?.response!!
                when (response.status) {
                    400 -> logE("400 Error upload image to playlist: $error")
                    401 -> logE("401 Error upload image to playlist: $error")
                }
                logE("Error upload image to playlist: $error")
            }
        })
    }
}