/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */

package com.adgutech.adomusic.remote.ui.fragments.players.md3

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.TintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentMd3PlayerPlaybackControlsBinding
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.PlayPauseButtonOnClickHandler
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerControlsFragment
import com.adgutech.adomusic.remote.ui.fragments.bases.goToAlbum
import com.adgutech.adomusic.remote.ui.fragments.bases.goToArtist
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.applyColor
import com.adgutech.commons.extensions.ripAlpha
import com.adgutech.commons.ui.color.MediaNotificationProcessor
import com.google.android.material.slider.Slider

class MD3PlaybackControlsFragment :
    AbsPlayerControlsFragment(R.layout.fragment_md3_player_playback_controls) {

    private var _binding: FragmentMd3PlayerPlaybackControlsBinding? = null
    private val binding get() = _binding!!

    override val progressSlider: Slider
        get() = binding.progressSlider

    override val shuffleButton: ImageButton
        get() = binding.shuffleButton

    override val repeatButton: ImageButton
        get() = binding.repeatButton

    override val nextButton: ImageButton
        get() = binding.nextButton

    override val previousButton: ImageButton
        get() = binding.previousButton

    override val songTotalTime: TextView
        get() = binding.songTotalTime

    override val songCurrentProgress: TextView
        get() = binding.songCurrentProgress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMd3PlayerPlaybackControlsBinding.bind(view)
        setUpMusicControllers()
        binding.playPauseButton.setOnClickListener {
            if (AppRemoteHelper.isPlaying) {
                AppRemoteHelper.pause()
            } else {
                AppRemoteHelper.resume()
            }
        }
        binding.title.isSelected = true
        binding.text.isSelected = true
        binding.title.setOnClickListener {
            goToAlbum(requireActivity())
        }
        binding.text.setOnClickListener {
            goToArtist(requireActivity())
        }
    }

    override fun setColor(color: MediaNotificationProcessor) {
        val colorBg = ATHUtil.resolveColor(requireContext(), android.R.attr.colorBackground)
        if (ColorUtil.isColorLight(colorBg)) {
            lastPlaybackControlsColor =
                MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getSecondaryDisabledTextColor(requireContext(), true)
        } else {
            lastPlaybackControlsColor =
                MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getPrimaryDisabledTextColor(requireContext(), false)
        }

        val colorFinal = accentColor().ripAlpha()

        TintHelper.setTintAuto(
            binding.playPauseButton,
            MaterialValueHelper.getPrimaryTextColor(
                requireContext(),
                ColorUtil.isColorLight(colorFinal)
            ),
            false
        )
        binding.playPauseCard.setCardBackgroundColor(colorFinal)

        binding.progressSlider.applyColor(colorFinal)
        volumeFragment?.setTintable(colorFinal)
        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
        updatePlayPauseDrawableState()
    }

    private fun updateSong() {
        val track = AppRemoteHelper.currentTrack!!
        binding.title.text = track.name
        binding.text.text = Utils.getArtists(track.artists)
    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    override fun onPlayerStateChanged() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    private fun setUpPlayPauseFab() {
        binding.playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
        updatePlayPauseDrawableState()
    }

    private fun updatePlayPauseDrawableState() {
        if (AppRemoteHelper.isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause_outline_small)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_outline_small)
        }
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
    }

    public override fun show() {}

    public override fun hide() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}