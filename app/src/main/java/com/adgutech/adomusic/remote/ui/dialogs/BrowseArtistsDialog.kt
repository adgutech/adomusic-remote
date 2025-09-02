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

package com.adgutech.adomusic.remote.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.EXTRA_CURRENTLY_TRACK
import com.adgutech.adomusic.remote.EXTRA_TRACK
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.currentFragment
import com.adgutech.adomusic.remote.extensions.findNavController
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.extensions.toArtistId
import com.adgutech.adomusic.remote.models.ArtistTrackParcelable
import com.adgutech.adomusic.remote.models.PlaylistTrackParcelable
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.spotify.models.ArtistSimple
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.commons.extensions.colorButtons
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.Track

/**
 * Created by Adolfo Gutiérrez on 06/06/2025.
 */

class BrowseArtistsDialog : DialogFragment() {

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    companion object {
        fun create(track: ArtistTrackParcelable): BrowseArtistsDialog {
            return BrowseArtistsDialog().apply {
                arguments = bundleOf(EXTRA_TRACK to track)
            }
        }

        fun create(track: PlaylistTrackParcelable): BrowseArtistsDialog {
            return BrowseArtistsDialog().apply {
                arguments = bundleOf(EXTRA_TRACK to track)
            }
        }

        fun create(currentlyTrack: Track): BrowseArtistsDialog {
            val trackGson = Gson().toJson(currentlyTrack)
            return BrowseArtistsDialog().apply {
                arguments = bundleOf(EXTRA_CURRENTLY_TRACK to trackGson)
            }
        }

        fun create(track: TrackParcelable): BrowseArtistsDialog {
            return BrowseArtistsDialog().apply {
                arguments = bundleOf(EXTRA_TRACK to track)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val trackGson = arguments?.getString(EXTRA_CURRENTLY_TRACK)
        val currentlyTrack = Gson().fromJson(trackGson, Track::class.java)

        val track = getParcelableTrack(TrackParcelable::class.java)
        val artistTrack = getParcelableTrack(ArtistTrackParcelable::class.java)
        val playlistTrack = getParcelableTrack(PlaylistTrackParcelable::class.java)

        val artists = arrayListOf<Artist>()
        val artistsSimple = arrayListOf<ArtistSimple>()
        val artistName = mutableListOf<String>()

        if (currentlyTrack != null) {
            for (artist in currentlyTrack.artists!!) {
                artists.add(artist)
                artistName.add(artist.name)
            }
        }

        if (track != null) {
            for (artist in track.artists) {
                artistsSimple.add(artist)
                artistName.add(artist.name)
            }
        }

        if (artistTrack != null) {
            for (artist in artistTrack.artists) {
                artistsSimple.add(artist)
                artistName.add(artist.name)
            }
        }

        if (playlistTrack != null) {
            for (artist in playlistTrack.artists) {
                artistsSimple.add(artist)
                artistName.add(artist.name)
            }
        }

        return materialDialog(R.string.title_browse_artist_on)
            .setItems(artistName.toTypedArray()) { dialog, witch ->
                if (track != null) {
                    activity?.findNavController(R.id.fragment_container)?.navigate(
                        R.id.artistDetailsFragment,
                        bundleOf(EXTRA_ARTIST_ID to artistsSimple[witch].id)
                    )
                }
                if (artistTrack != null) {
                    activity?.findNavController(R.id.fragment_container)?.navigate(
                        R.id.artistDetailsFragment,
                        bundleOf(EXTRA_ARTIST_ID to artistsSimple[witch].id)
                    )
                }
                if (currentlyTrack != null) {
                    mainActivity.apply {
                        // Remove exit transition of current fragment so
                        // it doesn't exit with a weird transition
                        currentFragment(R.id.fragment_container)?.exitTransition = null

                        //Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
                        setBottomNavVisibility(false)
                        if (getBottomSheetBehavior().state == BottomSheetBehavior.STATE_EXPANDED) {
                            collapsePanel()
                        }

                        findNavController(R.id.fragment_container).navigate(
                            R.id.artistDetailsFragment,
                            bundleOf(EXTRA_ARTIST_ID to artists[witch].uri.toArtistId())
                        )
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }

    private fun <T> getParcelableTrack(clazz: Class<T>): T? {
        return BundleCompat.getParcelable(requireArguments(), EXTRA_TRACK, clazz)
    }
}