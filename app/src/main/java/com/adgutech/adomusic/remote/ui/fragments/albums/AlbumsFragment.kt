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

package com.adgutech.adomusic.remote.ui.fragments.albums

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
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.album.AlbumAdapter
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.USER_LOGGED
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsSortOrderFragment
import com.adgutech.commons.extensions.gridCount
import com.adgutech.commons.extensions.showCircularProgress
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Created by Adolfo Gutierrez on 03/18/25.
 */

class AlbumsFragment : AbsSortOrderFragment<AlbumAdapter, GridLayoutManager>(),
    AlbumAdapter.OnAlbumClickListener {

    companion object {
        val TAG: String = AlbumsFragment::class.java.simpleName
    }

    override val titleRes: Int
        get() = R.string.title_albums

    override val emptyMessage: Int
        get() = R.string.empty_albums

    override val isCreatePlaylistVisible: Boolean
        get() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryViewModel.getAlbums().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {}
                is Success -> {
                    val albums = result.data
                    if (albums.isNotEmpty()) {
                        adapter!!.swapDataSet(albums)
                    } else {
                        adapter!!.swapDataSet(listOf())
                    }
                    progress.showCircularProgress(requireContext(), false)
                }

                is Error -> {
                    progress.showCircularProgress(requireContext(), false)
                    logE("Error to load albums: ${result.error}")
                }
            }
        }
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
        libraryViewModel.forceReload(ReloadType.ALBUMS)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        when (key) {
            USER_LOGGED -> {
                if (!preference.isUserLogged) {
                    libraryViewModel.forceReload(ReloadType.ALBUMS)
                    adapter?.swapDataSet(listOf())
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        libraryViewModel.forceReload(ReloadType.ALBUMS)
    }

    override fun createAdapter(): AlbumAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return AlbumAdapter(requireActivity(), dataSet, this)
    }

    override fun createLayoutManager(): GridLayoutManager {
        return GridLayoutManager(
            requireContext(),
            gridCount(),
            GridLayoutManager.VERTICAL,
            false
        )
    }

    override fun setOnAlbumClickListener(albumId: String?, view: View) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(
            R.id.albumDetailsFragment,
            bundleOf(
                EXTRA_ALBUM_ID to albumId
            ),
            null,
            FragmentNavigatorExtras(view to albumId!!)
        )
    }

    override fun setSortOrder(sortOrder: String) {
        libraryViewModel.forceReload(ReloadType.ALBUMS)
    }

    override fun loadSortOrder(): String {
        return preference.albumSortOrder
    }

    override fun saveSortOrder(sortOrder: String) {
        preference.albumSortOrder = sortOrder
    }

    private fun setupSortOrderMenu(subMenu: SubMenu) {
        val currentSortOrder: String? = getSortOrder()
        subMenu.clear()
        subMenu.add(
            0,
            R.id.action_album_sort_order_default,
            0,
            R.string.action_sort_order_default
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_DEFAULT
        subMenu.add(
            0,
            R.id.action_album_sort_order_asc,
            1,
            R.string.action_sort_order_asc
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_A_Z
        subMenu.add(
            0,
            R.id.action_album_sort_order_desc,
            2,
            R.string.action_sort_order_desc
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_Z_A
        subMenu.add(
            0,
            R.id.action_album_sort_order_artist,
            3,
            R.string.action_album_sort_order_artist
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_ARTIST
        subMenu.add(
            0,
            R.id.action_album_sort_order_artist_desc,
            4,
            R.string.action_album_sort_order_artist_desc
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC
        subMenu.add(
            0,
            R.id.action_album_sort_order_release_date,
            5,
            R.string.action_album_sort_order_release_date
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE
        subMenu.add(
            0,
            R.id.action_album_sort_order_release_date_desc,
            6,
            R.string.action_album_sort_order_release_date_desc
        ).isChecked = currentSortOrder == SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE_DESC

        subMenu.setGroupCheckable(0, true, true)
    }

    private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
        val sortOrder: String = when (item.itemId) {
            R.id.action_album_sort_order_default -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_DEFAULT
            }

            R.id.action_album_sort_order_asc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_A_Z
            }

            R.id.action_album_sort_order_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_Z_A
            }

            R.id.action_album_sort_order_artist -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_ARTIST
            }

            R.id.action_album_sort_order_artist_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC
            }

            R.id.action_album_sort_order_release_date -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE
            }

            R.id.action_album_sort_order_release_date_desc -> {
                progress.showCircularProgress(requireContext(), true)
                SortOrder.AlbumSortOrder.ALBUM_RELEASE_DATE_DESC
            }

            else -> preference.albumSortOrder
        }
        if (sortOrder != preference.albumSortOrder) {
            item.isChecked = true
            setAndSaveSortOrder(sortOrder)
            return true
        }
        return false
    }
}