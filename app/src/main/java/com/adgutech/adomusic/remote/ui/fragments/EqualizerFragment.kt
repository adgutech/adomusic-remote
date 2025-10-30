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

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentEqualizerBinding
import com.adgutech.adomusic.remote.databinding.ItemEqualizerBandBinding
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.EqualizerHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.EQUALIZER_ENABLED
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.adomusic.remote.ui.dialogs.EqualizerPresetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.addAccentColor
import com.adgutech.commons.extensions.showToast
import dev.chrisbanes.insetter.applyInsetter
import kotlin.math.roundToInt

/**
 * Created by Adolfo Gutiérrez on 07/18/2025.
 */

class EqualizerFragment : Fragment(R.layout.fragment_equalizer),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var _binding: FragmentEqualizerBinding? = null
    private val binding get() = _binding!!

    private var bands = HashMap<Short, Int>()
    private var bandSeekBars = ArrayList<SeekBar>()

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEqualizerBinding.bind(view)
        mainActivity.setSupportActionBar(binding.appBarLayout.toolbar)
        mainActivity.supportActionBar?.title = null

        binding.equalizerContainer.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.switchEqualizer.apply {
            isChecked = preference.isEqualizerEnabled
            setOnCheckedChangeListener { _, isChecked ->
                preference.isEqualizerEnabled = isChecked
            }
        }

        setupToolbar()
        setupEqualizer()
        refreshOnOffText()
        mainActivity.collapsePanel()
    }

    override fun onResume() {
        super.onResume()
        preference.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            EQUALIZER_ENABLED -> {
                setupEqualizer()
                refreshOnOffText()
            }
        }
    }

    private fun setupToolbar() {
        with (binding.appBarLayout.toolbar) {
            title = getString(R.string.action_equalizer)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupEqualizer() {
        val equalizer = EqualizerHelper.instance
        try {
            equalizer.enabled = preference.isEqualizerEnabled
        } catch (e: ExceptionInInitializerError) {
            e.printStackTrace()
        }
        setupBands(equalizer)
        setupPresets(equalizer)
    }

    @SuppressLint("SetTextI18n")
    private fun setupBands(equalizer: Equalizer) {

        binding.maxDecibels.isEnabled = preference.isEqualizerEnabled
        binding.minDecibels.isEnabled = preference.isEqualizerEnabled
        binding.zeroDecibels.isEnabled = preference.isEqualizerEnabled

        val minValue = equalizer.bandLevelRange[0]
        val maxValue = equalizer.bandLevelRange[1]
        binding.maxDecibels.text = "+${maxValue / 100} dB"
        binding.minDecibels.text = "${minValue / 100} dB"
        binding.zeroDecibels.text = "${(minValue + maxValue)} dB"

        bandSeekBars.clear()
        binding.equalizerBandsHolder.removeAllViews()

        val bandType = object : TypeToken<HashMap<Short, Int>>() {}.type
        bands = Gson().fromJson<HashMap<Short, Int>>(preference.equalizerBands, bandType)
            ?: HashMap()

        for (band in 0 until equalizer.numberOfBands) {
            val frequency = equalizer.getCenterFreq(band.toShort()) / 1000.0
            val formatted = EqualizerHelper.setFormatFrequency(frequency)

            ItemEqualizerBandBinding.inflate(
                layoutInflater,
                binding.equalizerBandsHolder,
                false
            ).apply {
                binding.equalizerBandsHolder.addView(root)
                bandSeekBars.add(equalizerBandSeekBar)
                equalizerBandLabel.isEnabled = preference.isEqualizerEnabled
                equalizerBandSeekBar.isEnabled = preference.isEqualizerEnabled
                equalizerBandLabel.text = formatted
                equalizerBandSeekBar.max = maxValue - minValue

                equalizerBandSeekBar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {
                            val newProgress = (progress / 100.0).roundToInt() * 100
                            equalizerBandSeekBar.progress = newProgress

                            val newValue = newProgress + minValue
                            try {
                                if (equalizer.getBandLevel(band.toShort()) != newValue.toShort()) {
                                    equalizer.setBandLevel(band.toShort(), newValue.toShort())
                                    bands[band.toShort()] = newValue
                                }
                            } catch (ignored: Exception) {
                            }
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        draggingStarted(equalizer)
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        bands[band.toShort()] = equalizerBandSeekBar.progress
                        preference.equalizerBands = Gson().toJson(bands)
                    }
                })
                if (preference.isEqualizerEnabled) {
                    equalizerBandSeekBar.addAccentColor(requireContext())
                }
            }
        }
    }

    private fun draggingStarted(equalizer: Equalizer) {
        binding.btnPreset.text = getString(R.string.action_equalizer_custom)
        preference.equalizerPreset = EqualizerHelper.EQUALIZER_PRESET_CUSTOM
        for (band in 0 until equalizer.numberOfBands) {
            bands[band.toShort()] = bandSeekBars[band].progress
        }
    }

    private fun setupPresets(equalizer: Equalizer) {
        try {
            presetChanged(preference.equalizerPreset, equalizer)
        } catch (e: Exception) {
            showToast(R.string.text_unknown_error_occurred)
            logE(e.toString())
            preference.equalizerPreset = EqualizerHelper.EQUALIZER_PRESET_CUSTOM
        }

        binding.btnPreset.apply {
            isEnabled = preference.isEqualizerEnabled
            setOnClickListener {
                EqualizerPresetDialog(equalizer) { presetId ->
                    try {
                        preference.equalizerPreset = presetId as Int
                        presetChanged(presetId, equalizer)
                    } catch (e: Exception) {
                        showToast(R.string.text_unknown_error_occurred)
                        logE(e.toString())
                        preference.equalizerPreset = EqualizerHelper.EQUALIZER_PRESET_CUSTOM
                    }
                }.show(childFragmentManager, "EqualizerPresetDialog")
            }
            if (preference.isEqualizerEnabled){
                accentColor(requireContext())
            }
        }
    }

    private fun presetChanged(presetId: Int, equalizer: Equalizer) {
        if (presetId == EqualizerHelper.EQUALIZER_PRESET_CUSTOM) {
            binding.btnPreset.text = getString(R.string.action_equalizer_custom)

            for (band in 0 until equalizer.numberOfBands) {
                val minValue = equalizer.bandLevelRange[0]
                val progress = if (bands.containsKey(band.toShort())) {
                    bands[band.toShort()]
                } else {
                    val maxValue = equalizer.bandLevelRange[1]
                    (maxValue - minValue) / 2
                }

                bandSeekBars[band].progress = progress!!.toInt()
                val newValue = progress + minValue
                equalizer.setBandLevel(band.toShort(), newValue.toShort())
            }
        } else {
            val presetName = equalizer.getPresetName(presetId.toShort())
            if (presetName.isEmpty()) {
                preference.equalizerPreset = EqualizerHelper.EQUALIZER_PRESET_CUSTOM
                binding.btnPreset.text = getString(R.string.action_equalizer_custom)
            } else {
                binding.btnPreset.text = presetName
            }

            equalizer.usePreset(presetId.toShort())

            val lowestBandLevel = equalizer.bandLevelRange?.get(0)
            for (band in 0 until equalizer.numberOfBands) {
                val level = equalizer.getBandLevel(band.toShort()).minus(lowestBandLevel!!)
                bandSeekBars[band].progress = level
            }
        }
    }

    private fun refreshOnOffText() {
        if (preference.isEqualizerEnabled) {
            binding.switchEqualizer.setText(R.string.text_on)
        } else {
            binding.switchEqualizer.setText(R.string.text_off)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preference.unregisterOnSharedPreferenceChangedListener(this)
        mainActivity.expandPanel()
        _binding = null
    }
}