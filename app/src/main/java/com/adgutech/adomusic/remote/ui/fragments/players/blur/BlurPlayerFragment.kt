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

package com.adgutech.adomusic.remote.ui.fragments.players.blur

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.FragmentBlurPlayerBinding
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.BlurTransformation
import com.adgutech.adomusic.remote.glide.GlideExtension.trackCoverOptions
import com.adgutech.adomusic.remote.glide.crossfadeListener
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.NEW_BLUR_AMOUNT
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.PlayerAlbumCoverFragment
import com.adgutech.commons.extensions.drawAboveSystemBars
import com.adgutech.commons.extensions.whichFragment
import com.adgutech.commons.ui.color.MediaNotificationProcessor
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.Track

class BlurPlayerFragment : AbsPlayerFragment(R.layout.fragment_blur_player),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var lastRequest: RequestBuilder<Drawable>? = null

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    private lateinit var playbackControlsFragment: BlurPlaybackControlsFragment

    private var lastColor: Int = 0

    private var _binding: FragmentBlurPlayerBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlurPlayerBinding.bind(view)
        setUpSubFragments()
        setUpPlayerToolbar()
        binding.playerToolbar.drawAboveSystemBars(requireContext())
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = whichFragment(R.id.playbackControlsFragment)
        val playerAlbumCoverFragment: PlayerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            ToolbarContentTintHelper.colorizeToolbar(this, Color.WHITE, activity)
        }.setOnMenuItemClickListener(this)

        setTextEqualizer()
    }

    override fun onFavoriteToggled() {
        toggleFavorite(AppRemoteHelper.currentTrack!!)
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        playbackControlsFragment.setColor(color)
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)
        ToolbarContentTintHelper.colorizeToolbar(binding.playerToolbar, Color.WHITE, activity)
    }

    override fun toggleFavorite(track: Track) {
        super.toggleFavorite(track)
        updateIsFavorite()
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override val paletteColor: Int
        get() = lastColor

    private fun setTextEqualizer() {
        binding.playerToolbar.menu?.findItem(R.id.action_equalizer)?.apply {
            title = if (!App.isProVersion()) {
                getString(R.string.action_equalizer_pro)
            } else {
                getString(R.string.action_equalizer)
            }
        }
    }

    private fun updateBlur() {
        // https://github.com/bumptech/glide/issues/527#issuecomment-148840717
        val track = AppRemoteHelper.currentTrack!!
        AppRemoteHelper.getImage(track.imageUri, Image.Dimension.LARGE) { bitmap ->
            Glide.with(this)
                .load(bitmap)
                .trackCoverOptions(track)
                .transform(
                    BlurTransformation.Builder(requireContext())
                        .blurRadius(preference.blurAmount.toFloat())
                        .build()
                ).thumbnail(lastRequest)
                .error(Glide.with(this).load(ColorDrawable(Color.DKGRAY)).fitCenter())
                .also {
                    lastRequest = it.clone()
                    it.crossfadeListener()
                        .into(binding.colorBackground)
                }
        }
    }

    override fun onServiceConnected() {
        updateIsFavorite()
        updateBlur()
    }

    override fun onPlayerStateChanged() {
        super.onPlayerStateChanged()
        updateIsFavorite()
        updateBlur()
    }

    override fun onPause() {
        super.onPause()
        lastRequest = null
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
        _binding = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == NEW_BLUR_AMOUNT) {
            updateBlur()
        }
    }
}
