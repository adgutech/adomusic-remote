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

import com.adgutech.adomusic.remote.models.DeviceParcelable
import com.adgutech.adomusic.remote.models.TrackQueue
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Result
import com.adgutech.adomusic.remote.api.spotify.models.Track
import com.adgutech.adomusic.remote.api.spotify.models.TrackPosition
import com.adgutech.adomusic.remote.api.spotify.models.TrackToPlayPosition
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.utils.Utils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

interface PlayerRepository {
    fun getAvailableDevice(): List<DeviceParcelable>
    fun playUri(deviceId: String?, contextUri: String)
    fun playUri(deviceId: String?, contextUri: String, uris: List<String>, position: Int)
    fun resume(deviceId: String?)
    fun pause(deviceId: String?)
    fun skipNext(deviceId: String?)
    fun skipPrevious(deviceId: String?)
    fun getUserQueue(): List<TrackQueue>
}

class RealPlayerRepository(private val spotifyService: SpotifyService) : PlayerRepository {

    override fun getAvailableDevice(): List<DeviceParcelable> {
        val deviceList = arrayListOf<DeviceParcelable>()
        val availableDevices = spotifyService.availableDevice
        val devices = availableDevices.devices
        for (device in devices) {
            val id = device.id
            val isActive = device.is_active
            val isPrivateSession = device.is_private_session
            val isRestricted = device.is_restricted
            val name = device.name
            val type = device.type
            val volumePercent = device.volume_percent
            val supportsVolume = device.supports_volume
            val deviceParcelable = DeviceParcelable(
                id,
                isActive,
                isPrivateSession,
                isRestricted,
                name,
                type,
                volumePercent,
                supportsVolume
            )
            deviceList.add(deviceParcelable)
        }
        return deviceList
    }

    override fun playUri(deviceId: String?, contextUri: String) {
        val trackToPlayPosition = TrackToPlayPosition()
        trackToPlayPosition.contextUri = contextUri
        spotifyService.playUri(deviceId, trackToPlayPosition, object : Callback<Result> {
            override fun success(t: Result?, response: Response?) {
                logD("Success to play uri.")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to play uri: $error")
            }
        })
    }

    override fun playUri(deviceId: String?, contextUri: String, uris: List<String>, position: Int) {
        val trackToPlayPosition = TrackToPlayPosition()
        trackToPlayPosition.let {
            it.contextUri = contextUri
            it.uris = uris
            it.offset.position = position
        }
        spotifyService.playUri(deviceId, trackToPlayPosition, object : Callback<Result> {
            override fun success(t: Result?, response: Response?) {
                logD("Success to play uri.")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to play uri: $error")
            }
        })
    }

    override fun resume(deviceId: String?) {
        spotifyService.resume(deviceId, object : Callback<Result> {
            override fun success(t: Result?, response: Response?) {
                logD("Success to resume track.")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to resume track: $error")
            }
        })
    }

    override fun pause(deviceId: String?) {
        spotifyService.pause(deviceId, object : Callback<Result> {
            override fun success(t: Result?, response: Response?) {
                logD("Success to pause track.")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to pause track: $error")
            }
        })
    }

    override fun skipNext(deviceId: String?) {
        spotifyService.skipNext(deviceId, object : Callback<Result> {
                override fun success(t: Result?, response: Response?) {
                    logD("Success to skip next track.")
                }

                override fun failure(error: RetrofitError?) {
                    logE("Error to skip next track: $error")
                }
            })
    }

    override fun skipPrevious(deviceId: String?) {
        spotifyService.skipPrevious(deviceId, object : Callback<Result> {
            override fun success(t: Result?, response: Response?) {
                logD("Success to skip previous track.")
            }

            override fun failure(error: RetrofitError?) {
                logE("Error to skip previous track: $error")
            }
        })
    }

    override fun getUserQueue(): List<TrackQueue> {
        val trackList = arrayListOf<TrackQueue>()
        val duplicate = arrayListOf<String>()
        val queueUserTrack = spotifyService.getUserQueue()
        val queue = queueUserTrack.queue
        val currentTrack = queueUserTrack.currently_playing
        queue.add(0, currentTrack)
        for (track in queue) {
            val id = track.id
            val item = getTrackFromUserQueueImpl(track, id, currentTrack)
            if (!duplicate.contains(id)) {
                trackList.add(item)
                duplicate.add(id)
            }
        }
        return trackList
    }

    private fun getTrackFromUserQueueImpl(
        track: Track,
        id: String,
        currentlyPlaying: Track
    ): TrackQueue {
        val album = track.album.name
        val albumId = track.album.id
        val artist = track.artists[0].name
        val artistId = track.artists[0].id
        val artists = track.artists
        val discNumber = track.disc_number
        val duration = track.duration_ms
        val imageUrl = Utils.getImageUrl(track.album.images)
        val isExplicit = track.explicit
        val link = track.external_ids[SpotifyService.SPOTIFY_URL]!!
        val name = track.name
        val trackNumber = track.track_number
        val type = track.type
        val uri = track.uri
        return TrackQueue(
            album,
            albumId,
            artist,
            artistId,
            artists,
            currentlyPlaying,
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