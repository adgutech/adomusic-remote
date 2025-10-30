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

package com.adgutech.adomusic.remote.ui.fragments.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.models.PlaylistTrackParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class PlaylistDetailsViewModel(
    private val realRepository: RealRepository,
    private val playlistId: String?
) : ViewModel(), OnSpotifyServiceEventListener {

    private val playlistDetails = MutableLiveData<Result<Playlist>>()
    private val playlistTracks = MutableLiveData<Result<List<PlaylistTrackParcelable>>>()

    init {
        fetchPlaylistDetails()
        fetchPlaylistTracks()
    }

    fun getPlaylistDetails(): MutableLiveData<Result<Playlist>> = playlistDetails

    fun getPlaylistTracks(): MutableLiveData<Result<List<PlaylistTrackParcelable>>> = playlistTracks

    fun uploadImageToPlaylist(playlistId: String, contentType: String, body: RequestBody) {
        viewModelScope.launch(IO) {
            realRepository.uploadImageToPlaylist(playlistId, contentType, body)
        }
    }

    fun fetchPlaylistDetails() {
        viewModelScope.launch(IO) {
            playlistDetails.postValue(
                realRepository.getPlaylist(
                    AbsMainActivityFragment.userId ?: "", playlistId ?: ""
                )
            )
        }
    }

    private fun fetchPlaylistTracks() {
        viewModelScope.launch(IO) {
            playlistTracks.postValue(
                realRepository.getPlaylistTracks(
                    AbsMainActivityFragment.userId ?: "", playlistId ?: ""
                )
            )
        }
    }

    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onPlayerStateChanged() {
        fetchPlaylistDetails()
        fetchPlaylistTracks()
    }
}