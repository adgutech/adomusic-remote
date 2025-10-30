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

package com.adgutech.adomusic.remote.adapters.playlist

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
import com.adgutech.adomusic.remote.glide.GlideExtension.playlistImageOptions
import com.adgutech.adomusic.remote.helpers.SortOrder
import com.adgutech.adomusic.remote.helpers.menu.PlaylistMenuHelper
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.utils.Utils
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider

/**
 * Created by Adolfo Gutierrez on 04/17/25.
 */

class PlaylistAdapter(
    val activity: FragmentActivity,
    var dataSet: MutableList<PlaylistParcelable>,
    val listener: OnPlaylistClickListener
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), PopupTextProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_playlist_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = dataSet[position]
        holder.title?.text = playlist.title
        holder.text?.text =
            Utils.getPlaylistInfoString(activity, playlist.displayName, playlist.trackTotal)
        val icon = if (playlist.isPublic) R.drawable.ic_public_16dp else R.drawable.ic_lock_16dp
        holder.icon?.setImageResource(icon)
        holder.image?.transitionName = playlist.id
        loadTrackImage(playlist, holder)
    }

    override fun getPopupText(view: View, position: Int): CharSequence {
        val sectionName: String? = when (activity.preference.playlistSortOrder) {
            SortOrder.PlaylistSortOrder.PLAYLIST_A_Z, SortOrder.PlaylistSortOrder.PLAYLIST_Z_A -> {
                dataSet[position].title
            }

            SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME,
            SortOrder.PlaylistSortOrder.PLAYLIST_DISPLAY_NAME_DESC -> {
                dataSet[position].displayName
            }

            else -> return ""
        }
        return com.adgutech.commons.utils.Utils.getSectionName(sectionName)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<PlaylistParcelable>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    private fun loadTrackImage(playlist: PlaylistParcelable, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        Glide.with(activity)
            .load(Utils.getImageUrl(playlist.images!!))
            .playlistImageOptions(playlist)
            .transition(GlideExtension.getDefaultTransition())
            .into(holder.image!!)
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        private val playlistMenuRes = PlaylistMenuHelper.MENU_RES

        private val playlist: PlaylistParcelable
            get() = dataSet[layoutPosition]

        init {
            menu?.setOnClickListener(object : PlaylistMenuHelper.OnClickPlaylistMenu(activity) {

                override val playlist: PlaylistParcelable
                    get() = this@ViewHolder.playlist

                override val menuRes: Int
                    get() = playlistMenuRes
            })
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            image?.let {
                listener.setOnPlaylistClickListener(playlist.id, it)
            }
        }
    }

    interface OnPlaylistClickListener {
        fun setOnPlaylistClickListener(playlistId: String?, view: View)
    }
}