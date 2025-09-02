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

package com.adgutech.adomusic.remote.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.navigation.contains
import androidx.navigation.ui.setupWithNavController
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.currentFragment
import com.adgutech.adomusic.remote.extensions.findNavController
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.ui.activities.bases.AbsSpotifyLoginActivity
import com.adgutech.adomusic.remote.utils.AppRater
import com.adgutech.commons.extensions.extra
import com.adgutech.commons.extensions.hideStatusBar
import com.adgutech.commons.extensions.setTaskDescriptionColorAuto
import com.adgutech.commons.interfaces.IScrollHelper

/**
 * Created by Adolfo Gutiérrez on 02/16/2025.
 */

class MainActivity : AbsSpotifyLoginActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val EXPAND_PANEL = "expand_panel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTaskDescriptionColorAuto()
        hideStatusBar()
        AppRater.appLaunched(this)

        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.fragment_container)
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.mobile_navigation)
        val destinationId = navGraph.startDestinationId

        if (!navGraph.contains(preference.lastTab)) {
            preference.lastTab = destinationId
        }
        navGraph.setStartDestination(
            if (preference.isRememberLastTab) {
                preference.lastTab.let {
                    if (it == 0) {
                        destinationId
                    } else {
                        it
                    }
                }
            } else {
                destinationId
            }
        )

        navController.graph = navGraph
        navigationView.setupWithNavController(navController)
        // Scroll Fragment to top
        navigationView.setOnItemReselectedListener {
            currentFragment(R.id.fragment_container).apply {
                if (this is IScrollHelper) {
                    scrollToTop()
                }
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == navGraph.startDestinationId) {
                currentFragment(R.id.fragment_container)?.enterTransition = null
            }
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_albums,
                R.id.navigation_artists, R.id.navigation_playlists,
                R.id.navigation_search -> {
                    // Save the last tab
                    if (preference.isRememberLastTab) {
                        preference.lastTab = destination.id
                    }
                    // Show Bottom Navigation Bar
                    setBottomNavVisibility(visible = true, animate = true)
                }

                R.id.playing_queue_fragment -> {
                    setBottomNavVisibility(visible = false, hideBottomSheet = true)
                }

                else -> {
                    // Hide Bottom Navigation Bar
                    setBottomNavVisibility(visible = false, animate = true)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val expand = intent.extra<Boolean>(EXPAND_PANEL).value ?: false
        if (expand && preference.isExpandPanel) {
            fromNotification = true
            slidingPanel.bringToFront()
            expandPanel()
            intent.removeExtra(EXPAND_PANEL)
        }
    }
}