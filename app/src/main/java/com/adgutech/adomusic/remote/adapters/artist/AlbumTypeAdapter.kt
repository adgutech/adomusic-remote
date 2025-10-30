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
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import code.name.monkey.appthemehelper.ThemeStore
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension.albumCoverOptions
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.bumptech.glide.Glide

class AlbumTypeAdapter(
    val activity: FragmentActivity,
    var dataSet: List<Any>
) : RecyclerView.Adapter<AlbumTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            HEADER -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        com.adgutech.commons.R.layout.sub_header,
                        parent,
                        false
                    ),
                    viewType
                )
            }
            ALBUM -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_album_type,
                        parent,
                        false
                    ),
                    viewType
                )
            }
            else -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_album_type,
                        parent,
                        false
                    ),
                    viewType
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position] is ArtistAlbumParcelable) ALBUM else HEADER
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ALBUM -> {
                val album = dataSet[position] as ArtistAlbumParcelable
                holder.title?.text = album.name
                holder.text?.text = getArtistOrVariousArtists(album.artist!!)
                holder.text2?.text = Utils.formatYear(album.releaseDate!!)
                Glide.with(activity).asDrawable()
                    .load(album.imageUrl)
                    .albumCoverOptions(album)
                    .into(holder.image!!)
            }
            else -> {
                holder.title?.text = dataSet[position].toString()
                holder.title?.setTextColor(ThemeStore.accentColor(activity))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<Any>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    private fun getArtistOrVariousArtists(artist: String): String {
        return if (artist == "Various Artists") {
            activity.getString(R.string.various_artists)
        } else {
            artist
        }
    }

    inner class ViewHolder(itemView: View, itemViewType: Int) : MediaEntryViewHolder(itemView) {

        private val item: Any
            get() = dataSet[layoutPosition]

        init {
            itemView.setOnLongClickListener(null)
            when (itemViewType) {
                ALBUM -> setImageTransitionName(activity.getString(R.string.transition_album_image))
                else -> {}
            }
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            when (itemViewType) {
                ALBUM -> {
                    activity.findNavController(R.id.fragment_container).navigate(
                        R.id.albumDetailsFragment,
                        bundleOf(EXTRA_ALBUM_ID to (item as ArtistAlbumParcelable).id)
                    )
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val HEADER = 0
        private const val ALBUM = 1
    }
}