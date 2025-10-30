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

package com.adgutech.adomusic.remote.adapters.album

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.menu.TrackMenuHelper
import com.adgutech.adomusic.remote.models.AlbumTrackParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.utils.Utils
import me.zhanghai.android.fastscroll.PopupTextProvider

/**
 * Created by Adolfo Gutiérrez on 06/04/2025.
 */

class AlbumTracksAdapter(
    val activity: FragmentActivity,
    var dataSet: List<AlbumTrackParcelable>
) : RecyclerView.Adapter<AlbumTracksAdapter.ViewHolder>(), PopupTextProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.item_track_simple, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = dataSet[position]
        holder.title?.text = track.name
        holder.text?.text = Utils.getArtists(track.artists)
        holder.imageText?.text = track.trackNumber.toString()
        holder.time?.text = Utils.getReadableDurationString(track.duration)
        holder.icon?.isVisible = track.isExplicit
    }

    override fun getPopupText(view: View, position: Int): CharSequence {
        return Utils.getSectionName(dataSet[position].name)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<AlbumTrackParcelable>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        private var trackMenuRes = TrackMenuHelper.MENU_RES
        private val track: AlbumTrackParcelable
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
            super.onClick(v)
            AppRemoteHelper.playUri(track.uri, false)
        }
    }
}