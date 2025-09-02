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
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.artist.TopArtistsAdapter
import com.adgutech.adomusic.remote.adapters.track.ShuffleButtonTrackAdapter
import com.adgutech.adomusic.remote.adapters.track.TrackAdapter
import com.adgutech.adomusic.remote.databinding.FragmentListDetailBinding
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.adomusic.remote.ui.fragments.home.LIKED_SONGS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_ARTISTS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_TRACKS
import com.adgutech.commons.extensions.gridCount
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialSharedAxis

class DetailListFragment : AbsMainActivityFragment(R.layout.fragment_list_detail),
    TopArtistsAdapter.OnTopArtistsClickListener {

    private var _binding: FragmentListDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailListFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (args.type) {
            TOP_ARTISTS,
            TOP_TRACKS,
            LIKED_SONGS
            -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }

            else -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListDetailBinding.bind(view)
        mainActivity.setSupportActionBar(binding.appBarLayout.toolbar)
        binding.appBarLayout.toolbar.apply {
            title = null
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        when (args.type) {
            LIKED_SONGS -> loadLikedSongs()
            TOP_ARTISTS -> loadTopArtists()
            TOP_TRACKS -> loadTopTracks()
        }

        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun setOnTopArtistsClickListener(artistId: String?, view: View) {
        findNavController().navigate(
            R.id.artistDetailsFragment,
            bundleOf(EXTRA_ARTIST_ID to artistId),
            null,
            FragmentNavigatorExtras(view to artistId!!)
        )
    }

    private fun loadLikedSongs() {
        binding.appBarLayout.toolbar.title = getString(R.string.title_liked_songs)
        val shuffleAdapter = getShuffleButtonTrackAdapter(mutableListOf())
        binding.recyclerView.apply {
            adapter = shuffleAdapter
            layoutManager = getLinearLayoutManager()
        }
        libraryViewModel.getMySavedTracks().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    shuffleAdapter.swapDataSet(result.data)
                }

                is Result.Error -> {
                    logE("Error loading liked songs : ${result.error}")
                }

                is Result.Loading -> {
                    logD("loading liked songs...")
                }
            }
        }
    }

    private fun loadTopArtists() {
        binding.appBarLayout.toolbar.title = getString(R.string.title_top_artists)
        val topArtistAdapter = getTopArtistsAdapter(listOf())
        binding.recyclerView.apply {
            adapter = topArtistAdapter
            layoutManager = gridLayoutManager()
        }
        libraryViewModel.getTopArtists().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    topArtistAdapter.swapDataSet(result.data)
                }

                is Result.Error -> {
                    logE("Error loading top artists : ${result.error}")
                }

                is Result.Loading -> {
                    logD("loading top artists...")
                }
            }

        }
    }

    private fun loadTopTracks() {
        binding.appBarLayout.toolbar.title = getString(R.string.title_top_tracks)
        val trackAdapter = getTrackAdapter(mutableListOf())
        binding.recyclerView.apply {
            adapter = trackAdapter
            layoutManager = getLinearLayoutManager()
        }
        libraryViewModel.getTopTracks().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    trackAdapter.swapDataSet(result.data)
                }

                is Result.Error -> {
                    logE("Error loading top artists : ${result.error}")
                }

                is Result.Loading -> {
                    logD("loading top artists...")
                }
            }

        }
    }

    private fun getShuffleButtonTrackAdapter(tracks: MutableList<TrackParcelable>): ShuffleButtonTrackAdapter {
        return ShuffleButtonTrackAdapter(requireActivity(), tracks, R.layout.item_track_list)
    }

    private fun getTopArtistsAdapter(topArtist: List<ArtistParcelable>): TopArtistsAdapter {
        return TopArtistsAdapter(requireActivity(), topArtist, this)
    }

    private fun getTrackAdapter(topTracks: MutableList<TrackParcelable>): TrackAdapter {
        return TrackAdapter(requireActivity(), topTracks, R.layout.item_track_list)
    }

    private fun getLinearLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
    }

    private fun gridLayoutManager(): GridLayoutManager {
        return GridLayoutManager(requireActivity(), gridCount(), GridLayoutManager.VERTICAL, false)
    }
}