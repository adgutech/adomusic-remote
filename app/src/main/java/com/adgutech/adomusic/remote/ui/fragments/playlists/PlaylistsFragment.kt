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

package com.adgutech.adomusic.remote.ui.fragments.playlists

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgutech.adomusic.remote.EXTRA_PLAYLIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.playlist.PlaylistAdapter
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.USER_LOGGED
import com.adgutech.adomusic.remote.ui.dialogs.CreatePlaylistDialog
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsSortOrderFragment
import com.adgutech.commons.extensions.showCircularProgress
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Created by Adolfo Gutierrez on 03/18/25.
 */

class PlaylistsFragment : AbsSortOrderFragment<PlaylistAdapter, LinearLayoutManager>(),
    PlaylistAdapter.OnPlaylistClickListener {

    companion object {
        val TAG: String = PlaylistsFragment::class.java.simpleName
    }

    override val titleRes: Int
        get() = R.string.title_playlists

    override val emptyMessage: Int
        get() = R.string.empty_playlists

    override val isCreatePlaylistVisible: Boolean
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if (App.isSpotifyPremium()) {
            libraryViewModel.getPlaylists().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Loading -> {}
                    is Success -> {
                        val playlists = result.data
                        if (playlists.isNotEmpty()) {
                            adapter!!.swapDataSet(playlists)
                        } else {
                            adapter!!.swapDataSet(listOf())
                        }
                        progress.showCircularProgress(requireContext(), false)
                    }

                    is Error -> {
                        progress.showCircularProgress(requireContext(), false)
                        logE("Error to load playlists: ${result.error}")
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
        libraryViewModel.forceReload(ReloadType.PLAYLISTS)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        when (key) {
            USER_LOGGED -> {
                if (!preference.isUserLogged) {
                    libraryViewModel.forceReload(ReloadType.PLAYLISTS)
                    adapter?.swapDataSet(listOf())
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        libraryViewModel.forceReload(ReloadType.PLAYLISTS)
    }

    override fun createAdapter(): PlaylistAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return PlaylistAdapter(requireActivity(), dataSet, this)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)
    }

    override fun onCreatePlaylistClicked() {
        if (userId != null) {
            CreatePlaylistDialog.create(userId!!)
                .show(childFragmentManager, "CreatePlaylistDialog")
        }
    }

    override fun setOnPlaylistClickListener(playlistId: String?, view: View) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(
            R.id.playlistDetailsFragment,
            bundleOf(EXTRA_PLAYLIST_ID to playlistId),
            null,
            FragmentNavigatorExtras(view to playlistId!!)
        )
    }

    override fun setSortOrder(sortOrder: String) {
        libraryViewModel.forceReload(ReloadType.PLAYLISTS)
    }

    override fun loadSortOrder(): String {
        return preference.playlistSortOrder
    }

    override fun saveSortOrder(sortOrder: String) {
        preference.playlistSortOrder = sortOrder
    }

    private fun setupSortOrderMenu(subMenu: SubMenu) {
        val currentSortOrder: String? = getSortOrder()
        subMenu.clear()
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_default,
            0,
            R.string.action_sort_order_default
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_DEFAULT
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_asc,
            1,
            R.string.action_sort_order_asc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_A_Z
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_desc,
            2,
            R.string.action_sort_order_desc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_Z_A
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_display_name_asc,
            3,
            R.string.action_playlist_sort_order_display_name_asc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_display_name_desc,
            4,
            R.string.action_playlist_sort_order_display_name_desc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME_DESC
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_track_count_asc,
            5,
            R.string.action_playlist_sort_order_track_count_asc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT
        subMenu.add(
            0,
            R.id.action_playlist_sort_order_track_count_desc,
            6,
            R.string.action_playlist_sort_order_track_count_desc
        ).isChecked = currentSortOrder == SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT_DESC

        subMenu.setGroupCheckable(0, true, true)
    }

    private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
        val sortOrder: String = when (item.itemId) {
            R.id.action_playlist_sort_order_default -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_DEFAULT
            }

            R.id.action_playlist_sort_order_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_A_Z
            }

            R.id.action_playlist_sort_order_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_Z_A
            }

            R.id.action_playlist_sort_order_display_name_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME
            }

            R.id.action_playlist_sort_order_display_name_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME_DESC
            }

            R.id.action_playlist_sort_order_track_count_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT
            }

            R.id.action_playlist_sort_order_track_count_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.PlaylistSortOrder.PLAYLIST_TRACK_COUNT_DESC
            }

            else -> preference.playlistSortOrder
        }
        if (sortOrder != preference.playlistSortOrder) {
            item.isChecked = true
            setAndSaveSortOrder(sortOrder)
            return true
        }
        return false
    }
}