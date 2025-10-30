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
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.artist.TopArtistsAdapter
import com.adgutech.adomusic.remote.adapters.track.TrackAdapter
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.Home
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.ui.fragments.home.HomeFragment
import com.adgutech.adomusic.remote.ui.fragments.home.LIKED_SONGS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_ARTISTS
import com.adgutech.adomusic.remote.ui.fragments.home.TOP_TRACKS

class HomeAdapter(
    private val activity: FragmentActivity
) : RecyclerView.Adapter<HomeAdapter.AbsHomeViewItem>(),
    TopArtistsAdapter.OnTopArtistsClickListener {

    private var list = listOf<Home>()

    override fun getItemViewType(position: Int): Int {
        return list[position].homeSection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsHomeViewItem {
        val layout =
            LayoutInflater.from(activity).inflate(R.layout.section_recycler_view, parent, false)
        return when (viewType) {
            LIKED_SONGS -> LikedSongsViewHolder(layout)
            TOP_ARTISTS -> TopArtistViewHolder(layout)
            TOP_TRACKS -> TopTrackViewHolder(layout)
            else -> TopTrackViewHolder(layout)
        }
    }

    override fun onBindViewHolder(holder: AbsHomeViewItem, position: Int) {
        val home = list[position]
        when (getItemViewType(position)) {
            LIKED_SONGS -> {
                val viewHolder = holder as LikedSongsViewHolder
                viewHolder.bindView(home)
                viewHolder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity.findNavController(R.id.fragment_container)
                        .navigate(
                            R.id.detailsListFragment,
                            bundleOf("type" to LIKED_SONGS)
                        )
                }
            }

            TOP_ARTISTS -> {
                val viewHolder = holder as TopArtistViewHolder
                viewHolder.bindView(home)
                viewHolder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity.findNavController(R.id.fragment_container)
                        .navigate(
                            R.id.detailsListFragment,
                            bundleOf("type" to TOP_ARTISTS)
                        )
                }
            }

            TOP_TRACKS -> {
                val viewHolder = holder as TopTrackViewHolder
                viewHolder.bindView(home)
                viewHolder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity.findNavController(R.id.fragment_container)
                        .navigate(
                            R.id.detailsListFragment,
                            bundleOf("type" to TOP_TRACKS)
                        )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun setOnTopArtistsClickListener(artistId: String?, view: View) {
        activity.findNavController(R.id.fragment_container).navigate(
            R.id.artistDetailsFragment,
            bundleOf(EXTRA_ARTIST_ID to artistId),
            null,
            FragmentNavigatorExtras(view to artistId!!)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapData(sections: List<Home>) {
        list = sections
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    private inner class LikedSongsViewHolder(itemView: View) : AbsHomeViewItem(itemView) {
        fun bindView(home: Home) {
            when (home.arrayList) {
                is Result.Loading -> {}
                is Result.Success -> {
                    title.setText(home.titleRes)
                    recyclerView.apply {
                        layoutManager = linearLayoutManager()
                        adapter = getTrackAdapter(home.arrayList.data.toMutableList() as MutableList<TrackParcelable>)
                    }
                }

                is Result.Error -> {}
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inner class TopArtistViewHolder(itemView: View) : AbsHomeViewItem(itemView) {
        fun bindView(home: Home) {
            when (home.arrayList) {
                is Result.Loading -> {}
                is Result.Success -> {
                    title.setText(home.titleRes)
                    recyclerView.apply {
                        layoutManager = linearLayoutManager()
                        adapter =
                            getTopArtistsAdapter(home.arrayList.data as List<ArtistParcelable>)
                    }
                }

                is Result.Error -> {}
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inner class TopTrackViewHolder(itemView: View) : AbsHomeViewItem(itemView) {
        fun bindView(home: Home) {
            when (home.arrayList) {
                is Result.Loading -> {}
                is Result.Success -> {
                    title.setText(home.titleRes)
                    recyclerView.apply {
                        layoutManager = linearLayoutManager()
                        adapter = getTrackAdapter(home.arrayList.data as List<TrackParcelable>)
                    }
                }

                is Result.Error -> {}
            }
        }
    }

    open class AbsHomeViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
        val title: AppCompatTextView = itemView.findViewById(R.id.title)
        val clickableArea: ViewGroup = itemView.findViewById(R.id.clickable_area)
    }

    private fun getTopArtistsAdapter(artists: List<ArtistParcelable>): TopArtistsAdapter {
        return TopArtistsAdapter(activity, artists, this)
    }

    private fun getTrackAdapter(tracks: List<TrackParcelable>): TrackAdapter {
        return TrackAdapter(activity, tracks.toMutableList(), R.layout.item_album_grid)
    }

    private fun gridLayoutManager() =
        GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)

    private fun linearLayoutManager() =
        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
}