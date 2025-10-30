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

package com.adgutech.adomusic.remote.service

import android.app.Activity
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import com.adgutech.adomusic.remote.CLIENT_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.REDIRECT_URI
import com.adgutech.adomusic.remote.REQUEST_CODE
import com.adgutech.adomusic.remote.extensions.checkForInternet
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.logW
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.EqualizerHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.EQUALIZER_ENABLED
import com.adgutech.adomusic.remote.scopesList
import com.adgutech.adomusic.remote.service.notification.AppRemoteNotification
import com.adgutech.adomusic.remote.service.notification.AppRemoteNotificationImpl24
import com.adgutech.adomusic.remote.volume.AudioVolumeObserver
import com.adgutech.adomusic.remote.volume.OnAudioVolumeChangedListener
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.hasVersionQ
import com.adgutech.commons.hasVersionS
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.AuthenticationFailedException
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.OfflineModeException
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException
import com.spotify.android.appremote.api.error.SpotifyRemoteServiceException
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MusicService : Service(), SharedPreferences.OnSharedPreferenceChangeListener,
    OnAudioVolumeChangedListener {

    var spotifyAppRemote: SpotifyAppRemote? = null
    private var capabilitiesSubscription: Subscription<Capabilities>? = null
    private var playerStateSubscription: Subscription<PlayerState>? = null

    private var pausedByZeroVolume: Boolean = false

    var isSpotifyPremium: Boolean = false

    var currentTrack: Track? = null
    var isPlaying: Boolean = false

    var repeatMode: Int = 0
    var isShuffle: Boolean = false

    var trackDurationMillis: Long = -1
    var trackProgressMillis: Long = -1

    private var handler: Handler = Handler(Looper.getMainLooper())

    private val serviceScope = CoroutineScope(Job() + Main)

    //Notification
    private var appRemoteNotification: AppRemoteNotification? = null
    private var notificationManager: NotificationManager? = null
    private var isForeground: Boolean = false

    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            val progress = trackProgressMillis
            trackProgressMillis = progress + DURATION_LOOP
            handler.postDelayed(this, DURATION_LOOP.toLong())
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return  MusicBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (spotifyAppRemote != null) {
            if (!spotifyAppRemote!!.isConnected) {
                stopSelf()
            }
        }
        return true
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService()
        appRemoteNotification = AppRemoteNotificationImpl24.from(this, notificationManager!!)
        val audioVolumeObserver = AudioVolumeObserver(this)
        audioVolumeObserver.register(AudioManager.STREAM_MUSIC, this)
        preference.registerOnSharedPreferenceChangedListener(this)
        EqualizerHelper.setupEqualizer(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            serviceScope.launch {
                when (intent.action) {
                    ACTION_QUIT -> quit()
                }
            }
        }
        return START_STICKY
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            EQUALIZER_ENABLED -> {
                EqualizerHelper.setupEqualizer(this)
            }
        }
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        if (preference.isPauseOnZeroVolume) {
            if (isPlaying && currentVolume < 1) {
                spotifyAppRemote?.let {
                    if (it.isConnected) {
                        it.playerApi
                            .pause()
                            .setResultCallback { result ->
                                logD("Pause result: $result")
                            }.setErrorCallback { throwable ->
                                logE("Pause error: $throwable")
                            }
                    }
                }
                pausedByZeroVolume = true
            } else if (pausedByZeroVolume && currentVolume >= 1) {
                spotifyAppRemote?.let {
                    if (it.isConnected) {
                        it.playerApi
                            .resume()
                            .setResultCallback { result ->
                                logD("Resume result: $result")
                            }.setErrorCallback { throwable ->
                                logE("Resume error: $throwable")
                            }
                    }
                }
                pausedByZeroVolume = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        quit()
        releaseResources()
        serviceScope.cancel()
        preference.unregisterOnSharedPreferenceChangedListener(this)
    }

    fun pause() {
        handler.removeCallbacks(progressUpdateRunnable)
    }

    fun resume() {
        handler.removeCallbacks(progressUpdateRunnable)
        handler.postDelayed(progressUpdateRunnable, DURATION_LOOP.toLong())
    }

    fun connect(showAuthView: Boolean, mCallback: Connector.ConnectionListener) {
        if (!checkForInternet()) {
            showToast(R.string.text_not_internet)
            return
        }
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(showAuthView)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote?) {
                spotifyAppRemote = appRemote
                this@MusicService.onServiceConnected()
                mCallback.onConnected(appRemote)
            }

            override fun onFailure(throwable: Throwable?) {
                if (throwable is SpotifyRemoteServiceException) {
                    if (throwable.cause is SecurityException) {
                        logW(throwable.message!!)
                    } else if (throwable.cause is IllegalStateException) {
                        logW(throwable.message!!)
                    }
                } else if (throwable is NotLoggedInException) {
                    preference.isUserLogged = false
                    preference.accessToken = "access_token"
                    logW("NotLoggedInException")
                } else if (throwable is AuthenticationFailedException) {
                    preference.isUserLogged = false
                    preference.accessToken = "access_token"
                    logW("AuthenticationFailedException")
                } else if (throwable is UserNotAuthorizedException) {
                    preference.isUserLogged = false
                    preference.accessToken = "access_token"
                    logW("UserNotAuthorizedException")
                } else if (throwable is UnsupportedFeatureVersionException) {
                    logW(throwable.message!!)
                } else if (throwable is OfflineModeException) {
                    logW("OfflineModeException")
                } else if (throwable is CouldNotFindSpotifyApp) {
                    preference.isUserLogged = false
                    preference.accessToken = "access_token"
                    logW("CouldNotFindSpotifyApp")
                } else if (throwable is SpotifyDisconnectedException) {
                    this@MusicService.onServiceDisconnected()
                    mCallback.onFailure(throwable)
                } else if (throwable is SpotifyConnectionTerminatedException) {
                    this@MusicService.onServiceDisconnected()
                    mCallback.onFailure(throwable)
                }
            }
        })
    }

    private fun onServiceConnected() {
        notifyChange(CONNECT_CHANGED)
        onSubscribedToPlayerState {}
        onSubscribedToCapabilities()
        preference.isUserLogged = true
    }

    private fun onServiceDisconnected() {
        notifyChange(DISCONNECT_CHANGED)
    }

    private fun quit() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isForeground = false
        notificationManager?.cancel(AppRemoteNotification.NOTIFICATION_ID)
        stopSelf()
        disconnect()
    }

    fun onAuthorizationSpotify(activity: Activity) {
        val builder = AuthorizationRequest
            .Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setShowDialog(true)
        builder.setScopes(scopesList)
        val request = builder.build()
        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request)
    }

    fun onSubscribedToPlayerState(callback: () -> Unit) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                playerStateSubscription = cancelAndResetSubscription(playerStateSubscription)
                playerStateSubscription = it
                    .playerApi
                    .subscribeToPlayerState()
                    .setEventCallback { playerState ->
                        currentTrack = playerState.track

                        isPlaying = !playerState.isPaused

                        isShuffle = playerState.playbackOptions.isShuffling
                        repeatMode = playerState.playbackOptions.repeatMode

                        trackDurationMillis = playerState.track.duration
                        trackProgressMillis = playerState.playbackPosition

                        if (isPlaying) resume() else pause()
                        callback()
                    }
            }
        }
    }

    fun onSubscribedToCapabilities() {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                capabilitiesSubscription = cancelAndResetSubscription(capabilitiesSubscription)
                capabilitiesSubscription = it
                    .userApi
                    .subscribeToCapabilities()
                    .setEventCallback { capabilities ->
                        isSpotifyPremium = capabilities.canPlayOnDemand
                    }
            }
        }
    }

    private fun <T : Any?> cancelAndResetSubscription(subscription: Subscription<T>?): Subscription<T>? {
        subscription?.let {
            if (!it.isCanceled) {
                it.cancel()
            }
            return it
        }
        return null
    }

    private fun disconnect() {
        if (spotifyAppRemote != null && spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.let {
                SpotifyAppRemote.disconnect(it)
                spotifyAppRemote = null
            }
        }
    }

    private fun notifyChange(what: String) {
        when (what) {
            CONNECT_CHANGED -> {
                spotifyAppRemote?.let {
                    appRemoteNotification?.updateStatusRemote(it.isConnected)
                }
                startForegroundOrNotify()
            }
            DISCONNECT_CHANGED -> {
                spotifyAppRemote?.let {
                    appRemoteNotification?.updateStatusRemote(it.isConnected)
                }
                startForegroundOrNotify()
            }
        }
    }

    private fun startForegroundOrNotify() {
        if (appRemoteNotification != null && spotifyAppRemote != null) {
            if (isForeground && !spotifyAppRemote!!.isConnected) {
                // This makes the notification dismissible
                // We can't call stopForeground(false) on A12 though, which may result in crashes
                // when we call startForeground after that e.g. when Alarm goes off,
                if (!hasVersionS) {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
                    isForeground = false
                }
            }
            if (!isForeground && spotifyAppRemote!!.isConnected) {
                // Specify that this is a media service, if supported.
                if (hasVersionQ) {
                    startForeground(
                        AppRemoteNotification.NOTIFICATION_ID, appRemoteNotification!!.build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                } else {
                    startForeground(
                        AppRemoteNotification.NOTIFICATION_ID,
                        appRemoteNotification!!.build()
                    )
                }
                isForeground = true
            } else {
                // If we are already in foreground just update the notification
                notificationManager?.notify(
                    AppRemoteNotification.NOTIFICATION_ID, appRemoteNotification!!.build()
                )
            }
        }
    }

    private fun releaseResources() {
        EqualizerHelper.release()
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    companion object {
        val TAG: String = MusicService::class.java.simpleName
        private const val PACKAGE_NAME = "com.adgutech.adomusic.remote"
        const val ACTION_QUIT = "$PACKAGE_NAME.quit"
        const val CONNECT_CHANGED = "$PACKAGE_NAME.spotifyconnected"
        const val DISCONNECT_CHANGED = "$PACKAGE_NAME.spotifydisconnected"
        private const val DURATION_LOOP = 100
    }
}