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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.bases.CustomFragmentStatePagerAdapter
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.glide.GlideExtension
import com.adgutech.adomusic.remote.glide.GlideExtension.asBitmapPalette
import com.adgutech.adomusic.remote.glide.GlideExtension.trackCoverOptions
import com.adgutech.adomusic.remote.glide.RetroMusicColoredTarget
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.ui.fragments.NowPlayingScreen.*
import com.adgutech.commons.ui.color.MediaNotificationProcessor
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.Track

class AlbumCoverPagerAdapter(
    fragmentManager: FragmentManager,
    private val dataSet: List<Track>
) : CustomFragmentStatePagerAdapter(fragmentManager) {

    private var currentColorReceiver: AlbumCoverFragment.ColorReceiver? = null
    private var currentColorReceiverPosition = -1

    override fun getItem(position: Int): Fragment {
        return AlbumCoverFragment.newInstance(dataSet[position])
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val o = super.instantiateItem(container, position)
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver!!, currentColorReceiverPosition)
        }
        return o
    }

    /**
     * Only the latest passed [AlbumCoverFragment.ColorReceiver] is guaranteed to receive a
     * response
     */
    fun receiveColor(colorReceiver: AlbumCoverFragment.ColorReceiver, position: Int) {

        if (getFragment(position) is AlbumCoverFragment) {
            val fragment = getFragment(position) as AlbumCoverFragment
            currentColorReceiver = null
            currentColorReceiverPosition = -1
            fragment.receiveColor(colorReceiver, position)
        } else {
            currentColorReceiver = colorReceiver
            currentColorReceiverPosition = position
        }
    }

    class AlbumCoverFragment : Fragment() {

        private var isColorReady: Boolean = false
        private lateinit var color: MediaNotificationProcessor
        private lateinit var track: Track
        private var colorReceiver: ColorReceiver? = null
        private var request: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val trackGson = arguments?.getString(TRACK_ARG)
            track = Gson().fromJson(trackGson, Track::class.java)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(getLayoutWithPlayerTheme(), container, false)
            return view
        }

        private fun getLayoutWithPlayerTheme(): Int {
            return if (preference.nowPlayingScreen == Peek) {
                R.layout.fragment_peek_album_cover
            } else {
                R.layout.fragment_album_cover
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            loadAlbumCover(albumCover = view.findViewById(R.id.player_image))
        }

        override fun onDestroyView() {
            super.onDestroyView()
            colorReceiver = null
        }

        private fun loadAlbumCover(albumCover: ImageView) {
            AppRemoteHelper.getImage(track.imageUri, Image.Dimension.LARGE) {
                Glide.with(this)
                    .asBitmapPalette()
                    .trackCoverOptions(track)
                    .load(GlideExtension.getTrackCoverModel(it))
                    .dontAnimate()
                    .into(object : RetroMusicColoredTarget(albumCover) {
                        override fun onColorReady(colors: MediaNotificationProcessor) {
                            setColor(colors)
                        }
                    })
            }
        }

        private fun setColor(color: MediaNotificationProcessor) {
            this.color = color
            isColorReady = true
            if (colorReceiver != null) {
                colorReceiver!!.onColorReady(color, request)
                colorReceiver = null
            }
        }

        internal fun receiveColor(colorReceiver: ColorReceiver, request: Int) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request)
            } else {
                this.colorReceiver = colorReceiver
                this.request = request
            }
        }

        interface ColorReceiver {
            fun onColorReady(color: MediaNotificationProcessor, request: Int)
        }

        companion object {

            private const val TRACK_ARG = "track"

            fun newInstance(track: Track): AlbumCoverFragment {
                val trackGson = Gson().toJson(track)
                return  AlbumCoverFragment().apply {
                    arguments = bundleOf(TRACK_ARG to trackGson)
                }
            }
        }
    }

    companion object {
        val TAG: String = AlbumCoverPagerAdapter::class.java.simpleName
    }
}