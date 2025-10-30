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

package com.adgutech.adomusic.remote.ui.fragments.artists

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.adgutech.adomusic.remote.EXTRA_ARTIST_NAME
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.artist.AlbumTypeAdapter
import com.adgutech.adomusic.remote.databinding.FragmentMoreAlbumsBinding
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.addAlpha
import com.adgutech.commons.extensions.isLandscape
import com.adgutech.commons.utils.FastScrollerThemeHelper.create
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Job

abstract class AbsMoreAlbumsFragment : AbsMainActivityFragment(R.layout.fragment_more_albums),
    ChipGroup.OnCheckedStateChangeListener {

    private var _binding: FragmentMoreAlbumsBinding? = null
    private val binding get() = _binding!!

    abstract val artistDetailsViewModel: ArtistDetailsViewModel
    abstract val artistId: String?

    private lateinit var albumTypeAdapter: AlbumTypeAdapter

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoreAlbumsBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        val artistName = requireArguments().getString(EXTRA_ARTIST_NAME)!!
        binding.toolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            title = artistName
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        artistDetailsViewModel.getMoreAlbums().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> logD("more albums loading...")
                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        albumTypeAdapter.swapDataSet(result.data)
                    } else {
                        albumTypeAdapter.swapDataSet(listOf())
                    }
                }
                is Result.Error -> logE("more albums error: ${result.error}")
            }
        }

        setupRecyclerView()
        setupChips()

        albumFilter()

        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean { return false }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        albumFilter()
    }

    private fun setupRecyclerView() {
        albumTypeAdapter = AlbumTypeAdapter(requireActivity(), emptyList())
        val spamCount = if (isLandscape) 2 else 1
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), spamCount)
            adapter = albumTypeAdapter
            create(this)
        }
    }

    private fun setupChips() {
        val chips = binding.albumFilterGroup.children.map { it as Chip }
        if (!preference.isMaterialYou) {
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            )

            val colors = intArrayOf(
                android.R.color.transparent,
                accentColor().addAlpha(0.5F)
            )

            chips.forEach {
                it.chipBackgroundColor = ColorStateList(states, colors)
            }
        }
        binding.albumFilterGroup.setOnCheckedStateChangeListener(this)
    }

    private fun albumFilter() {
        val filter = getFilter()
        job?.cancel()
        job = artistDetailsViewModel.albumFilter(artistId!!, filter)
    }

    private fun getFilter(): AlbumTypeFilter {
        return when (binding.albumFilterGroup.checkedChipId) {
            R.id.chip_album -> AlbumTypeFilter.ALBUM
            R.id.chip_single -> AlbumTypeFilter.SINGLE
            R.id.chip_compilation -> AlbumTypeFilter.COMPILATION
            R.id.chip_appears_on -> AlbumTypeFilter.APPEARS_ON
            else -> AlbumTypeFilter.NO_FILTER
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}