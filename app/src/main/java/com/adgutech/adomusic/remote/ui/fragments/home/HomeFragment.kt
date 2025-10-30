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

package com.adgutech.adomusic.remote.ui.fragments.home

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spanned
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.common.ATHToolbarActivity
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.HomeAdapter
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.FragmentHomeBinding
import com.adgutech.adomusic.remote.extensions.goToProVersion
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.GlideExtension.userImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.api.spotify.models.UserPrivate
import com.adgutech.adomusic.remote.extensions.checkForInternet
import com.adgutech.adomusic.remote.extensions.hasSpotifyInstalled
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.USER_LOGGED
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.applyColor
import com.adgutech.commons.extensions.dip
import com.adgutech.commons.extensions.getColorByAttr
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.extensions.isVisible
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.interfaces.IScrollHelper
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class HomeFragment : AbsMainActivityFragment(R.layout.fragment_home), IScrollHelper,
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val TAG: String = HomeFragment::class.java.simpleName
    }

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeAdapter: HomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeBinding = FragmentHomeBinding.bind(view)
        _binding = HomeBinding(homeBinding)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null

        enterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)
        reenterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)

        checkForMargins()
        checkUserLogged()

        homeAdapter = HomeAdapter(requireActivity())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeAdapter
        }
        libraryViewModel.getHome().observe(viewLifecycleOwner) { home ->
            homeAdapter.swapData(home)
        }
        loadProfile()
        setupTitle()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

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

        if (App.isProVersion()) {
            binding.proFeaturesContent.isGone()
        } else {
            binding.proFeaturesContent.isVisible()
        }

        binding.constraintLayoutColor.applyColor(
                requireContext(),
                code.name.monkey.appthemehelper.R.color.md_yellow_A400
            )

        binding.itemLimitAlertContent.apply {
            if (preference.isShowItemLimitAlert) {
                isVisible()
                binding.iconClose.setOnClickListener {
                    isGone()
                    preference.isShowItemLimitAlert = false
                }
            } else {
                isGone()
            }
        }
        binding.btnBuyNow.setOnClickListener {
            requireContext().goToProVersion()
        }
    }

    override fun onResume() {
        super.onResume()
        preference.registerOnSharedPreferenceChangedListener(this)
        checkForMargins()
        checkUserLogged()
        libraryViewModel.forceReload(ReloadType.HOME_SECTIONS)
        exitTransition = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preference.unregisterOnSharedPreferenceChangedListener(this)
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.removeItem(R.id.action_sort_order)
        menu.findItem(R.id.action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            binding.toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar)
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

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), binding.toolbar)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            USER_LOGGED -> {
                checkUserLogged()
            }
        }
    }

    override fun scrollToTop() {
        binding.container.scrollTo(0, 0)
        binding.appBarLayout.setExpanded(true)
    }

    override fun onPlayerStateChanged() {
        libraryViewModel.forceReload(ReloadType.HOME_SECTIONS)
        checkUserLogged()
    }

    private fun checkForMargins() {
        if (mainActivity.isBottomNavVisible) {
            binding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(com.adgutech.commons.R.dimen.bottom_nav_height)
            }
        }
    }

    private fun checkUserLogged() {
        binding.contentSpotifySignin.isVisible =
            !preference.isUserLogged || !requireContext().checkForInternet()
        binding.contentContainer.isVisible =
            preference.isUserLogged || requireContext().checkForInternet()
        if (!preference.isUserLogged) {
            if (::homeAdapter.isInitialized) {
                homeAdapter.swapData(listOf())
            }
        }
    }

    private fun setupTitle() {
        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
        val hexColorControlNormal = String.format("#%06X", requireContext().getColorByAttr(android.R.attr.textColorPrimary))
        val appName = "<font color=$hexColor>Ado<b>Music</b></font><font color=gray> Remote</font>".parseAsHtml()
        val appNamePro = "<font color=$hexColor>Ado<b>Music</b></font><font color=gray> Remote</font><font color=$hexColorControlNormal> PRO</font>".parseAsHtml()
        binding.appBarLayout.title = if (App.isProVersion()) appNamePro else appName
    }

    private fun loadProfile() {
        libraryViewModel.getMe().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {}
                is Success -> userInfo(result.data)
                is Error -> {
                    loadProfile()
                    logE("Error loading user profile : ${result.error}")
                }
            }
        }
    }

    private fun userInfo(userPrivate: UserPrivate) {
        binding.text.text = getTextAccentColor()
        if (userPrivate.display_name.isNotEmpty()) {
            binding.profileView.isVisible()
            binding.displayName.text = userPrivate.display_name
            Utils.getImageUrl(userPrivate.images)
            Glide.with(requireContext())
                .load(Utils.getImageUrl(userPrivate.images))
                .userImageOptions(userPrivate)
                .into(binding.userImage)
        }
    }

    fun setSharedAxisXTransitions() {
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            .addTarget(CoordinatorLayout::class.java)
    }

    private fun getTextAccentColor(): Spanned {
        val text = resources.getString(R.string.text_hello)
        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
        val textHello = "<font color=$hexColor>$text</font>".parseAsHtml()
        return textHello
    }
}