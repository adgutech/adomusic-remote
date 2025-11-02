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
import android.content.Context
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.FragmentMiniPlayerBinding
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.trackCoverOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.MusicProgressViewUpdateHelper
import com.adgutech.adomusic.remote.helpers.PlayPauseButtonOnClickHandler
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsSpotifyServiceFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.isTablet
import com.adgutech.commons.extensions.show
import com.adgutech.commons.extensions.textColorPrimary
import com.adgutech.commons.extensions.textColorSecondary
import com.bumptech.glide.Glide
import com.spotify.protocol.types.Image.Dimension
import kotlin.math.abs

/**
 * Created by Adolfo Gutiérrez on 05/09/2025.
 */

open class MiniPlayerFragment : AbsSpotifyServiceFragment(R.layout.fragment_mini_player),
    MusicProgressViewUpdateHelper.Callback, View.OnClickListener {

    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.actionNext -> AppRemoteHelper.playNextTrack()
            R.id.actionPrevious -> AppRemoteHelper.playPreviousTrack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMiniPlayerBinding.bind(view)
        view.setOnTouchListener(FlingPlayBackController(requireContext()))
        setUpMiniPlayer()
        setUpButtons()
    }

    fun setUpButtons() {
        if (isTablet) {
            binding.actionNext.show()
            binding.actionPrevious.show()
        } else {
            binding.actionNext.isVisible = preference.isExtraControls
            binding.actionPrevious.isVisible = preference.isExtraControls
        }
        binding.actionNext.setOnClickListener(this)
        binding.actionPrevious.setOnClickListener(this)
    }

    private fun setUpMiniPlayer() {
        setUpPlayPauseButton()
        binding.progressBar.accentColor(requireContext())
    }

    private fun setUpPlayPauseButton() {
        binding.actionPlayPause.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updateSongTitle() {
        val track = AppRemoteHelper.currentTrack
        if (track != null) {
            val title = track.name.toSpannable()
            title.setSpan(ForegroundColorSpan(textColorPrimary()), 0, title.length, 0)

            val artists = Utils.getArtists(track.artists)
            val text = artists.toSpannable()
            text.setSpan(ForegroundColorSpan(textColorSecondary()), 0, text.length, 0)

            binding.miniPlayerTitle.isSelected = true
            binding.miniPlayerTitle.text = title
            binding.miniPlayerText.isSelected = true
            binding.miniPlayerText.text = text
        }
    }

    private fun updateSongCover() {
        val track = AppRemoteHelper.currentTrack!!
        AppRemoteHelper.getImage(track.imageUri, Dimension.THUMBNAIL) { bitmap ->
            Glide.with(requireContext())
                .load(GlideExtension.getTrackCoverModel(bitmap))
                .transition(GlideExtension.getDefaultTransition())
                .trackCoverOptions(track)
                .into(binding.image)
        }
    }

    override fun onServiceConnected() {
        updateSongTitle()
        updateSongCover()
        updatePlayPauseDrawableState()
    }

    override fun onPlayerStateChanged() {
        updateSongTitle()
        updateSongCover()
        updatePlayPauseDrawableState()
    }

    override fun onUpdateProgressViews(progress: Long, total: Long) {
        binding.progressBar.max = total.toInt()
        binding.progressBar.progress = progress.toInt()
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    private fun updatePlayPauseDrawableState() {
        if (AppRemoteHelper.isPlaying) {
            binding.actionPlayPause.setImageResource(R.drawable.ic_pause_24dp)
        } else {
            binding.actionPlayPause.setImageResource(R.drawable.ic_play_arrow_24dp)
        }
    }

    inner class FlingPlayBackController(context: Context) : View.OnTouchListener {

        private var flingPlayBackController = GestureDetector(context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (abs(velocityX) > abs(velocityY)) {
                        if (velocityX < 0) {
                            AppRemoteHelper.playNextTrack()
                            return true
                        } else if (velocityX > 0) {
                            AppRemoteHelper.playPreviousTrack()
                            return true
                        }
                    }
                    return false
                }
            })

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return flingPlayBackController.onTouchEvent(event)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}