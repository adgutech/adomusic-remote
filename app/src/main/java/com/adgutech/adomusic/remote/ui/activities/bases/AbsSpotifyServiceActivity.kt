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

package com.adgutech.adomusic.remote.ui.activities.bases

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.REQUEST_CODE
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.api.spotify.SpotifyApi
import com.adgutech.adomusic.remote.extensions.hasSpotifyInstalled
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.commons.extensions.disableScreenRotation
import com.adgutech.commons.extensions.enableScreenRotation
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse.Type.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Adolfo Gutiérrez on 02/18/2025.
 */

abstract class AbsSpotifyServiceActivity : AbsBaseActivity(), OnSpotifyServiceEventListener {

    companion object {
        val TAG: String = AbsSpotifyServiceActivity::class.java.simpleName
    }

    protected val libraryViewModel by viewModel<LibraryViewModel>()
    private val spotifyApi: SpotifyApi by inject()
    private var serviceToken: AppRemoteHelper.ServiceToken? = null
    private val spotifyServiceEventListeners = ArrayList<OnSpotifyServiceEventListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasSpotifyInstalled()) {
            serviceToken = AppRemoteHelper.bindToService(this, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    connect(false)
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    this@AbsSpotifyServiceActivity.onServiceDisconnected()
                }
            })
        }
        setPermissionDeniedMessage(getString(R.string.permission_notification_denied))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if result comes from the correct activity.
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                // Response was successful and contains auth token.
                TOKEN -> {
                    spotifyApi.setAccessToken(response.accessToken)
                    libraryViewModel.loadLibraryContent()

                    // Calculate and store the expiration time.
                    preference.accessTokenExpirationTime =
                        System.currentTimeMillis() + TimeUnit.SECONDS
                            .toMillis(response.expiresIn.toLong())
                }
                //Auth flow returned an error.
                ERROR -> logE("Auth error: ${response.error}")
                //Most likely auth flow was cancelled.
                else -> logD("Auth result: ${response.type}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppRemoteHelper.unbindFromService(serviceToken)
    }

    override fun onServiceConnected() {
        if (AppRemoteHelper.musicService != null) {
            if (System.currentTimeMillis() >= preference.accessTokenExpirationTime
                || preference.accessToken == "access_token") {
                AppRemoteHelper.musicService!!.onAuthorizationSpotify(this)
            }
            AppRemoteHelper.musicService!!.onSubscribedToPlayerState {
                this@AbsSpotifyServiceActivity.onPlayerStateChanged()
            }
        }
        for (listener in spotifyServiceEventListeners) {
            listener.onServiceConnected()
        }
    }

    override fun onServiceDisconnected() {
        for (listener in spotifyServiceEventListeners) {
            listener.onServiceDisconnected()
        }
    }

    override fun onPlayerStateChanged() {
        for (listener in spotifyServiceEventListeners) {
            listener.onPlayerStateChanged()
        }
    }

    fun addSpotifyServiceEventListener(listener: OnSpotifyServiceEventListener?) {
        if (listener != null) {
            spotifyServiceEventListeners.add(listener)
        }
    }

    fun removeSpotifyServiceEventListener(listener: OnSpotifyServiceEventListener?) {
        if (listener != null) {
            spotifyServiceEventListeners.remove(listener)
        }
    }

    /*fun showSpotifyPremiumDialog() {
        disableScreenRotation()
        val materialDialog = materialDialog(R.string.title_notice)
        materialDialog.setMessage(R.string.message_spotify_premium)
        materialDialog.setPositiveButton(R.string.action_ok) { dialog, _ ->
            enableScreenRotation()
            dialog.dismiss()
        }
        materialDialog.setNegativeButton(R.string.action_exit) { dialog, _ ->
            enableScreenRotation()
            dialog.dismiss()
            finish()
        }
        materialDialog.setCancelable(false)
        materialDialog.show()
    }*/

    fun showSpotifyDisconnectedDialog() {
        if (isFinishing || isDestroyed) {
            return // Don't show the dialog if the activity is not running
        }
        disableScreenRotation()
        val materialDialog = materialDialog(R.string.title_notification_app_remote_disconnected)
        materialDialog.setMessage(R.string.summary_notification_app_remote_disconnected)
        materialDialog.setPositiveButton(R.string.action_connect) { dialog, _ ->
            enableScreenRotation()
            connect(false)
            dialog.dismiss()
        }
        materialDialog.setNeutralButton(R.string.action_exit) { dialog, _ ->
            enableScreenRotation()
            dialog.dismiss()
            finish()
        }
        materialDialog.setCancelable(false)
        materialDialog.show()
    }

    fun connect(showAuthView: Boolean) {
        AppRemoteHelper.musicService?.connect(showAuthView, object : ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote?) {
                    this@AbsSpotifyServiceActivity.onServiceConnected()
                }

                override fun onFailure(throwable: Throwable?) {
                    this@AbsSpotifyServiceActivity.onServiceDisconnected()
                    showSpotifyDisconnectedDialog()
                }
            })
    }
}