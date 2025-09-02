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

package com.adgutech.adomusic.remote.ui.fragments.albums

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.models.AlbumTrackParcelable
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.Result.Loading
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.repositories.RealRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AlbumDetailsViewModel(
    private val realRepository: RealRepository,
    private val albumId: String?
) : ViewModel(), OnSpotifyServiceEventListener {

    private val checkAlbumSaved = MutableLiveData<Result<Array<Boolean>>>()

    private val handler: Handler = Handler(Looper.getMainLooper())

    init {
        fetchCheckAlbumSaved()
    }

    private fun fetchCheckAlbumSaved() {
        viewModelScope.launch(IO) {
            albumId?.let {
                checkAlbumSaved.postValue(Loading)
                checkAlbumSaved.postValue(realRepository.containsMySavedAlbums(it))
            }
        }
        handler.postDelayed({
            viewModelScope.launch(IO) {
                albumId?.let {
                    checkAlbumSaved.postValue(Loading)
                    checkAlbumSaved.postValue(realRepository.containsMySavedAlbums(it))
                }
            }
        }, 300)
    }

    fun getAlbumDetails(): LiveData<Result<Album>> = liveData(IO) {
        emit(Loading)
        val album = realRepository.getAlbumDetails(albumId!!)
        emit(album)
    }

    fun checkAlbumSaved(): MutableLiveData<Result<Array<Boolean>>> = checkAlbumSaved

    fun getAlbumTracks(): LiveData<Result<List<AlbumTrackParcelable>>> = liveData(IO) {
        emit(Loading)
        val tracks = realRepository.getAlbumTracks(albumId!!)
        emit(tracks)
    }

    fun getArtistAlbums(artistId: String): LiveData<Result<List<ArtistAlbumParcelable>>> = liveData(IO) {
        emit(Loading)
        val albums = realRepository.getArtistAlbums(artistId)
        emit(albums)
    }

    fun getArtistDetails(artistId: String): LiveData<Result<Artist>> = liveData(IO) {
        emit(Loading)
        val artist = realRepository.getArtistDetails(artistId)
        emit(artist)
    }

    fun addToMySavedAlbums(albumId: String) = viewModelScope.launch(IO) {
        realRepository.addToMySavedAlbums(albumId)
        fetchCheckAlbumSaved()
    }

    fun removeFromMySavedAlbums(albumId: String) = viewModelScope.launch(IO) {
        realRepository.removeFromMySavedAlbums(albumId)
        fetchCheckAlbumSaved()
    }

    override fun onServiceConnected() {}

    override fun onServiceDisconnected() {}

    override fun onPlayerStateChanged() {
        fetchCheckAlbumSaved()
    }
}