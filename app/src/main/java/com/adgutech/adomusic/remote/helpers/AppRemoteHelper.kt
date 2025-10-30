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

package com.adgutech.adomusic.remote.helpers

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.repositories.PlayerRepository
import com.adgutech.adomusic.remote.service.MusicService
import com.adgutech.commons.extensions.showToast
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Image.Dimension
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.WeakHashMap

object AppRemoteHelper : KoinComponent {

    private val context = App.getInstance()
    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()
    var musicService: MusicService? = null

    val spotifyAppRemote: SpotifyAppRemote?
        get() = if (musicService != null) {
            musicService!!.spotifyAppRemote
        } else null

    val isSpotifyPremium: Boolean
        get() = if (musicService != null) {
            musicService!!.isSpotifyPremium
        } else false

    val currentTrack: Track?
        get() = if (musicService != null) {
            musicService!!.currentTrack
        } else null

    val isPlaying: Boolean
        get() = if (musicService != null) {
            musicService!!.isPlaying
        } else false

    val repeatMode: Int
        get() = if (musicService != null) {
            musicService!!.repeatMode
        } else 0

    val isShuffle: Boolean
        get() = if (musicService != null) {
            musicService!!.isShuffle
        } else false

    val trackDurationMillis: Long
        get() = if (musicService != null) {
            musicService!!.trackDurationMillis
        } else -1

    val trackProgressMillis: Long
        get() = if (musicService != null) {
            musicService!!.trackProgressMillis
        } else -1

    @Suppress("DEPRECATION")
    fun bindToService(context: Context, callback: ServiceConnection): ServiceToken? {

        val realActivity = (context as Activity).parent ?: context
        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MusicService::class.java)

        // https://issuetracker.google.com/issues/76112072#comment184
        // Workaround for ForegroundServiceDidNotStartInTimeException
        try {
            context.startService(intent)
        } catch (_: Exception) {
            ContextCompat.startForegroundService(context, intent)
        }

        val binder = ServiceBinder(callback)

        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MusicService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
            )
        ) {
            mConnectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
            musicService = null
        }
    }

    fun playUri(uri: String, isShuffle: Boolean) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi.let { playerApi ->
                    playerApi.setShuffle(isShuffle)
                        playerApi.play(uri)
                        .setResultCallback { logD("Play uri result: $uri") }
                        .setErrorCallback { throwable -> logE("Play uri error: $throwable") }
                }
            }
        }
    }

    fun addQueue(name: String, uri: String) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .queue(uri)
                    .setResultCallback {
                        context.showToast(
                            String.format(
                                context.getString(R.string.text_added_to_queue),
                                name
                            )
                        )
                    }.setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("Queue error: $throwable")
                    }
            }
        }
    }

    fun playNextTrack() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .skipNext()
                    .setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("Skip next error: $throwable")
                    }
            }
        }
    }

    fun playPreviousTrack() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .skipPrevious()
                    .setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("Skip previous error: $throwable")
                    }
            }
        }
    }

    fun pause() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .pause()
                    .setResultCallback { result -> logD("Pause result: $result") }
                    .setErrorCallback { throwable -> logE("Pause error: $throwable") }
            }
        }
    }

    fun resume() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .resume()
                    .setResultCallback { result -> logD("Resume result: $result") }
                    .setErrorCallback { throwable -> logE("Resume error: $throwable") }
            }
        }
    }

    fun seekTo(seekToPosition: Int) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .seekTo(seekToPosition.toLong())
                    .setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("SeekTo error: $throwable")
                    }
            }
        }
    }

    fun toggleRepeat() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .toggleRepeat()
                    .setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("ToggleRepeat error: $throwable")
                    }
            }
        }
    }

    fun toggleShuffle() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.playerApi
                    .toggleShuffle()
                    .setErrorCallback { throwable ->
                        context.showToast(context.getString(R.string.title_spotify_premium_required))
                        logE("ToggleShuffle error: $throwable")
                    }
            }
        }
    }

    fun addToLibrary(uri: String) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.userApi
                    .addToLibrary(uri)
                    .setResultCallback { result -> logD("Add to library result: $result") }
                    .setResultCallback { throwable -> logE("Add to library error: $throwable") }
            }
        }
    }

    fun removeFromLibrary(uri: String) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.userApi
                    .removeFromLibrary(uri)
                    .setResultCallback { result -> logD("Remove from library result: $result") }
                    .setResultCallback { throwable -> logE("Remove from library error: $throwable") }
            }
        }
    }

    fun getLibraryState(uri: String, callback: (isAdded: Boolean) -> Unit) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.userApi
                    .getLibraryState(uri)
                    .setResultCallback { result -> callback(result.isAdded) }
            }
        }
    }

    fun getImage(
        imageUri: ImageUri,
        dimension: Dimension,
        callback: (bitmap: Bitmap) -> Unit
    ) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                it.imagesApi
                    .getImage(imageUri, dimension)
                    ?.setResultCallback { bitmap -> callback(bitmap) }
            }
        }
    }



    class ServiceBinder internal constructor(private val mCallback: ServiceConnection?) :
        ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.service
            mCallback?.onServiceConnected(name, service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mCallback?.onServiceDisconnected(name)
            musicService = null
        }
    }

    class ServiceToken internal constructor(internal var mWrappedContext: ContextWrapper)
}