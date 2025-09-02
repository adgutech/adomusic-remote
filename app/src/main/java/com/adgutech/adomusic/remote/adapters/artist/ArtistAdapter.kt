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
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.artistImageOptions
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider

/**
 * Created by Adolfo Gutierrez on 03/13/25.
 */

class ArtistAdapter(
    val activity: FragmentActivity,
    var dataSet: List<ArtistParcelable>,
    val listener: OnArtistClickListener
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), PopupTextProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.item_artist_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = dataSet[position]
        holder.title?.text = artist.name
        holder.text?.text = Utils.getFollowersFormat(activity, artist.followers)
        holder.image?.transitionName = artist.id
        loadArtistImage(artist, holder)
    }

    override fun getPopupText(view: View, position: Int): CharSequence {
        val sectionName: String? = when (activity.preference.artistSortOrder) {
            SortOrder.ArtistSortOrder.ARTIST_A_Z, SortOrder.ArtistSortOrder.ARTIST_Z_A -> {
                dataSet[position].name
            }

            else -> return ""
        }
        return Utils.getSectionName(sectionName)
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

        override fun onClick(v: View?) {
            super.onClick(v)
            val artist = dataSet[layoutPosition]
            image?.let {
                listener.setOnArtistClickListener(artist.id, it)
            }
        }
    }

    interface OnArtistClickListener {
        fun setOnArtistClickListener(artistId: String?, view: View)
    }
}