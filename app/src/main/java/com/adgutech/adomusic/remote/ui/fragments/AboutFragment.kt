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

package com.adgutech.adomusic.remote.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.FragmentAboutBinding
import com.adgutech.adomusic.remote.ui.dialogs.LicensesDialogFragment
import com.adgutech.commons.GOOGLE_PLAY_DETAILS
import com.adgutech.commons.PAYPAL_URL
import com.adgutech.commons.PRIVACY_POLICY_URL
import com.adgutech.commons.extensions.openUrl
import com.google.android.material.transition.MaterialSharedAxis
import dev.chrisbanes.insetter.applyInsetter

class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.aboutContent.root.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.aboutContent.apply {
            text.text = getAppVersion()

            appRateButton.setOnClickListener {
                val uri = Uri.parse(GOOGLE_PLAY_DETAILS)
                    .buildUpon()
                    .appendQueryParameter("id", requireActivity().packageName)
                    .build()
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            donateButton.setOnClickListener {
                openUrl(PAYPAL_URL)
            }
            shareAppButton.setOnClickListener {
                shareApp()
            }
            licensesButton.setOnClickListener {
                LicensesDialogFragment().show(childFragmentManager, "LicensesDialogFragment")
            }
            privacyPolicyButton.setOnClickListener {
                openUrl(PRIVACY_POLICY_URL)
            }
        }
    }

    private fun getAppVersion(): String {
        return try {
            val isPro = if (App.isProVersion()) "Pro" else "Free"
            val packageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            "${getString(R.string.title_version)} ${packageInfo.versionName} $isPro"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder(requireActivity()).setType("text/plain")
            .setChooserTitle(R.string.title_share_app)
            .setText(String.format(getString(R.string.text_share_app), requireActivity().packageName))
            .startChooser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}