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

package com.adgutech.adomusic.remote.ui.fragments.players.normal

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.FragmentPlayerBinding
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.SNOW_FALL
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.PlayerAlbumCoverFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.colorControlNormal
import com.adgutech.commons.extensions.drawAboveSystemBars
import com.adgutech.commons.extensions.isColorLight
import com.adgutech.commons.extensions.surfaceColor
import com.adgutech.commons.extensions.whichFragment
import com.adgutech.commons.ui.color.MediaNotificationProcessor
import com.adgutech.commons.ui.views.DrawableGradient
import com.adgutech.commons.utils.ViewUtil
import com.spotify.protocol.types.Track

class PlayerFragment : AbsPlayerFragment(R.layout.fragment_player),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private lateinit var controlsFragment: PlayerPlaybackControlsFragment
    private var valueAnimator: ValueAnimator? = null

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!


    private fun colorize(i: Int) {
        if (valueAnimator != null) {
            valueAnimator?.cancel()
        }

        valueAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            surfaceColor(),
            i
        )
        valueAnimator?.addUpdateListener { animation ->
            if (isAdded) {
                val drawable = DrawableGradient(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        animation.animatedValue as Int,
                        surfaceColor()
                    ), 0
                )
                binding.colorGradientBackground.background = drawable
            }
        }
        valueAnimator?.setDuration(ViewUtil.ANIM_TIME.toLong())?.start()
    }

    override fun onShow() {
        controlsFragment.show()
    }

    override fun onHide() {
        controlsFragment.hide()
    }

    override fun toolbarIconColor() = colorControlNormal()

    override fun onColorChanged(color: MediaNotificationProcessor) {
        controlsFragment.setColor(color)
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)

        ToolbarContentTintHelper.colorizeToolbar(
            binding.playerToolbar,
            colorControlNormal(),
            requireActivity()
        )

        if (preference.isAdaptiveColor) {
            colorize(color.backgroundColor)
        }
    }

    override fun toggleFavorite(track: Track) {
        super.toggleFavorite(track)
        updateIsFavorite()
    }

    override fun onFavoriteToggled() {
        toggleFavorite(AppRemoteHelper.currentTrack!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)
        setUpSubFragments()
        setUpPlayerToolbar()
        startOrStopSnow(preference.isSnowFall)

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
        playerToolbar().drawAboveSystemBars(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
        _binding = null
    }

    private fun setUpSubFragments() {
        controlsFragment = whichFragment(R.id.playbackControlsFragment)
        val playerAlbumCoverFragment: PlayerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.inflateMenu(R.menu.menu_player)
        //binding.playerToolbar.menu.setUpWithIcons()
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
            } else {
                getString(R.string.action_equalizer)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == SNOW_FALL) {
            startOrStopSnow(preference.isSnowFall)
        }
    }

    private fun startOrStopSnow(isSnowFalling: Boolean) {
        if (_binding == null) return
        if (isSnowFalling && !surfaceColor().isColorLight) {
            binding.snowfallView.isVisible = true
            binding.snowfallView.restartFalling()
        } else {
            binding.snowfallView.isVisible = false
            binding.snowfallView.stopFalling()
        }
        if (!BuildConfig.DEBUG) {
            if (!Utils.isSnowfallAvailable()) {
                binding.snowfallView.isVisible = false
            }
        }
    }

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    companion object {

        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}
