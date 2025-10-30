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

import android.content.Intent
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SPOTIFY_PACKAGE
import com.adgutech.adomusic.remote.extensions.checkForInternet
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.commons.GOOGLE_PLAY_DETAILS
import com.adgutech.commons.extensions.disableScreenRotation
import com.adgutech.commons.extensions.enableScreenRotation
import androidx.core.net.toUri
import com.adgutech.commons.extensions.showToast

abstract class AbsSpotifyLoginActivity : AbsSlidingMusicPanelActivity() {

    companion object {
        val TAG: String = AbsSpotifyLoginActivity::class.java.simpleName
    }

    fun showSpotifyNotInstalledDialog() {
        disableScreenRotation()
        val materialDialog = materialDialog(R.string.title_spotify_install)
        materialDialog.setMessage(R.string.message_spotify_install)
        materialDialog.setNegativeButton(R.string.action_cancel) { dialog, _ ->
            enableScreenRotation()
            dialog.dismiss()
        }
        materialDialog.setPositiveButton(R.string.action_download) { dialog, _ ->
            if (checkForInternet()) {
                getSpotifyInGooglePlayStore()
                enableScreenRotation()
                dialog.dismiss()
            } else {
                showToast(R.string.text_not_internet)
            }
        }
        materialDialog.setCancelable(false)
        materialDialog.show()
    }

    private fun getSpotifyInGooglePlayStore() {
        val uri = GOOGLE_PLAY_DETAILS.toUri()
            .buildUpon()
            .appendQueryParameter("id", SPOTIFY_PACKAGE)
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}