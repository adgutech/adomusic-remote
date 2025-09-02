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

package com.adgutech.adomusic.remote.ui.fragments.players.simple

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.TintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentSimplePlayerPlaybackControlsBinding
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerControlsFragment
import com.adgutech.adomusic.remote.ui.fragments.bases.goToAlbum
import com.adgutech.adomusic.remote.ui.fragments.bases.goToArtist
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.ui.color.MediaNotificationProcessor

/**
 * @author Hemanth S (h4h13).
 */

class SimplePlaybackControlsFragment :
    AbsPlayerControlsFragment(R.layout.fragment_simple_player_playback_controls) {

    private var _binding: FragmentSimplePlayerPlaybackControlsBinding? = null
    private val binding get() = _binding!!

    override val shuffleButton: ImageButton
        get() = binding.shuffleButton

    override val repeatButton: ImageButton
        get() = binding.repeatButton

    override val nextButton: ImageButton
        get() = binding.nextButton

    override val previousButton: ImageButton
        get() = binding.previousButton

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSimplePlayerPlaybackControlsBinding.bind(view)
        setUpPlayPauseFab()
        binding.title.isSelected = true
        binding.title.setOnClickListener {
            goToAlbum(requireActivity())
        }
        binding.text.setOnClickListener {
            goToArtist(requireActivity())
        }
    }

    private fun updateSong() {
        val track = AppRemoteHelper.currentTrack!!
        binding.title.text = track.name
        binding.text.text = Utils.getArtists(track.artists)
    }

    public override fun show() {
        binding.playPauseButton.animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(360f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    public override fun hide() {
        binding.playPauseButton.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun onUpdateProgressViews(progress: Long, total: Long) {
        binding.songCurrentProgress.text = String.format(
            "%s / %s",
            Utils.getReadableDurationString(progress),
            Utils.getReadableDurationString(total)
        )
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

        val colorFinal = if (preference.isAdaptiveColor) {
            color.primaryTextColor
        } else {
            accentColor()
        }

        volumeFragment?.setTintable(colorFinal)

        TintHelper.setTintAuto(
            binding.playPauseButton,
            MaterialValueHelper.getPrimaryTextColor(
                requireContext(),
                ColorUtil.isColorLight(colorFinal)
            ),
            false
        )
        TintHelper.setTintAuto(binding.playPauseButton, colorFinal, true)
        binding.text.setTextColor(colorFinal)

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
    }

    private fun setUpPlayPauseFab() {
        binding.playPauseButton.setOnClickListener {
            if (AppRemoteHelper.isPlaying) {
                AppRemoteHelper.pause()
            } else {
                AppRemoteHelper.resume()
            }
            it.showBounceAnimation()
        }
    }

    private fun updatePlayPauseDrawableState() {
        if (AppRemoteHelper.isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause_24dp)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
