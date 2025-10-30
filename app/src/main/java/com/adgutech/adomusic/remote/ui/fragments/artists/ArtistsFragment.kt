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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.artist.ArtistAdapter
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.USER_LOGGED
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsSortOrderFragment
import com.adgutech.commons.extensions.showCircularProgress
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Created by Adolfo Gutierrez on 03/18/25.
 */

class ArtistsFragment : AbsSortOrderFragment<ArtistAdapter, LinearLayoutManager>(),
    ArtistAdapter.OnArtistClickListener {

    companion object {
        val TAG: String = ArtistsFragment::class.java.simpleName
    }

    override val titleRes: Int
        get() = R.string.title_artists

    override val emptyMessage: Int
        get() = R.string.empty_artists

    override val isCreatePlaylistVisible: Boolean
        get() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if (App.isSpotifyPremium()) {
            libraryViewModel.getArtists().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Loading -> {}
                    is Success -> {
                        val artists = result.data
                        if (artists.isNotEmpty()) {
                            adapter!!.swapDataSet(artists)
                        } else {
                            adapter!!.swapDataSet(listOf())
                        }
                        progress.showCircularProgress(requireContext(), false)
                    }

                    is Error -> {
                        progress.showCircularProgress(requireContext(), false)
                        logE("Error to load artists: ${result.error}")
                    }
                }
            }
        //}
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        setupSortOrderMenu(menu.findItem(R.id.action_sort_order).subMenu!!)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (handleSortOrderMenuItem(menuItem)) {
            return true
        }
        return super.onMenuItemSelected(menuItem)
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.forceReload(ReloadType.ARTISTS)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        when (key) {
            USER_LOGGED -> {
                if (!preference.isUserLogged) {
                    libraryViewModel.forceReload(ReloadType.ARTISTS)
                    adapter?.swapDataSet(listOf())
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        libraryViewModel.forceReload(ReloadType.ARTISTS)
    }

    override fun createAdapter(): ArtistAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return ArtistAdapter(requireActivity(), dataSet, this)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun setOnArtistClickListener(artistId: String?, view: View) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(
            R.id.artistDetailsFragment,
            bundleOf(EXTRA_ARTIST_ID to artistId),
            null,
            FragmentNavigatorExtras(view to artistId!!)
        )
    }

    override fun setSortOrder(sortOrder: String) {
        libraryViewModel.forceReload(ReloadType.ARTISTS)
    }

    override fun loadSortOrder(): String {
        return preference.artistSortOrder
    }

    override fun saveSortOrder(sortOrder: String) {
        preference.artistSortOrder = sortOrder
    }

    private fun setupSortOrderMenu(subMenu: SubMenu) {
        val currentSortOrder: String? = getSortOrder()
        subMenu.clear()
        subMenu.add(
            0,
            R.id.action_artist_sort_order_default,
            0,
            R.string.action_sort_order_default
        ).isChecked = currentSortOrder == SortOrder.ArtistSortOrder.ARTIST_DEFAULT
        subMenu.add(
            0,
            R.id.action_artist_sort_order_asc,
            1,
            R.string.action_sort_order_asc
        ).isChecked = currentSortOrder == SortOrder.ArtistSortOrder.ARTIST_A_Z
        subMenu.add(
            0,
            R.id.action_artist_sort_order_desc,
            2,
            R.string.action_sort_order_desc
        ).isChecked = currentSortOrder == SortOrder.ArtistSortOrder.ARTIST_Z_A
        subMenu.add(
            0,
            R.id.action_artist_sort_order_most_followed_asc,
            3,
            R.string.action_artist_sort_order_most_followed_asc
        ).isChecked = currentSortOrder == SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED
        subMenu.add(
            0,
            R.id.action_artist_sort_order_most_followed_desc,
            4,
            R.string.action_artist_sort_order_most_followed_desc
        ).isChecked = currentSortOrder == SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED_DESC

        subMenu.setGroupCheckable(0, true, true)
    }

    private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
        val sortOrder: String = when (item.itemId) {
            R.id.action_artist_sort_order_default -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.ArtistSortOrder.ARTIST_DEFAULT
            }

            R.id.action_artist_sort_order_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.ArtistSortOrder.ARTIST_A_Z
            }

            R.id.action_artist_sort_order_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.ArtistSortOrder.ARTIST_Z_A
            }

            R.id.action_artist_sort_order_most_followed_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED
            }

            R.id.action_artist_sort_order_most_followed_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.ArtistSortOrder.ARTIST_MOST_FOLLOWED_DESC
            }

            else -> preference.artistSortOrder
        }
        if (sortOrder != preference.artistSortOrder) {
            item.isChecked = true
            setAndSaveSortOrder(sortOrder)
            return true
        }
        return false
    }
}