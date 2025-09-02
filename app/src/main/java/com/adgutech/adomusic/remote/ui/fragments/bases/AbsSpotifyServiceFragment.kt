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

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.adgutech.adomusic.remote.interfaces.OnSpotifyServiceEventListener
import com.adgutech.adomusic.remote.ui.activities.bases.AbsSpotifyServiceActivity

open class AbsSpotifyServiceFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId),
    OnSpotifyServiceEventListener {

    companion object {
        val TAG: String = AbsSpotifyServiceFragment::class.java.simpleName
    }

    var serviceActivity: AbsSpotifyServiceActivity? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            serviceActivity = context as AbsSpotifyServiceActivity?
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " +
                    AbsSpotifyServiceActivity::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        serviceActivity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceActivity?.addSpotifyServiceEventListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        serviceActivity?.removeSpotifyServiceEventListener(this)
    }

    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onPlayerStateChanged() {}
}