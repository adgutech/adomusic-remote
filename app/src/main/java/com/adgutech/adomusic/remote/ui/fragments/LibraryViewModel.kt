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

package com.adgutech.adomusic.remote.ui.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.animation.doOnEnd
import androidx.lifecycle.*
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.Home
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.models.TrackQueue
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.spotify.SpotifyApi
import com.adgutech.adomusic.remote.api.spotify.models.UserPrivate
import com.adgutech.adomusic.remote.preferences.Preferences
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.fragments.ReloadType.*
import com.adgutech.adomusic.remote.ui.fragments.search.SearchFilter
import com.adgutech.commons.utils.DensityUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * Created by Adolfo Gutiérrez on 03/06/2025.
 */

class LibraryViewModel(
    private val realRepository: RealRepository,
    preference: Preferences,
    spotifyApi: SpotifyApi
) : ViewModel(), OnSpotifyServiceEventListener {

    private val _paletteColor = MutableLiveData<Int>()
    private val albums = MutableLiveData<Result<List<AlbumParcelable>>>()
    private val artists = MutableLiveData<Result<List<ArtistParcelable>>>()
    private val home = MutableLiveData<List<Home>>()
    private val playlists = MutableLiveData<Result<List<PlaylistParcelable>>>()
    private val searchResults = MutableLiveData<Result<List<Any>>>()
    private val userQueue = MutableLiveData<Result<List<TrackQueue>>>()
    private val fabMargin = MutableLiveData(0)
    val paletteColor: LiveData<Int> = _paletteColor

    private val handler: Handler = Handler(Looper.getMainLooper())

    init {
        if (preference.accessToken == spotifyApi.accessToken) {
            loadLibraryContent()
        }
    }

    override fun onServiceConnected() {
        logD("onServiceConnected")
    }

    override fun onServiceDisconnected() {
        logD("onServiceDisconnected")
    }

    override fun onPlayerStateChanged() {
        logD("onPlayerStateChanged")
    }

    fun loadLibraryContent() {
        viewModelScope.launch(IO) {
            fetchHomeSections()
            awaitAll(
                async { fetchArtists() },
                async { fetchAlbums() },
                async { fetchPlaylists() }
            )
        }
    }

    fun addTrackToPlaylist(playlistId: String, playlistName: String, trackUris: List<String>) {
        viewModelScope.launch(IO) {
            realRepository.addTrackToPlaylist(playlistId, playlistName, trackUris)
        }
    }

    fun addToQueue(uri: String, name: String) = viewModelScope.launch(IO) {
        realRepository.addToQueue(uri, name)
    }

    fun changePlaylistDetails(
        playlistId: String,
        name: String
    ) = viewModelScope.launch(IO) {
        realRepository.changePlaylistDetails(playlistId, name)
        forceReload(PLAYLISTS)
    }

    fun changePlaylistDetails(
        playlistId: String,
        name: String,
        description: String
    ) = viewModelScope.launch(IO) {
        realRepository.changePlaylistDetails(playlistId, name, description)
        forceReload(PLAYLISTS)
    }

    fun createPlaylist(userId: String, name: String) = viewModelScope.launch(IO) {
        realRepository.createPlaylist(userId, name)
        forceReload(PLAYLISTS)
    }

    fun getArtists(): LiveData<Result<List<ArtistParcelable>>> = artists

    fun getMe(): LiveData<Result<UserPrivate>> = liveData(IO) {
        emit(Result.Loading)
        emit(realRepository.getMe())
    }

    fun getPlaylists(): LiveData<Result<List<PlaylistParcelable>>> = playlists

    fun getAlbums(): LiveData<Result<List<AlbumParcelable>>> = albums

    fun getMySavedTracks(): LiveData<Result<List<TrackParcelable>>> = liveData(IO) {
        emit(Result.Loading)
        emit(realRepository.getMySavedTracks())
    }

    fun getTopArtists(): LiveData<Result<List<ArtistParcelable>>> = liveData(IO) {
        emit(Result.Loading)
        emit(realRepository.getTopArtists())
    }

    fun getTopTracks(): LiveData<Result<List<TrackParcelable>>> = liveData(IO) {
        emit(Result.Loading)
        emit(realRepository.getTopTracks())
    }

    fun getUserQueue(): MutableLiveData<Result<List<TrackQueue>>> = userQueue

    fun getHome(): LiveData<List<Home>> = home

    fun getSearchResults(): LiveData<Result<List<Any>>> = searchResults

    fun getFabMargin(): LiveData<Int> = fabMargin

    private suspend fun fetchAlbums() {
        albums.postValue(Result.Loading)
        albums.postValue(realRepository.getMySavedAlbums())
        handler.postDelayed({
            viewModelScope.launch(IO) {
                albums.postValue(Result.Loading)
                albums.postValue(realRepository.getMySavedAlbums())
            }
        }, 300)
    }

    private suspend fun fetchArtists() {
        artists.postValue(Result.Loading)
        artists.postValue(realRepository.getFollowedArtists())
        handler.postDelayed({
            viewModelScope.launch(IO) {
                artists.postValue(Result.Loading)
                artists.postValue(realRepository.getFollowedArtists())
            }
        }, 300)
    }

    private suspend fun fetchHomeSections() {
        home.postValue(realRepository.homeSections())
    }

    private suspend fun fetchPlaylists() {
        playlists.postValue(Result.Loading)
        playlists.postValue(realRepository.getMyPlaylists())
        handler.postDelayed({
            viewModelScope.launch(IO) {
                playlists.postValue(Result.Loading)
                playlists.postValue(realRepository.getMyPlaylists())
            }
        }, 300)
    }

     fun fetchUserQueue() {
        viewModelScope.launch(IO) {
            userQueue.postValue(Result.Loading)
            userQueue.postValue(realRepository.getUserQueue())
        }
    }

    fun setFabMargin(context: Context, bottomMargin: Int) {
        val currentValue = DensityUtil.dip2px(context, 16F) +
                bottomMargin
        ValueAnimator.ofInt(fabMargin.value!!, currentValue).apply {
            addUpdateListener {
                fabMargin.postValue(
                    (it.animatedValue as Int)
                )
            }
            doOnEnd {
                fabMargin.postValue(currentValue)
            }
            start()
        }
    }

    fun search(query: String?, searchFilter: SearchFilter) =
        viewModelScope.launch(IO) {
            searchResults.postValue(Result.Loading)
            searchResults.postValue(realRepository.getSearch(query, searchFilter))
        }

    fun clearSearchResults() {
        searchResults.value = Result.Loading
    }

    fun forceReload(type: ReloadType) = viewModelScope.launch(IO) {
        when (type) {
            ALBUMS -> fetchAlbums()
            ARTISTS -> fetchArtists()
            HOME_SECTIONS -> fetchHomeSections()
            PLAYLISTS -> fetchPlaylists()
        }
    }

    fun updateColor(newColor: Int) {
        _paletteColor.postValue(newColor)
    }

    fun followPlaylist(name: String, playlistId: String) = viewModelScope.launch(IO) {
        realRepository.followPlaylist(name, playlistId)
    }

    fun unfollowPlaylist(name: String, playlistId: String) = viewModelScope.launch(IO) {
        realRepository.unfollowPlaylist(name, playlistId)
    }
}