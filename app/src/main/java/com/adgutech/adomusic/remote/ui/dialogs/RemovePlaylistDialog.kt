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

package com.adgutech.adomusic.remote.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.adgutech.adomusic.remote.EXTRA_PLAYLIST
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.commons.extensions.colorButtons
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RemovePlaylistDialog : DialogFragment() {

    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlistDetails = BundleCompat.getParcelable(
            requireArguments(),
            EXTRA_PLAYLIST,
            Playlist::class.java
        )

        val playlist = BundleCompat.getParcelable(
            requireArguments(),
            EXTRA_PLAYLIST,
            PlaylistParcelable::class.java
        )

        val name = if (playlist != null) {
            playlist.title
        } else if (playlistDetails != null) {
            playlistDetails.name
        } else ""

        val playlistId = if (playlist != null) {
            playlist.id
        } else if (playlistDetails != null) {
            playlistDetails.id
        } else ""

        return materialDialog(R.string.title_remove_playlist_from_library)
            .setMessage(
                String.format(
                    getString(R.string.message_remove_playlist_from_library), name
                )
            )
            .setPositiveButton(R.string.action_remove) { _, _ ->
                libraryViewModel.unfollowPlaylist(name!!, playlistId!!)
                libraryViewModel.forceReload(ReloadType.PLAYLISTS)
                if (playlistDetails != null) {
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }

    companion object {
        fun create(playlist: Playlist): RemovePlaylistDialog {
            return RemovePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_PLAYLIST to playlist)
            }
        }

        fun create(playlist: PlaylistParcelable): RemovePlaylistDialog {
            return RemovePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_PLAYLIST to playlist)
            }
        }
    }
}