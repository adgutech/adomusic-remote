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
import android.text.TextUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.adgutech.adomusic.remote.EXTRA_USER_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.DialogPlaylistBinding
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.commons.extensions.colorButtons
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CreatePlaylistDialog : DialogFragment() {

    private var _binding: DialogPlaylistBinding? = null
    private val binding get() = _binding!!

    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPlaylistBinding.inflate(layoutInflater)

        val userId = arguments?.getString(EXTRA_USER_ID)!!

        val playlistNameView = binding.playlistName
        val playlistNameContainer = binding.playlistNameContainer

        return materialDialog(R.string.title_new_playlist)
            .setView(binding.root)
            .setPositiveButton(
                R.string.action_create
            ) { _, _ ->
                val playlistName = playlistNameView.text.toString()
                if (!TextUtils.isEmpty(playlistName)) {
                    libraryViewModel.createPlaylist(userId, playlistName)
                } else {
                    playlistNameContainer.error = getString(R.string.text_create_playlist_error)
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }

    companion object {
        fun create(userId: String?): CreatePlaylistDialog {
            return CreatePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_USER_ID to userId)
            }
        }
    }
}