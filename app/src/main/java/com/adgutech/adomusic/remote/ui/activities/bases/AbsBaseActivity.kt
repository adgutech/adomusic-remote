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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.rootView
import com.adgutech.commons.hasVersionTiramisu
import com.google.android.material.snackbar.Snackbar

abstract class AbsBaseActivity : AbsThemeActivity() {

    private var hadPermissions: Boolean = false
    private var permissionDeniedMessage: String? = null

    private val snackBarContainer: View
        get() = rootView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preference.isAdjustVolume) {
            volumeControlStream = AudioManager.STREAM_MUSIC
        }
        if (hasVersionTiramisu) {
            hadPermissions = hasPermissionsNotifications()
        }
        permissionDeniedMessage = null
    }

    override fun onResume() {
        super.onResume()
        if (hasVersionTiramisu) {
            val hasPermissions = hasPermissionsNotifications()
            if (hasPermissions != hadPermissions) {
                hadPermissions = hasPermissions
                onHasPermissionsChanged(hasPermissions)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_NOTIFICATIONS && hasVersionTiramisu) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    )) {
                        // User has deny from permission dialog.
                        Snackbar.make(snackBarContainer, permissionDeniedMessage!!, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.action_grant) { requestPermissionsNotifications() }
                            .setActionTextColor(accentColor())
                            .show()
                    } else {
                        // User has deny permission and checked never show permission
                        // dialog so you can redirect to Application settings page.
                        Snackbar.make(snackBarContainer, permissionDeniedMessage!!, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.action_settings) {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            .setActionTextColor(accentColor())
                            .show()
                    }
                }
            }
            hadPermissions = true
            onHasPermissionsChanged(true)
        }
    }

    // this lets keyboard close when clicked in background
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    getSystemService<InputMethodManager>()
                        ?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    protected fun hasPermissionsNotifications(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    protected fun requestPermissionsNotifications() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            PERMISSION_NOTIFICATIONS
        )
    }

    protected fun setPermissionDeniedMessage(message: String) {
        permissionDeniedMessage = message
    }

    fun getPermissionDeniedMessage(): String {
        return if (permissionDeniedMessage == null) {
            getString(R.string.permission_denied)
        } else {
            permissionDeniedMessage!!
        }
    }

    protected open fun onHasPermissionsChanged(hasPermissions: Boolean) {
        // implemented by sub classes
        logD(hasPermissions)
    }

    companion object {
        const val PERMISSION_NOTIFICATIONS = 125
        const val PERMISSION_STORAGE = 150
    }
}