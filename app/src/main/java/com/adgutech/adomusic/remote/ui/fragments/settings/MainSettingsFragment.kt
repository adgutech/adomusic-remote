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

package com.adgutech.adomusic.remote.ui.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SPOTIFY_PREFERENCE
import com.adgutech.adomusic.remote.databinding.FragmentMainSettingsBinding
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.commons.extensions.drawAboveSystemBarsWithPadding
import com.google.android.material.transition.MaterialSharedAxis
import androidx.core.net.toUri
import com.adgutech.adomusic.remote.extensions.hasSpotifyInstalled

/**
 * Created by Adolfo Gutiérrez on 06/07/25.
 */

class MainSettingsFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            preferenceThemes.setOnClickListener(this@MainSettingsFragment)
            preferencePlayback.setOnClickListener(this@MainSettingsFragment)
            preferencePersonalize.setOnClickListener(this@MainSettingsFragment)
            preferenceGeneral.setOnClickListener(this@MainSettingsFragment)
            preferenceSpotifySettings.setOnClickListener(this@MainSettingsFragment)
            preferenceAbout.setOnClickListener(this@MainSettingsFragment)
        }

        binding.container.drawAboveSystemBarsWithPadding(requireContext())
    }

    override fun onClick(v: View) {
        if (v.id == R.id.preferenceSpotifySettings) {
            goToSpotifySettings()
            return
        }
        exitTransition =
            MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
        reenterTransition =
            MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(
            when (v.id) {
                R.id.preferenceThemes -> R.id.action_mainSettingsFragment_to_themeSettingsFragment
                R.id.preferencePlayback -> R.id.action_mainSettingsFragment_to_playbackSettingsFragment
                R.id.preferencePersonalize -> R.id.action_mainSettingsFragment_to_personalizeSettingsFragment
                R.id.preferenceGeneral -> R.id.action_mainSettingsFragment_to_generalSettingsFragment
                R.id.preferenceAbout -> R.id.action_mainSettingsFragment_to_aboutSettingsFragment
                else -> R.id.mainSettingsFragment
            }
        )
    }

    private fun goToSpotifySettings() {
        if (hasSpotifyInstalled()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(SPOTIFY_PREFERENCE.toUri())
            startActivity(intent)
        } else {
            mainActivity.showSpotifyNotInstalledDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}