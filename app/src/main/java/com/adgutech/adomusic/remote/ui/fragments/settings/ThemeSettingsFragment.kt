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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.edit
import androidx.core.view.doOnPreDraw
import androidx.preference.Preference
import code.name.monkey.appthemehelper.ACCENT_COLORS
import code.name.monkey.appthemehelper.ACCENT_COLORS_SUB
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATEColorPreference
import code.name.monkey.appthemehelper.common.prefs.supportv7.ATESwitchPreference
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.VersionUtils
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.ADAPTIVE_COLOR
import com.adgutech.adomusic.remote.ui.fragments.NowPlayingScreen.*
import com.adgutech.commons.preference.PreferenceBase.Companion.ACCENT_COLOR
import com.adgutech.commons.preference.PreferenceBase.Companion.BLACK_THEME
import com.adgutech.commons.preference.PreferenceBase.Companion.CUSTOM_FONT
import com.adgutech.commons.preference.PreferenceBase.Companion.DESATURATED_COLOR
import com.adgutech.commons.preference.PreferenceBase.Companion.GENERAL_THEME
import com.adgutech.commons.preference.PreferenceBase.Companion.MATERIAL_YOU
import com.adgutech.commons.preference.PreferenceBase.Companion.WALLPAPER_ACCENT
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.color.DynamicColors
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Created by Adolfo Gutiérrez on 06/14/25.
 */

class ThemeSettingsFragment : AbsSettingsFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    @SuppressLint("CheckResult")
    override fun invalidateSettings() {
        val generalTheme: Preference? = findPreference(GENERAL_THEME)
        generalTheme?.let {
            setSummary(it)
            it.setOnPreferenceChangeListener { _, newValue ->
                setSummary(it, newValue)
                ThemeStore.markChanged(requireContext())
                restartActivity()
                true
            }
        }

        val accentColorPref: ATEColorPreference? = findPreference(ACCENT_COLOR)
        val accentColor = ThemeStore.accentColor(requireContext())
        accentColorPref?.setColor(accentColor, ColorUtil.darkenColor(accentColor))
        accentColorPref?.setOnPreferenceClickListener {
            materialDialog().show {
                colorChooser(
                    initialSelection = accentColor,
                    showAlphaSelector = false,
                    colors = ACCENT_COLORS,
                    subColors = ACCENT_COLORS_SUB, allowCustomArgb = true
                ) { _, color ->
                    ThemeStore.editTheme(requireContext()).accentColor(color).commit()
                    restartActivity()
                }
            }
            return@setOnPreferenceClickListener true
        }
        val blackTheme: ATESwitchPreference? = findPreference(BLACK_THEME)
        blackTheme?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.title_preference_just_black_pro)
            } else getString(R.string.title_preference_just_black)

            setOnPreferenceChangeListener { _, _ ->
                if (!App.isProVersion()) {
                    showProToastAndNavigate(getString(R.string.pro_just_black))
                    return@setOnPreferenceChangeListener false
                }
                ThemeStore.markChanged(requireContext())
                if (VersionUtils.hasNougatMR()) {
                    requireActivity().setTheme(preference.themeResFromPrefValue("black"))
                }
                restartActivity()
                true
            }
        }

        val desaturatedColor: ATESwitchPreference? = findPreference(DESATURATED_COLOR)
        desaturatedColor?.setOnPreferenceChangeListener { _, value ->
            val desaturated = value as Boolean
            ThemeStore.prefs(requireContext()).edit {
                putBoolean("desaturated_color", desaturated)
            }
            preference.isDesaturatedColor = desaturated
            restartActivity()
            true
        }

        val materialYou: ATESwitchPreference? = findPreference(MATERIAL_YOU)
        materialYou?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                DynamicColors.applyToActivitiesIfAvailable(App.getInstance())
            }
            restartActivity()
            true
        }
        val wallpaperAccent: ATESwitchPreference? = findPreference(WALLPAPER_ACCENT)
        wallpaperAccent?.setOnPreferenceChangeListener { _, _ ->
            restartActivity()
            true
        }
        val customFont: ATESwitchPreference? = findPreference(CUSTOM_FONT)
        customFont?.setOnPreferenceChangeListener { _, _ ->
            restartActivity()
            true
        }

        val adaptiveColor: ATESwitchPreference? = findPreference(ADAPTIVE_COLOR)
        adaptiveColor?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.title_preference_adaptive_color_pro)
            } else getString(R.string.title_preference_adaptive_color)

            isEnabled = preference.nowPlayingScreen in listOf(Normal, Material)

            setOnPreferenceChangeListener { _, _ ->
                if (!App.isProVersion()) {
                    showProToastAndNavigate(getString(R.string.pro_adaptive_color))
                    return@setOnPreferenceChangeListener false
                }
                true
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_themes)
        val wallpaperAccent: ATESwitchPreference? = findPreference(WALLPAPER_ACCENT)
        wallpaperAccent?.isVisible = VersionUtils.hasOreoMR1() && !VersionUtils.hasS()
    }
}