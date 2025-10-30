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

package com.adgutech.adomusic.remote.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SPOTIFY_PACKAGE
import com.adgutech.adomusic.remote.preferences.Preferences
import com.adgutech.adomusic.remote.ui.activities.PurchaseActivity
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.hasVersionMarshmallow
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.Locale

val Context.preference: Preferences
    get() = Preferences.newInstance(this)

val Fragment.preference: Preferences
    get() = requireContext().preference

fun Context.checkForInternet(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (hasVersionMarshmallow) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                logD("Transport Wi-Fi.")
                true
            }

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                logD("Transport Cellular.")
                true
            }

            else -> false
        }
    } else {
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

fun FragmentActivity.installLanguageAndRecreate(code: String, onInstallComplete: () -> Unit) {
    var mySessionId = 0

    val manager = SplitInstallManagerFactory.create(this)
    val listener = object: SplitInstallStateUpdatedListener {
        override fun onStateUpdate(state: SplitInstallSessionState) {
            // Restart the activity if the language is installed (sessionId is same and status is installed)
            if (state.sessionId() == mySessionId && state.status() == SplitInstallSessionStatus.INSTALLED) {
                onInstallComplete()
                manager.unregisterListener(this)
            }
        }
    }
    manager.registerListener(listener)

    if (code != "auto") {
        // Try to download language resources
        val request =
            SplitInstallRequest.newBuilder().addLanguage(Locale.forLanguageTag(code))
                .build()
        manager.startInstall(request)
            // Recreate the activity on download complete
            .addOnSuccessListener {
                mySessionId = it
            }
            .addOnFailureListener {
                showToast(getString(R.string.text_download_language_failed))
            }
    } else {
        recreate()
    }
}

fun Context.goToProVersion() {
    startActivity(Intent(this, PurchaseActivity::class.java))
}

fun Context.installSplitCompat() {
    SplitCompat.install(this)
}

fun Context.hasSpotifyInstalled(): Boolean {
    return try {
        packageManager.getPackageInfo(SPOTIFY_PACKAGE, 0)
        true
    } catch (e: NameNotFoundException) {
        logE("Spotify app not installed: $e")
        false
    }
}

fun Fragment.hasSpotifyInstalled(): Boolean {
    return requireContext().hasSpotifyInstalled()
}