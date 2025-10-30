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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.doOnPreDraw
import androidx.preference.Preference
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATEListPreference
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATESwitchPreference
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.extensions.installLanguageAndRecreate
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.ADJUST_VOLUME
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.ARTIST_TIME_RANGE
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.PAUSE_ON_ZERO_VOLUME
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.TRACK_TIME_RANGE
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.commons.preference.PreferenceBase.Companion.LANGUAGE_CODE
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.activityViewModel

/**
 * Created by Adolfo Gutiérrez on 07/11/25.
 */

class GeneralSettingsFragment : AbsSettingsFragment() {

    private val libraryViewModel: LibraryViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val prefArtist: Preference? = findPreference(ARTIST_TIME_RANGE)
        prefArtist?.setOnPreferenceChangeListener { lastAdded, newValue ->
            setSummary(lastAdded, newValue)
            libraryViewModel.forceReload(ReloadType.HOME_SECTIONS)
            true
        }

        val prefTrack: Preference? = findPreference(TRACK_TIME_RANGE)
        prefTrack?.setOnPreferenceChangeListener { lastAdded, newValue ->
            setSummary(lastAdded, newValue)
            libraryViewModel.forceReload(ReloadType.HOME_SECTIONS)
            true
        }

        val pauseOnZero: ATESwitchPreference? = findPreference(PAUSE_ON_ZERO_VOLUME)
        pauseOnZero?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.title_preference_pause_on_zero_pro)
            } else getString(R.string.title_preference_pause_on_zero)

            setOnPreferenceChangeListener { _, _ ->
                if (!App.isProVersion()) {
                    showProToastAndNavigate(getString(R.string.pro_pause_on_zero))
                    return@setOnPreferenceChangeListener false
                }
                true
            }
        }

        val adjustVolume: ATESwitchPreference? = findPreference(ADJUST_VOLUME)
        adjustVolume?.setOnPreferenceChangeListener { _, _ ->
            restartActivity()
            return@setOnPreferenceChangeListener true
        }

        val languagePreference: Preference? = findPreference(LANGUAGE_CODE)
        languagePreference?.setOnPreferenceChangeListener { prefs, newValue ->
            setSummary(prefs, newValue)
            if (newValue as? String == "auto") {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            } else {
                // Install the languages from Play Store first and then set the application locale
                requireActivity().installLanguageAndRecreate(newValue.toString()) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(
                            newValue as? String
                        )
                    )
                }
            }
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preference.languageCode =
            AppCompatDelegate.getApplicationLocales().toLanguageTags().ifEmpty { "auto" }
        addPreferencesFromResource(R.xml.preference_general)
    }

    override fun invalidateSettings() {
        val languagePreference: ATEListPreference? = findPreference(LANGUAGE_CODE)
        languagePreference?.setOnPreferenceChangeListener { _, _ ->
            restartActivity()
            return@setOnPreferenceChangeListener true
        }
    }
}