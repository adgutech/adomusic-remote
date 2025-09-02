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

package com.adgutech.adomusic.remote.ui.fragments.bases

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.extensions.currentFragment
import com.adgutech.adomusic.remote.extensions.goToProVersion
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.extensions.toAlbumId
import com.adgutech.adomusic.remote.extensions.toArtistId
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.interfaces.IPaletteColorHolder
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import com.adgutech.adomusic.remote.ui.dialogs.BrowseArtistsDialog
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.ui.fragments.players.PlayerAlbumCoverFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.getTintedDrawable
import com.adgutech.commons.extensions.hide
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.extensions.whichFragment
import com.adgutech.commons.hasVersionMarshmallow
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class AbsPlayerFragment(@LayoutRes layout: Int) : AbsSpotifyServiceFragment(layout),
    Toolbar.OnMenuItemClickListener, IPaletteColorHolder, PlayerAlbumCoverFragment.Callbacks {

    val libraryViewModel: LibraryViewModel by activityViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity

    private var playerAlbumCoverFragment: PlayerAlbumCoverFragment? = null

    override fun onMenuItemClick(
        item: MenuItem,
    ): Boolean {
        val track = AppRemoteHelper.currentTrack!!
        when (item.itemId) {
            /*R.id.action_lyrics -> {
                preference.showLyrics = !preference.showLyrics
                showLyricsIcon(item)
                if (preference.isLyricsScreenOn && preference.showLyrics) {
                    mainActivity.keepScreenOn(true)
                } else if (!preference.isScreenOnEnabled && !preference.showLyrics) {
                    mainActivity.keepScreenOn(false)
                }
                return true
            }*/

            R.id.action_toggle_liked_songs -> {
                toggleFavorite(track)
                return true
            }

            R.id.action_share -> {
                startActivity(
                    Intent.createChooser(
                        Utils.shareContentOfSpotify(track.uri, true),
                        String.format(getString(R.string.text_share_song), track.name)
                    )
                )
                return true
            }

            R.id.action_see_details_on_spotify -> {
                startActivity(
                    Utils.seeDetailsOnSpotify(track.album.uri, true)
                )
                return true
            }

            R.id.action_add_to_playlist -> {
                lifecycleScope.launch(IO) {
                    val playlists = get<RealRepository>().getMyPlaylistsForDialog()
                    withContext(Main) {
                        AddToPlaylistDialog.create(playlists, track.uri)
                            .show(childFragmentManager, "AddToPlaylistDialog")
                    }
                }
                return true
            }

            R.id.action_browse_album -> {
                //Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
                mainActivity.setBottomNavVisibility(false)
                mainActivity.collapsePanel()
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.albumDetailsFragment,
                    bundleOf(EXTRA_ALBUM_ID to track.album.uri.toAlbumId())
                )
                return true
            }

            R.id.action_browse_artist -> {
                goToArtist(requireActivity())
                return true
            }

            R.id.action_playing_queue -> {
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.playing_queue_fragment,
                    null,
                    navOptions { launchSingleTop = true }
                )
                return true
            }

            R.id.action_equalizer -> {
                if (!App.isProVersion()) {
                    showToast(
                        String.format(
                            getString(R.string.text_pro_feature),
                            getString(R.string.pro_equalizer)
                        )
                    )
                    requireContext().goToProVersion()
                    return false
                }
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.equalizer_fragment,
                    null,
                    navOptions { launchSingleTop = true }
                )
                return true
            }
        }
        return false
    }

    private fun showLyricsIcon(item: MenuItem) {
        val icon =
            if (preference.showLyrics) R.drawable.ic_lyrics_24dp else R.drawable.ic_lyrics_outline_24dp
        val drawable = requireContext().getTintedDrawable(
            icon,
            toolbarIconColor()
        )
        item.isChecked = preference.showLyrics
        item.icon = drawable
    }

    abstract fun playerToolbar(): Toolbar?

    abstract fun onShow()

    abstract fun onHide()

    abstract fun toolbarIconColor(): Int

    override fun onServiceConnected() {
        updateIsFavorite()
    }

    override fun onPlayerStateChanged() {
        updateIsFavorite()
    }

    protected open fun toggleFavorite(track: Track) {
        AppRemoteHelper.getLibraryState(track.uri) { isAdded ->
            if (isAdded) {
                AppRemoteHelper.removeFromLibrary(track.uri)
            } else {
                AppRemoteHelper.addToLibrary(track.uri)
            }
        }
    }

    fun updateIsFavorite() {
        val track = AppRemoteHelper.currentTrack!!
        AppRemoteHelper.getLibraryState(track.uri) {
            val icon = if (it) R.drawable.ic_favorite_24dp else R.drawable.ic_favorite_border_24dp
            val drawable = requireContext().getTintedDrawable(
                icon,
                toolbarIconColor()
            )
            if (playerToolbar() != null) {
                playerToolbar()?.menu?.findItem(R.id.action_toggle_liked_songs)?.apply {
                    setIcon(drawable)
                    title =
                        if (it) getString(R.string.action_remove_to_liked_songs)
                        else getString(R.string.action_save_to_liked_songs)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preference.isCirclePlayButton) {
            requireContext().theme.applyStyle(R.style.CircleFABOverlay, true)
        } else {
            requireContext().theme.applyStyle(R.style.RoundedFABOverlay, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (preference.isFullScreenMode &&
            view.findViewById<View>(
                com.adgutech.commons.R.id.status_bar
            ) != null
        ) {
            view.findViewById<View>(
                com.adgutech.commons.R.id.status_bar
            ).isVisible = false
        }
        playerAlbumCoverFragment = whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment?.setCallbacks(this)

        if (hasVersionMarshmallow) {
            view.findViewById<RelativeLayout>(R.id.statusBarShadow)?.hide()
        }
    }

    /*override fun onResume() {
        super.onResume()
        val nps = preference.nowPlayingScreen

        if (nps == NowPlayingScreen.Circle || nps == NowPlayingScreen.Peek || nps == NowPlayingScreen.Tiny) {
            playerToolbar()?.menu?.removeItem(R.id.action_lyrics)
        } else {
            playerToolbar()?.menu?.findItem(R.id.action_lyrics)?.apply {
                isChecked = preference.showLyrics
                showLyricsIcon(this)
            }
        }
    }*/

    companion object {
        val TAG: String = AbsPlayerFragment::class.java.simpleName
        const val VISIBILITY_ANIM_DURATION: Long = 300
    }
}

fun goToArtist(activity: Activity) {
    if (activity !is MainActivity) return
    val track = AppRemoteHelper.currentTrack!!
    activity.apply {
        if (track.artists!!.size >= 2) {
            CoroutineScope(IO).launch {
                withContext(Main) {
                    BrowseArtistsDialog.create(track)
                        .show(activity.supportFragmentManager, "BrowseArtistDialog")
                }
            }
        } else {
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
                bundleOf(EXTRA_ARTIST_ID to track.artist.uri.toArtistId())
            )
        }
    }
}

fun goToAlbum(activity: Activity) {
    if (activity !is MainActivity) return
    val track = AppRemoteHelper.currentTrack!!
    activity.apply {
        currentFragment(R.id.fragment_container)?.exitTransition = null

        //Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
        setBottomNavVisibility(false)
        if (getBottomSheetBehavior().state == BottomSheetBehavior.STATE_EXPANDED) {
            collapsePanel()
        }

        findNavController(R.id.fragment_container).navigate(
            R.id.albumDetailsFragment,
            bundleOf(EXTRA_ALBUM_ID to track.album.uri.toAlbumId())
        )
    }
}

fun goToLyrics(activity: Activity) {

}