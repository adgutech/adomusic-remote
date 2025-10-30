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

package com.adgutech.adomusic.remote.ui.fragments.bases

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import code.name.monkey.appthemehelper.common.ATHToolbarActivity
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.AbsMultiSelectAdapter
import com.adgutech.adomusic.remote.databinding.FragmentMainRecyclerBinding
import com.adgutech.adomusic.remote.extensions.checkForInternet
import com.adgutech.adomusic.remote.extensions.hasSpotifyInstalled
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.USER_LOGGED
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.dip
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.interfaces.IScrollHelper
import com.adgutech.commons.utils.FastScrollerThemeHelper.create
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

abstract class AbsRecyclerViewFragment<A : RecyclerView.Adapter<*>, LM : RecyclerView.LayoutManager> :
    AbsMainActivityFragment(R.layout.fragment_main_recycler), IScrollHelper,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var _binding: FragmentMainRecyclerBinding? = null
    private val binding get() = _binding!!

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    protected var adapter: A? = null
    protected var layoutManager: LM? = null

    val fab: FloatingActionButton
        get() = binding.fab

    val toolbar: Toolbar
        get() = binding.appBarLayout.toolbar

    val progress: CircularProgressIndicator
        get() = binding.progress

    abstract val titleRes: Int
    abstract val emptyMessage: Int
    abstract val isCreatePlaylistVisible: Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainRecyclerBinding.bind(view)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        enterTransition = MaterialFadeThrough().addTarget(recyclerView)
        reenterTransition = MaterialFadeThrough().addTarget(recyclerView)
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.title = null
        initLayoutManager()
        initAdapter()
        checkForMargins()
        checkUserLogged()
        setupRecyclerView()
        setupToolbar()

        binding.button.setOnClickListener {
            if (requireContext().checkForInternet()) {
                if (hasSpotifyInstalled()) {
                    AppRemoteHelper.musicService!!.onAuthorizationSpotify(requireActivity())
                    serviceActivity?.connect(true)
                } else {
                    mainActivity.showSpotifyNotInstalledDialog()
                }
            } else {
                showToast(R.string.text_not_internet)
            }
        }

        fab.fitsSystemWindows = preference.isFullScreenMode
        // Add listeners when shuffle is visible
        if (isCreatePlaylistVisible) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            })
            fab.apply {
                setOnClickListener {
                    if (AppRemoteHelper.musicService?.spotifyAppRemote != null) {
                        onCreatePlaylistClicked()
                    }
                }
                accentColor(requireContext())
            }
        } else {
            fab.isGone()
        }
        libraryViewModel.getFabMargin().observe(viewLifecycleOwner) {
            fab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preference.registerOnSharedPreferenceChangedListener(this)
        checkForMargins()
        checkUserLogged()
    }

    override fun onPlayerStateChanged() {
        checkUserLogged()
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
        binding.appBarLayout.setExpanded(true, true)
    }

    override fun onPrepareMenu(menu: Menu) {
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), toolbar)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(toolbar)
        )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_settings -> {
                exitTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
                reenterTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, false)
                findNavController().navigate(R.id.settings_fragment, null)
            }
        }
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            USER_LOGGED -> {
                checkUserLogged()
            }
        }
    }

    private fun checkUserLogged() {
        binding.contentSpotifySignin.isVisible =
            !preference.isUserLogged || !requireContext().checkForInternet()
        binding.recyclerView.isVisible =
            preference.isUserLogged || requireContext().checkForInternet()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = this@AbsRecyclerViewFragment.layoutManager
            adapter = this@AbsRecyclerViewFragment.adapter
            create(this)
        }
    }

    private fun setupToolbar() {
        val appName = resources.getString(titleRes)
        binding.appBarLayout.title = appName
    }

    private fun checkIsEmpty() {
        binding.emptyText.setText(emptyMessage)
        binding.empty.isVisible = adapter!!.itemCount == 0
    }

    private fun checkForMargins() {
        if (mainActivity.isBottomNavVisible) {
            binding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(com.adgutech.commons.R.dimen.bottom_nav_height)
            }
        }
    }

    private fun initAdapter() {
        adapter = createAdapter()
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }

    private fun initLayoutManager() {
        layoutManager = createLayoutManager()
    }

    protected fun invalidateAdapter() {
        checkIsEmpty()
        initAdapter()
        recyclerView.adapter = adapter
    }

    protected fun invalidateLayoutManager() {
        initLayoutManager()
        recyclerView.layoutManager = layoutManager
    }

    protected abstract fun createAdapter(): A

    protected abstract fun createLayoutManager(): LM

    open fun onCreatePlaylistClicked() {}

    override fun onDestroyView() {
        super.onDestroyView()
        preference.unregisterOnSharedPreferenceChangedListener(this)
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (adapter as? AbsMultiSelectAdapter<*, *>)?.actionMode?.finish()
    }
}