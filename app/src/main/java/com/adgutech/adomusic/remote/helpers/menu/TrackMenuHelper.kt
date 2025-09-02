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

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import com.adgutech.adomusic.remote.ui.dialogs.BrowseArtistsDialog
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object TrackMenuHelper : KoinComponent {

    val MENU_RES
        @MenuRes get() = R.menu.menu_item_track

    fun handleMenuClick(
        activity: FragmentActivity,
        track: TrackParcelable,
        @IdRes menuItemId: Int
    ): Boolean {
        val libraryViewModel = activity.getViewModel() as LibraryViewModel
        when (menuItemId) {
            R.id.action_play -> {
                AppRemoteHelper.playUri(track.uri, false)
                return true
            }

            R.id.action_add_to_current_playing -> {
                libraryViewModel.addToQueue(track.uri, track.name)
                return true
            }

            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().getMyPlaylistsForDialog()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, track.uri)
                            .show(activity.supportFragmentManager, "AddToPlaylistDialog")
                    }
                }
                return true
            }

            R.id.action_share -> {
                activity.startActivity(
                    Intent.createChooser(
                        Utils.shareContentOfSpotify(track.spotifyUrl!!, false),
                        String.format(activity.getString(R.string.text_share_song), track.name)
                    )
                )
                return true
            }

            R.id.action_see_details_on_spotify -> {
                activity.startActivity(
                    Utils.seeDetailsOnSpotify(track.spotifyUrl!!, false)
                )
                return true
            }

            R.id.action_browse_album -> {
                activity.findNavController(R.id.fragment_container).navigate(
                    R.id.albumDetailsFragment,
                    bundleOf(EXTRA_ALBUM_ID to track.albumId)
                )
                return true
            }

            R.id.action_browse_artist -> {
                if (track.artists.size >= 2) {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            BrowseArtistsDialog.create(track)
                                .show(activity.supportFragmentManager, "BrowseArtistDialog")
                        }
                    }
                } else {
                    activity.findNavController(R.id.fragment_container).navigate(
                        R.id.artistDetailsFragment,
                        bundleOf(EXTRA_ARTIST_ID to track.artistId)
                    )
                }
                return true
            }
        }
        return false
    }

    abstract class OnClickTrackMenu(
        private val activity: FragmentActivity
    ) : View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        open val menuRes: Int
            @MenuRes get() = MENU_RES

        abstract val track: TrackParcelable

        override fun onClick(v: View) {
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(menuRes)
            val menu = popupMenu.menu
            AppRemoteHelper.getLibraryState(track.uri) {
                menu.findItem(R.id.action_save_to_liked_songs).isVisible = !it
                menu.findItem(R.id.action_remove_to_liked_songs).isVisible = it
            }
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity, track, item.itemId)
        }
    }
}