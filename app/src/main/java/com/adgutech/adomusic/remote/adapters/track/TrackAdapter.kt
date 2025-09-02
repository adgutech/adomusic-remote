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

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.AbsMultiSelectAdapter
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.trackImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.menu.TrackMenuHelper
import com.adgutech.adomusic.remote.helpers.menu.TracksMenuHelper
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.bumptech.glide.Glide

open class TrackAdapter(
    override val activity: FragmentActivity,
    var dataSet: MutableList<TrackParcelable>,
    @LayoutRes val contentLayoutId: Int
) : AbsMultiSelectAdapter<TrackAdapter.ViewHolder, TrackParcelable>(
    activity,
    R.menu.menu_media_selection
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = try {
            LayoutInflater.from(activity).inflate(contentLayoutId, parent, false)
        } catch (e: Resources.NotFoundException) {
            LayoutInflater.from(activity).inflate(R.layout.item_track_list, parent, false)
        }
        return createViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = dataSet[position]
        val isChecked = isChecked(track)
        holder.itemView.isActivated = isChecked
        holder.menu?.isGone = isChecked
        holder.title?.text = track.name
        holder.text?.text = Utils.getArtists(track.artists)
        holder.icon?.isVisible = track.isExplicit
        loadTrackImage(track, holder)
    }

    override fun getIdentifier(position: Int): TrackParcelable? {
        return dataSet[position]
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<TrackParcelable>) {
        TracksMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    override fun getName(model: TrackParcelable): String? {
        return model.name
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<TrackParcelable>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    private fun loadTrackImage(track: TrackParcelable, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        Glide.with(activity)
            .load(track.imageUrl)
            .trackImageOptions()
            .transition(GlideExtension.getDefaultTransition())
            .into(holder.image!!)
    }

    protected open fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        protected open var trackMenuRes = TrackMenuHelper.MENU_RES

        protected open val track: TrackParcelable
            get() = dataSet[layoutPosition]

        init {
            menu?.setOnClickListener(object : TrackMenuHelper.OnClickTrackMenu(activity) {

                override val track: TrackParcelable
                    get() = this@ViewHolder.track

                override val menuRes: Int
                    get() = trackMenuRes
            })
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(layoutPosition)
            } else {
                AppRemoteHelper.playUri(track.uri, false)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            return toggleChecked(layoutPosition)
        }
    }
}