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

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import code.name.monkey.appthemehelper.ThemeStore
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentSettingsBinding
import com.adgutech.adomusic.remote.extensions.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Created by Adolfo Gutiérrez on 06/07/25.
 */

class SettingsFragment : Fragment(R.layout.fragment_settings), ColorCallback {

    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        setupToolbar()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        ThemeStore.editTheme(requireContext()).accentColor(color).commit()
        //if (hasVersionNougatMR1) {
        //for dynamic
        //}
        activity?.recreate()
    }

    private fun setupToolbar() {
        val navController: NavController = findNavController(R.id.contentFrame)
        with(binding.appBarLayout.toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            isTitleCentered = false
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarLayout.title =
                navController.currentDestination?.let { getStringFromDestination(it) }.toString()
        }
    }

    private fun getStringFromDestination(currentDestination: NavDestination): String {
        val idRes = when (currentDestination.id) {
            R.id.mainSettingsFragment -> R.string.title_settings
            R.id.themeSettingsFragment -> R.string.preference_category_title_themes
            R.id.playbackSettingsFragment -> R.string.preference_category_title_playback
            R.id.personalizeSettingsFragment -> R.string.preference_category_title_personalize
            R.id.generalSettingsFragment -> R.string.preference_category_title_general
            R.id.aboutFragment -> R.string.preference_category_title_about
            else -> R.id.action_settings
        }
        return getString(idRes)
    }
}