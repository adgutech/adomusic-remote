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

import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import code.name.monkey.appthemehelper.ThemeStore
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentVolumeBinding
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.volume.AudioVolumeObserver
import com.adgutech.adomusic.remote.volume.OnAudioVolumeChangedListener
import com.adgutech.commons.extensions.applyColor
import com.google.android.material.slider.Slider

class VolumeFragment : Fragment(), Slider.OnChangeListener, OnAudioVolumeChangedListener,
    View.OnClickListener {

    private var _binding: FragmentVolumeBinding? = null
    private val binding get() = _binding!!

    private var audioVolumeObserver: AudioVolumeObserver? = null

    private val audioManager: AudioManager
        get() = requireContext().getSystemService()!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVolumeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTintable(ThemeStore.accentColor(requireContext()))
        binding.volumeDown.setOnClickListener(this)
        binding.volumeUp.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (audioVolumeObserver == null) {
            audioVolumeObserver = AudioVolumeObserver(requireActivity())
        }
        audioVolumeObserver?.register(AudioManager.STREAM_MUSIC, this)

        val audioManager = audioManager
        binding.volumeSeekBar.valueTo =
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.value =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.addOnChangeListener(this)
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        if (_binding != null) {
            binding.volumeSeekBar.valueTo = maxVolume.toFloat()
            binding.volumeSeekBar.value = currentVolume.toFloat()
            binding.volumeDown.setImageResource(if (currentVolume == 0) R.drawable.ic_volume_off_24dp else R.drawable.ic_volume_down_24dp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVolumeObserver?.unregister()
        _binding = null
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        val audioManager = audioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value.toInt(), 0)
        setPauseWhenZeroVolume(value < 1f)
        binding.volumeDown.setImageResource(if (value == 0f) R.drawable.ic_volume_off_24dp else R.drawable.ic_volume_down_24dp)
    }

    override fun onClick(view: View) {
        val audioManager = audioManager
        when (view.id) {
            R.id.volumeDown -> audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0
            )

            R.id.volumeUp -> audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0
            )
        }
    }

    fun tintWhiteColor() {
        val color = Color.WHITE
        binding.volumeDown.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.volumeUp.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.volumeSeekBar.applyColor(color)
    }

    fun setTintable(color: Int) {
        binding.volumeSeekBar.applyColor(color)
    }

    private fun setPauseWhenZeroVolume(pauseWhenZeroVolume: Boolean) {
        if (preference.isPauseOnZeroVolume)
            if (AppRemoteHelper.isPlaying && pauseWhenZeroVolume)
                AppRemoteHelper.pause()
    }

    fun setTintableColor(color: Int) {
        binding.volumeDown.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.volumeUp.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        // TintHelper.setTint(volumeSeekBar, color, false)
        binding.volumeSeekBar.applyColor(color)
    }

    companion object {
        fun newInstance(): VolumeFragment {
            return VolumeFragment()
        }
    }
}
