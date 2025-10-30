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

package com.adgutech.adomusic.remote.ui.fragments.artists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistTrackParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.repositories.RealRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * Created by Adolfo Gutiérrez on 03/18/2025.
 */

class ArtistDetailsViewModel(
    private val realRepository: RealRepository,
    private val artistId: String?
) : ViewModel(), OnSpotifyServiceEventListener {

    private val artistDetails = MutableLiveData<Result<Artist>>()
    private val followingArtists = MutableLiveData<Result<Array<Boolean>>>()
    private val artistTopTracks = MutableLiveData<Result<List<ArtistTrackParcelable>>>()
    private val artistAlbums = MutableLiveData<Result<List<ArtistAlbumParcelable>>>()
    private val moreAlbums = MutableLiveData<Result<MutableList<Any>>>()

    init {
        fetchArtist()
        fetchFollowingArtists()
        fetchArtistTopTracks()
        fetchArtistAlbums()
    }

    private fun fetchArtist() {
        viewModelScope.launch(IO) {
            artistId?.let {
                artistDetails.postValue(Loading)
                artistDetails.postValue(realRepository.getArtistDetails(it))
            }
        }
    }

    private fun fetchFollowingArtists() {
        viewModelScope.launch(IO) {
            artistId?.let {
                followingArtists.postValue(Loading)
                followingArtists.postValue(realRepository.isFollowingArtists(it))
            }
        }
    }

    private fun fetchArtistTopTracks() {
        viewModelScope.launch(IO) {
            artistId?.let {
                artistTopTracks.postValue(Loading)
                artistTopTracks.postValue(realRepository.getArtistTopTracks(it))
            }
        }
    }

    private fun fetchArtistAlbums() {
        viewModelScope.launch(IO) {
            artistId?.let {
                artistAlbums.postValue(Loading)
                artistAlbums.postValue(realRepository.getArtistAlbums(it))
            }
        }
    }

    fun getArtistDetails(): MutableLiveData<Result<Artist>> = artistDetails

    fun getArtistAlbums(): MutableLiveData<Result<List<ArtistAlbumParcelable>>> = artistAlbums

    fun getArtistTopTracks(): MutableLiveData<Result<List<ArtistTrackParcelable>>> = artistTopTracks

    fun getMoreAlbums(): MutableLiveData<Result<MutableList<Any>>> = moreAlbums

    fun albumFilter(artistId: String, filter: AlbumTypeFilter) =
        viewModelScope.launch(IO) {
            val albumType = realRepository.getArtistAlbumsTypes(artistId, filter)
            moreAlbums.postValue(albumType)
        }

    fun isFollowingArtists(): MutableLiveData<Result<Array<Boolean>>> = followingArtists

    fun followArtists() = viewModelScope.launch(IO) {
        realRepository.followArtists(artistId!!)
        fetchFollowingArtists()
    }

    fun unfollowArtists() = viewModelScope.launch(IO) {
        realRepository.unfollowArtists(artistId!!)
        fetchFollowingArtists()
    }

    override fun onServiceConnected() {}

    override fun onServiceDisconnected() {}

    override fun onPlayerStateChanged() {
        fetchArtist()
        fetchFollowingArtists()
        fetchArtistTopTracks()
        fetchArtistAlbums()
    }
}