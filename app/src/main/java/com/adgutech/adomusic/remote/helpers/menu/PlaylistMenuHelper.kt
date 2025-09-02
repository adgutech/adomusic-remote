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
import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.ui.dialogs.EditPlaylistDialog
import com.adgutech.adomusic.remote.ui.dialogs.RemovePlaylistDialog
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.utils.Utils
import org.koin.androidx.viewmodel.ext.android.getViewModel

object PlaylistMenuHelper {

    val MENU_RES
        @MenuRes get() = R.menu.menu_item_playlist

    fun handleMenuClick(
        activity: FragmentActivity,
        playlist: PlaylistParcelable,
        @IdRes menuItemId: Int
    ): Boolean {
        val libraryViewModel = activity.getViewModel() as LibraryViewModel
        when (menuItemId) {
            R.id.action_play -> {
                AppRemoteHelper.playUri(playlist.uri!!, false)
                return true
            }

            R.id.action_share -> {
                activity.startActivity(
                    Intent.createChooser(
                        Utils.shareContentOfSpotify(playlist.link!!, false),
                        String.format(
                            activity.getString(R.string.text_share_playlist),
                            playlist.title
                        )
                    )
                )
                return true
            }

            R.id.action_see_details_on_spotify -> {
                activity.startActivity(
                    Utils.seeDetailsOnSpotify(playlist.link!!, false)
                )
                return true
            }

            R.id.action_edit -> {
                EditPlaylistDialog.create(playlist).show(
                    activity.supportFragmentManager, "EditPlaylistDialog"
                )
                return true
            }

            R.id.action_save_playlist_to_library -> {
                libraryViewModel.followPlaylist(playlist.title!!, playlist.id!!)
                return true
            }

            R.id.action_remove_playlist_from_library -> {
                RemovePlaylistDialog.create(playlist).show(
                    activity.supportFragmentManager, "RemovePlaylistDialog"
                )
                return true
            }
        }
        return false
    }

    abstract class OnClickPlaylistMenu(
        private val activity: FragmentActivity
    ) :
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        open val menuRes: Int
            @MenuRes get() = MENU_RES

        abstract val playlist: PlaylistParcelable

        private val libraryViewModel = activity.getViewModel() as LibraryViewModel

        private var currentUserId: String? = null

        init {
            libraryViewModel.getMe().observe(activity) { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> currentUserId = result.data.id
                    is Result.Error -> {}
                }
            }
        }

        override fun onClick(v: View) {
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(menuRes)
            val menu = popupMenu.menu
            menu.findItem(R.id.action_edit).isVisible = playlist.userId == currentUserId
            if (playlist.userId == currentUserId) {
                menu.findItem(R.id.action_save_playlist_to_library).isVisible = false
                menu.findItem(R.id.action_remove_playlist_from_library).isVisible = false
            } else {
                AppRemoteHelper.getLibraryState(playlist.uri!!) {
                    menu.findItem(R.id.action_save_playlist_to_library).isVisible = !it
                    menu.findItem(R.id.action_remove_playlist_from_library).isVisible = it
                }
            }
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity, playlist, item.itemId)
        }
    }
}