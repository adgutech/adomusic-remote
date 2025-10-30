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
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.adgutech.adomusic.remote.EXTRA_PLAYLISTS
import com.adgutech.adomusic.remote.EXTRA_TRACK_URI
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.extraNotNull
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.commons.extensions.colorButtons
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AddToPlaylistDialog : DialogFragment() {

    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlists = extraNotNull<List<PlaylistParcelable>>(EXTRA_PLAYLISTS).value
        val trackUris = extraNotNull<List<String>>(EXTRA_TRACK_URI).value

        val playlistNames = mutableListOf<String>()
        val playlistIds = mutableListOf<String>()

        val userId = AbsMainActivityFragment.userId
        val playlistFilter = playlists.filter { it.userId == userId }

        for (playlist in playlistFilter) {
            playlist.title?.let { playlistNames.add(it) }
            playlist.id?.let { playlistIds.add(it) }
        }

        return materialDialog(R.string.action_add_to_playlist)
            .setItems(playlistNames.toTypedArray()) { dialog, which ->
                libraryViewModel.addTrackToPlaylist(
                    playlistIds[which],
                    playlistNames[which],
                    trackUris
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }

    companion object {
        fun create(playlists: List<PlaylistParcelable>, uri: String): AddToPlaylistDialog {
            val list: MutableList<String> = mutableListOf()
            list.add(uri)
            return create(playlists, list)
        }

        fun create(playlists: List<PlaylistParcelable>, uris: List<String>): AddToPlaylistDialog {
            return AddToPlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_PLAYLISTS to playlists,
                    EXTRA_TRACK_URI to uris
                )
            }
        }
    }
}