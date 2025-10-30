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

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.extensions.checkForInternet
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class AbsMainActivityFragment(
    @LayoutRes contentLayoutId: Int
) : AbsSpotifyServiceFragment(contentLayoutId), MenuProvider {

    val libraryViewModel: LibraryViewModel by activityViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
        if (requireContext().checkForInternet()) {
            getMe()
        }
    }

    private fun getMe() {
        libraryViewModel.getMe().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {}
                is Success -> {
                    val me = result.data
                    userId = me.id
                    userUri = me.uri
                }
                is Error -> {
                    logE("Error to load user id: ${result.error}")
                    getMe()
                }
            }
        }
    }

    companion object {
        val TAG: String = AbsMainActivityFragment::class.java.simpleName

        var userId: String? = null
        var userUri: String? = null

    }
}