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

package com.adgutech.adomusic.remote.adapters.track

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.USER_COLLECTION
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.elevatedAccentColor
import com.google.android.material.button.MaterialButton

class ShuffleButtonTrackAdapter(
    activity: FragmentActivity,
    dataSet: MutableList<TrackParcelable>,
    @LayoutRes contentLayoutId: Int
) : AbsOffsetTrackAdapter(activity, dataSet, contentLayoutId) {

    override fun createViewHolder(view: View): TrackAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) OFFSET_ITEM else TRACK
    }

    override fun onBindViewHolder(holder: TrackAdapter.ViewHolder, position: Int) {
        val trackCollection = AbsMainActivityFragment.userUri + USER_COLLECTION
        if (holder.itemViewType == OFFSET_ITEM) {
            val viewHolder = holder as ViewHolder
            viewHolder.playAction?.let {
                it.setOnClickListener {
                    AppRemoteHelper.playUri(trackCollection, true)
                }
                it.accentColor(activity)
            }
            viewHolder.shuffleAction?.let {
                it.setOnClickListener {
                    AppRemoteHelper.playUri(trackCollection, false)
                }
                it.elevatedAccentColor(activity)
            }
        } else {
            super.onBindViewHolder(holder, position - 1)
        }
    }

    inner class ViewHolder(itemView: View) : AbsOffsetTrackAdapter.ViewHolder(itemView) {
        val playAction: MaterialButton? = itemView.findViewById(R.id.playAction)
        val shuffleAction: MaterialButton? = itemView.findViewById(R.id.shuffleAction)

        override fun onClick(v: View?) {
            if (itemViewType == OFFSET_ITEM) {
                AppRemoteHelper.playUri(track.uri, false)
                return
            }
            super.onClick(v)
        }
    }
}