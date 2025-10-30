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

package com.adgutech.adomusic.remote.helpers.menu

import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object TracksMenuHelper : KoinComponent {
    fun handleMenuClick(
        activity: FragmentActivity,
        tracks: List<TrackParcelable>,
        menuItemId: Int
    ): Boolean {
        when (menuItemId) {
            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().getMyPlaylistsForDialog()
                    withContext(Dispatchers.Main) {
                        val trackUris = arrayListOf<String>()
                        for (track in tracks) {
                            track.uri.let { trackUris.add(it) }
                        }
                        AddToPlaylistDialog.create(playlists, trackUris)
                            .show(activity.supportFragmentManager, "AddToPlaylistDialog")
                    }
                }
                return true
            }
        }
        return false
    }
}