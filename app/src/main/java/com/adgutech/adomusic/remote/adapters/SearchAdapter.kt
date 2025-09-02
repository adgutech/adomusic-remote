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

package com.adgutech.adomusic.remote.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import code.name.monkey.appthemehelper.ThemeStore
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.EXTRA_PLAYLIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.MediaEntryViewHolder
import com.adgutech.adomusic.remote.glide.GlideExtension.albumCoverOptions
import com.adgutech.adomusic.remote.glide.GlideExtension.artistImageOptions
import com.adgutech.adomusic.remote.glide.GlideExtension.playlistImageOptions
import com.adgutech.adomusic.remote.glide.GlideExtension.trackImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.helpers.menu.PlaylistMenuHelper
import com.adgutech.adomusic.remote.helpers.menu.TrackMenuHelper
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.isGone
import com.bumptech.glide.Glide

/**
 * Created by Adolfo Gutierrez on 05/31/2025.
 */

class SearchAdapter(
    private val activity: FragmentActivity,
    private var dataSet: List<Any>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

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
                        R.layout.item_list,
                        parent,
                        false
                    ),
                    viewType
                )
            }

            ARTIST -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_artist_list,
                        parent,
                        false
                    ),
                    viewType
                )
            }

            PLAYLIST -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_playlist_list,
                        parent,
                        false
                    ),
                    viewType
                )
            }

            TRACK -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_track_list,
                        parent,
                        false
                    ),
                    viewType
                )
            }

            else -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_list,
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
        if (dataSet[position] is AlbumParcelable) return ALBUM
        if (dataSet[position] is ArtistParcelable) return ARTIST
        if (dataSet[position] is PlaylistParcelable) return PLAYLIST
        return if (dataSet[position] is TrackParcelable) TRACK else HEADER
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ALBUM -> {
                holder.image?.isVisible = true
                val album = dataSet[position] as AlbumParcelable
                holder.title?.text = album.name
                holder.text?.isGone()
                Glide.with(activity).asDrawable()
                    .load(album.imageUrl)
                    .albumCoverOptions(album)
                    .into(holder.image!!)
            }

            ARTIST -> {
                holder.image?.isVisible = true
                val artist = dataSet[position] as ArtistParcelable
                holder.title?.text = artist.name
                holder.text?.text = Utils.getFollowersFormat(activity, artist.followers)
                Glide.with(activity).asDrawable()
                    .load(artist.imageUrl)
                    .artistImageOptions(artist)
                    .into(holder.image!!)
            }

            PLAYLIST -> {
                holder.image?.isVisible = true
                val playlist = dataSet[position] as PlaylistParcelable
                holder.title?.text = playlist.title
                holder.text?.text = Utils
                    .getPlaylistInfoString(activity, playlist.displayName, playlist.trackTotal)
                val icon =
                    if (playlist.isPublic) R.drawable.ic_public_16dp else R.drawable.ic_lock_16dp
                holder.icon?.setImageResource(icon)
                Glide.with(activity).asDrawable()
                    .load(Utils.getImageUrl(playlist.images ?: listOf()))
                    .playlistImageOptions(playlist)
                    .into(holder.image!!)
            }

            TRACK -> {
                holder.image?.isVisible = true
                val track = dataSet[position] as TrackParcelable
                holder.title?.text = track.name
                holder.text?.text = track.artist
                holder.icon?.isVisible = track.isExplicit
                Glide.with(activity).asDrawable()
                    .load(track.imageUrl)
                    .trackImageOptions()
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

    inner class ViewHolder(itemView: View, itemViewType: Int) : MediaEntryViewHolder(itemView) {

        init {
            itemView.setOnLongClickListener(null)
            if (itemViewType == TRACK) {
                menu?.setOnClickListener(object : TrackMenuHelper.OnClickTrackMenu(activity) {
                    override val track: TrackParcelable
                        get() = dataSet[layoutPosition] as TrackParcelable
                })
            } else if (itemViewType == PLAYLIST) {
                menu?.setOnClickListener(object : PlaylistMenuHelper.OnClickPlaylistMenu(activity) {
                    override val playlist: PlaylistParcelable
                        get() = dataSet[layoutPosition] as PlaylistParcelable
                })
            } else {
                menu?.isVisible = false
            }

            when (itemViewType) {
                ALBUM -> setImageTransitionName(activity.getString(R.string.transition_album_image))
                ARTIST -> setImageTransitionName(activity.getString(R.string.transition_artist_image))
                PLAYLIST -> setImageTransitionName(activity.getString(R.string.transition_playlist_image))
                else -> {}
            }
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            val item = dataSet[layoutPosition]
            when (itemViewType) {
                ALBUM -> {
                    activity.findNavController(R.id.fragment_container).navigate(
                        R.id.albumDetailsFragment,
                        bundleOf(EXTRA_ALBUM_ID to (item as AlbumParcelable).id)
                    )
                }

                ARTIST -> {
                    activity.findNavController(R.id.fragment_container).navigate(
                        R.id.artistDetailsFragment,
                        bundleOf(EXTRA_ARTIST_ID to (item as ArtistParcelable).id)
                    )
                }

                PLAYLIST -> {
                    activity.findNavController(R.id.fragment_container).navigate(
                        R.id.playlistDetailsFragment,
                        bundleOf(EXTRA_PLAYLIST_ID to (item as PlaylistParcelable).id)
                    )
                }

                TRACK -> {
                    AppRemoteHelper.playUri((item as TrackParcelable).uri, false)
                }
            }
        }
    }

    companion object {
        private const val HEADER = 0
        private const val ALBUM = 1
        private const val ARTIST = 2
        private const val PLAYLIST = 3
        private const val TRACK = 4
    }
}