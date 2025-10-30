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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.albumCoverOptions
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.utils.Utils
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider

/**
 * Created by Adolfo Gutierrez on 03/13/25.
 */

class AlbumAdapter(
    val activity: FragmentActivity,
    var dataSet: List<AlbumParcelable>,
    val listener: OnAlbumClickListener
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), PopupTextProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_grid, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = dataSet[position]
        holder.title?.text = album.name
        holder.text?.text = getArtistOrVariousArtists(album.artist!!)
        holder.image?.transitionName = album.id
        loadAlbumCover(album, holder)
    }

    override fun getPopupText(view: View, position: Int): CharSequence {
        val sectionName: String? = when (activity.preference.albumSortOrder) {
            SortOrder.AlbumSortOrder.ALBUM_A_Z, SortOrder.AlbumSortOrder.ALBUM_Z_A -> {
                dataSet[position].name
            }

            SortOrder.AlbumSortOrder.ALBUM_ARTIST, SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC -> {
                dataSet[position].artist
            }

            else -> return ""
        }
        return Utils.getSectionName(sectionName)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<AlbumParcelable>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    private fun loadAlbumCover(album: AlbumParcelable, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        Glide.with(activity)
            .load(album.imageUrl)
            .albumCoverOptions(album)
            .transition(GlideExtension.getDefaultTransition())
            .into(holder.image!!)
    }

    private fun getArtistOrVariousArtists(artist: String): String {
        return if (artist == "Various Artists") {
            activity.getString(R.string.various_artists)
        } else {
            artist
        }
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        private val album: AlbumParcelable
            get() = dataSet[layoutPosition]

        init {
            menu?.isGone()
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            image?.let { listener.setOnAlbumClickListener(album.id, it) }
        }
    }

    interface OnAlbumClickListener {
        fun setOnAlbumClickListener(albumId: String?, view: View)
    }
}