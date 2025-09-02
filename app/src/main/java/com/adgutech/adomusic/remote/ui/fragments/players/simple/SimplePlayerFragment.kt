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
import androidx.appcompat.widget.Toolbar
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.FragmentSimplePlayerBinding
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.PlayerAlbumCoverFragment
import com.adgutech.commons.extensions.colorControlNormal
import com.adgutech.commons.extensions.drawAboveSystemBars
import com.adgutech.commons.extensions.whichFragment
import com.adgutech.commons.ui.color.MediaNotificationProcessor
import com.spotify.protocol.types.Track

/**
 * @author Hemanth S (h4h13).
 */

class SimplePlayerFragment : AbsPlayerFragment(R.layout.fragment_simple_player) {

    private var _binding: FragmentSimplePlayerBinding? = null
    private val binding get() = _binding!!

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private lateinit var controlsFragment: SimplePlaybackControlsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSimplePlayerBinding.bind(view)
        setUpSubFragments()
        setUpPlayerToolbar()
        playerToolbar().drawAboveSystemBars(requireContext())
    }

    private fun setUpSubFragments() {
        val playerAlbumCoverFragment: PlayerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment.setCallbacks(this)
        controlsFragment = whichFragment(R.id.playbackControlsFragment)
    }

    override fun onShow() {
        controlsFragment.show()
    }

    override fun onHide() {
        controlsFragment.hide()
    }

    override fun toolbarIconColor() = colorControlNormal()

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)
        controlsFragment.setColor(color)
        ToolbarContentTintHelper.colorizeToolbar(
            binding.playerToolbar,
            colorControlNormal(),
            requireActivity()
        )
    }

    override fun onFavoriteToggled() {
        toggleFavorite(AppRemoteHelper.currentTrack!!)
    }

    override fun toggleFavorite(track: Track) {
        super.toggleFavorite(track)
        updateIsFavorite()
    }

    override fun onServiceConnected() {
        updateIsFavorite()
    }

    override fun onPlayerStateChanged() {
        super.onPlayerStateChanged()
        updateIsFavorite()
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.inflateMenu(R.menu.menu_player)
        binding.playerToolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.playerToolbar.setOnMenuItemClickListener(this)
        ToolbarContentTintHelper.colorizeToolbar(
            binding.playerToolbar,
            colorControlNormal(),
            requireActivity()
        )
        setTextEqualizer()
    }

    private fun setTextEqualizer() {
        binding.playerToolbar.menu?.findItem(R.id.action_equalizer)?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.action_equalizer_pro)
            } else getString(R.string.action_equalizer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
