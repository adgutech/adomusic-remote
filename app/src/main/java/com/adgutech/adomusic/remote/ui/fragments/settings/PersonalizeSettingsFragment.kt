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
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATEListPreference
import com.adgutech.adomusic.remote.R
import com.adgutech.commons.preference.PreferenceBase.Companion.APPBAR_MODE
import com.adgutech.commons.preference.PreferenceBase.Companion.TAB_TITLES_MODE
import com.google.android.material.transition.MaterialSharedAxis

class PersonalizeSettingsFragment : AbsSettingsFragment() {

    override fun invalidateSettings() {}

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_personalize)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val tabTextMode: ATEListPreference? = findPreference(TAB_TITLES_MODE)
        tabTextMode?.setOnPreferenceChangeListener { prefs, newValue ->
            setSummary(prefs, newValue)
            true
        }
        val appBarMode: ATEListPreference? = findPreference(APPBAR_MODE)
        appBarMode?.setOnPreferenceChangeListener { _, _ ->
            restartActivity()
            true
        }
    }
}