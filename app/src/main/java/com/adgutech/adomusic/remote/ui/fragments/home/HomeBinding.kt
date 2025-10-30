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

import com.adgutech.adomusic.remote.databinding.FragmentHomeBinding

class HomeBinding(homeBinding: FragmentHomeBinding) {
    val root = homeBinding.root
    val container = homeBinding.container
    val contentContainer = homeBinding.contentContainer
    val contentSpotifySignin = homeBinding.contentSpotifySignin
    val appBarLayout = homeBinding.appBarLayout
    val toolbar = homeBinding.appBarLayout.toolbar
    val profileView = homeBinding.profileView
    val userImage = homeBinding.profileView.image
    val displayName = homeBinding.profileView.displayName
    val text = homeBinding.profileView.text
    val recyclerView = homeBinding.contentHome.recyclerView
    val button = homeBinding.button
    val proFeaturesContent = homeBinding.contentHome.proFeaturesContent
    val btnBuyNow = homeBinding.contentHome.btnBuyNow
    val itemLimitAlertContent = homeBinding.contentHome.itemLimitAlertContent
    val constraintLayoutColor = homeBinding.contentHome.constraintLayoutColor
    val iconClose = homeBinding.contentHome.iconClose
}