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
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.trackImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.menu.TrackMenuHelper
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.models.TrackQueue
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.bumptech.glide.Glide

class PlayingQueueAdapter(
    val activity: FragmentActivity,
    var dataSet: MutableList<TrackQueue>,
    @LayoutRes val contentLayoutId: Int
) : RecyclerView.Adapter<PlayingQueueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = try {
            LayoutInflater.from(activity).inflate(contentLayoutId, parent, false)
        } catch (e: Resources.NotFoundException) {
            LayoutInflater.from(activity).inflate(R.layout.item_track_list, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = dataSet[position]
        if (track.uri == track.currentlyPlaying!!.uri) {
            holder.title?.text = getTextAccentColor(track.name)
        } else {
            holder.title?.text = track.name
        }
        holder.text?.text = Utils.getArtists(track.artists)
        holder.icon?.isVisible = track.isExplicit
        loadTrackImage(track, holder)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<TrackQueue>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    private fun getTextAccentColor(title: String): Spanned {
        val hexColor = String.format("#%06X", 0xFFFFFF and activity.accentColor())
        val text = "<font color=$hexColor>$title</font>".parseAsHtml()
        return text
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

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        private var trackMenuRes = TrackMenuHelper.MENU_RES
        private val track: TrackQueue
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
