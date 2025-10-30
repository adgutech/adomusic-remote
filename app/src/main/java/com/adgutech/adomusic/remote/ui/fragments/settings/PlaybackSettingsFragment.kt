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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.preference.Preference
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATESwitchPreference
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.NOW_PLAYING_SCREEN
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.SNOW_FALL
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.VOLUME_VISIBILITY_MODE
import com.adgutech.adomusic.remote.utils.Utils
import com.google.android.material.transition.MaterialSharedAxis

class PlaybackSettingsFragment : AbsSettingsFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {
        updateNowPlayingScreenSummary()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_playback)
    }

    private fun updateNowPlayingScreenSummary() {
        val pref: Preference? = findPreference(NOW_PLAYING_SCREEN)
        pref?.setSummary(preference.nowPlayingScreen.titleRes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference.registerOnSharedPreferenceChangedListener(this)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val volumeControls: ATESwitchPreference? = findPreference(VOLUME_VISIBILITY_MODE)
        volumeControls?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.title_preference_volume_controls_pro)
            } else getString(R.string.title_preference_volume_controls)

            setOnPreferenceChangeListener { _, _ ->
                if (!App.isProVersion()) {
                    showProToastAndNavigate(getString(R.string.pro_volume_controls))
                    return@setOnPreferenceChangeListener false
                }
                true
            }
        }
        val snowfall: ATESwitchPreference? = findPreference(SNOW_FALL)
        snowfall?.isEnabled = BuildConfig.DEBUG || Utils.isSnowfallAvailable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preference.unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            NOW_PLAYING_SCREEN -> updateNowPlayingScreenSummary()
        }
    }
}