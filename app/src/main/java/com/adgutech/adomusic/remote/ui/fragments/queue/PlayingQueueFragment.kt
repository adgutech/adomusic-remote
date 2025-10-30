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

package com.adgutech.adomusic.remote.ui.fragments.queue

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.track.PlayingQueueAdapter
import com.adgutech.adomusic.remote.databinding.FragmentPlayingQueueBinding
import com.adgutech.adomusic.remote.extensions.logW
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.extensions.isVisible
import com.adgutech.commons.utils.FastScrollerThemeHelper.create
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator

class PlayingQueueFragment : AbsMainActivityFragment(R.layout.fragment_playing_queue) {

    private var _binding: FragmentPlayingQueueBinding? = null
    private val binding get() = _binding!!
    private lateinit var playingQueueAdapter: PlayingQueueAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayingQueueBinding.bind(view)
        setupToolbar()
        setupRecyclerView()
        mainActivity.collapsePanel()
        if (App.isSpotifyPremium()) {
            libraryViewModel.getUserQueue().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        binding.recyclerView.isVisible()
                        binding.premiumRequired.isGone()
                        if (result.data.isNotEmpty()) {
                            playingQueueAdapter.swapDataSet(result.data)
                        } else {
                            playingQueueAdapter.swapDataSet(listOf())
                        }
                    }
                    is Result.Error -> {
                        binding.recyclerView.isGone()
                        binding.premiumRequired.isVisible()
                        logW("Error to load user queue: ${result.error}")
                    }
                }
            }
        } else {
            binding.recyclerView.isGone()
            binding.premiumRequired.isVisible()
            logW("Spotify Premium required.")
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivity.expandPanel()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        binding.appBarLayout.toolbar.subtitle = ""
        libraryViewModel.fetchUserQueue()
    }

    override fun onPlayerStateChanged() {
        super.onPlayerStateChanged()
        binding.appBarLayout.toolbar.subtitle = ""
        libraryViewModel.fetchUserQueue()
    }

    private fun setupToolbar() {
        binding.appBarLayout.pinWhenScrolled()
        binding.appBarLayout.toolbar.apply {
            title = getString(R.string.title_now_playing_queue)
            subtitle = ""
            setTitleTextAppearance(
                context,
                com.adgutech.commons.R.style.toolbarTextAppearanceNormal
            )
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
            ToolbarContentTintHelper.colorBackButton(this)
        }
    }

    private fun setupRecyclerView() {
        playingQueueAdapter =
            PlayingQueueAdapter(requireActivity(), ArrayList(), R.layout.item_track_list)
        binding.recyclerView.apply {
            adapter = playingQueueAdapter
            itemAnimator = DraggableItemAnimator()
            layoutManager = LinearLayoutManager(requireActivity())
            create(this)
        }
    }
}