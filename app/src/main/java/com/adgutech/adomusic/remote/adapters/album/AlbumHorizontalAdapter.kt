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
import com.adgutech.adomusic.remote.ALBUM
import com.adgutech.adomusic.remote.COMPILATION
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.SINGLE
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.trackImageOptions
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.commons.extensions.isGone
import com.bumptech.glide.Glide

class AlbumHorizontalAdapter(
    val activity: FragmentActivity,
    var dataSet: List<ArtistAlbumParcelable>,
    val listener: OnAlbumClickListener
) : RecyclerView.Adapter<AlbumHorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.item_album_grid, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = dataSet[position]
        holder.title?.text = album.name
        holder.text?.text = getAlbumType(album.albumType!!)
        holder.image?.transitionName = album.id
        loadTrackImage(album, holder)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<ArtistAlbumParcelable>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    private fun loadTrackImage(track: ArtistAlbumParcelable, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        Glide.with(activity)
            .load(track.imageUrl)
            .trackImageOptions()
            .transition(GlideExtension.getDefaultTransition())
            .into(holder.image!!)
    }

    private fun getAlbumType(albumType: String): String {
        return when (albumType) {
            ALBUM -> activity.getString(R.string.type_album)
            COMPILATION -> activity.getString(R.string.album_type_compilation)
            SINGLE -> activity.getString(R.string.type_single)
            else -> activity.getString(R.string.type_album)
        }
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        private val album: ArtistAlbumParcelable
            get() = dataSet[layoutPosition]

        init {
            menu?.isGone()
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            image?.let {
                listener.setOnAlbumClickListener(album.id, it)
            }
        }
    }

    interface OnAlbumClickListener {
        fun setOnAlbumClickListener(albumId: String?, view: View)
    }
}