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
import com.adgutech.adomusic.remote.EXTRA_PLAYLIST
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.extraNotNull
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.colorButtons
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EditPlaylistWithDescDialog : DialogFragment() {

    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    companion object {
        fun create(playlist: Playlist): EditPlaylistWithDescDialog {
            return EditPlaylistWithDescDialog().apply {
                arguments = bundleOf(
                    EXTRA_PLAYLIST to playlist
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val playlist = extraNotNull<Playlist>(EXTRA_PLAYLIST).value
        val layout = layoutInflater.inflate(R.layout.dialog_edit_playlist, null)

        val playlistNameView: TextInputEditText = layout.findViewById(R.id.playlistName)
        val playlistNameContainer: TextInputLayout = layout.findViewById(R.id.playlistNameContainer)
        val playlistDescriptionView: TextInputEditText = layout.findViewById(R.id.playlistDescription)
        val playlistDescriptionContainer: TextInputLayout = layout.findViewById(R.id.playlistDescriptionContainer)

        playlistNameContainer.accentColor(requireContext())
        playlistDescriptionContainer.accentColor(requireContext())

        playlistNameView.setText(playlist.name)
        playlistDescriptionView.setText(playlist.description)

        return materialDialog(R.string.title_edit_playlist)
            .setView(layout)
            .setPositiveButton(
                R.string.action_accept
            ) { _, _ ->
                val playlistName = playlistNameView.text.toString()
                val playlistDescription = playlistDescriptionView.text.toString()
                if (playlistName.isNotEmpty()) {
                    libraryViewModel.changePlaylistDetails(playlist.id!!, playlistName, playlistDescription)
                } else {
                    playlistNameContainer.error = getString(R.string.text_create_playlist_error)
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }
}