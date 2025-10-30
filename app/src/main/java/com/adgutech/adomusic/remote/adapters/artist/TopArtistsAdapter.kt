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

package com.adgutech.adomusic.remote.adapters.artist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.artistImageOptions
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.isGone
import com.bumptech.glide.Glide

class TopArtistsAdapter(
    val activity: FragmentActivity,
    var dataSet: List<ArtistParcelable>,
    val listener: OnTopArtistsClickListener
) : RecyclerView.Adapter<TopArtistsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.item_artist_grid, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = dataSet[position]
        val artistName = artist.name
        holder.title?.text = artistName
        holder.text?.text = Utils.getFollowersFormat(activity, artist.followers)
        holder.image?.transitionName = artistName
        loadArtistImage(artist, holder)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<ArtistParcelable>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    private fun loadArtistImage(artist: ArtistParcelable, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        Glide.with(activity)
            .load(artist.imageUrl)
            .artistImageOptions(artist)
            .transition(GlideExtension.getDefaultTransition())
            .into(holder.image!!)
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        init {
            menu?.isGone()
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            val artist = dataSet[layoutPosition]
            image?.let {
                listener.setOnTopArtistsClickListener(artist.id, it)
            }
        }
    }

    interface OnTopArtistsClickListener {
        fun setOnTopArtistsClickListener(artistId: String?, view: View)
    }
}