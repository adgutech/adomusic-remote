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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.models.TrackParcelable

abstract class AbsOffsetTrackAdapter(
    activity: FragmentActivity,
    dataSet: MutableList<TrackParcelable>,
    @LayoutRes contentLayoutId: Int
) : TrackAdapter(activity, dataSet, contentLayoutId) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackAdapter.ViewHolder {
        if (viewType == OFFSET_ITEM) {
            val view = LayoutInflater.from(activity)
                .inflate(R.layout.item_list_quick_actions, parent, false)
            return createViewHolder(view)
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun createViewHolder(view: View): TrackAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getIdentifier(position: Int): TrackParcelable? {
        var positionFinal = position
        positionFinal--
        return if (positionFinal < 0) null else super.getIdentifier(positionFinal)
    }

    override fun getItemCount(): Int {
        val superItemCount = super.getItemCount()
        return if (superItemCount == 0) 0 else superItemCount + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) OFFSET_ITEM else TRACK
    }

    open inner class ViewHolder(itemView: View) : TrackAdapter.ViewHolder(itemView) {

        /**
         * Could also return null, just to be safe return empty track.
         */
        override val track: TrackParcelable
            get() = if (itemViewType == OFFSET_ITEM) TrackParcelable.empty else dataSet[layoutPosition - 1]

        override fun onClick(v: View?) {
            if (isInQuickSelectMode && itemViewType != OFFSET_ITEM) {
                toggleChecked(layoutPosition)
            } else {
                AppRemoteHelper.playUri(track.uri, false)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (itemViewType == OFFSET_ITEM) return false
            toggleChecked(layoutPosition)
            return true
        }
    }

    companion object {
        const val OFFSET_ITEM = 0
        const val TRACK = 1
    }
}