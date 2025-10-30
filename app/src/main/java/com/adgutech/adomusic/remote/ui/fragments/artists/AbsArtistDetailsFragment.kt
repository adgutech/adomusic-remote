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

package com.adgutech.adomusic.remote.ui.fragments.artists

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.common.ATHToolbarActivity.getToolbarBackgroundColor
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_NAME
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.album.AlbumHorizontalAdapter
import com.adgutech.adomusic.remote.adapters.artist.ArtistTopTracksAdapter
import com.adgutech.adomusic.remote.databinding.FragmentArtistDetailsBinding
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.glide.GlideExtension.artistImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.elevatedAccentColor
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.extensions.isVisible
import com.adgutech.commons.extensions.surfaceColor
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get

abstract class AbsArtistDetailsFragment : AbsMainActivityFragment(R.layout.fragment_artist_details),
    AlbumHorizontalAdapter.OnAlbumClickListener {

    companion object {
        val TAG: String = AbsArtistDetailsFragment::class.java.simpleName
    }

    private var _binding: FragmentArtistDetailsBinding? = null
    private val binding get() = _binding!!

    private val toolbar: Toolbar
        get() = binding.toolbar

    abstract val artistDetailsViewModel: ArtistDetailsViewModel
    abstract val artistId: String?
    private lateinit var artist: Artist
    private lateinit var artistTopTracksAdapter: ArtistTopTracksAdapter
    private lateinit var albumHorizontalAdapter: AlbumHorizontalAdapter

    private var trackIds: List<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true).apply {
            drawingViewId = R.id.fragment_container
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor())
            setPathMotion(MaterialArcMotion())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArtistDetailsBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        serviceActivity?.addSpotifyServiceEventListener(artistDetailsViewModel)
        binding.toolbar.title = null
        binding.image.transitionName = artistId
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        artistDetailsViewModel.getArtistDetails().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("artist details loading...")
                is Success -> {
                    view.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                    showArtistInfo(result.data)
                }
                is Error -> logE("artist details error: ${result.error}")
            }
        }
        artistDetailsViewModel.getArtistTopTracks().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("artist top tracks loading...")
                is Success -> {
                    if (result.data.isNotEmpty()) {
                        artistTopTracksAdapter.swapDataSet(result.data)
                    } else {
                        artistTopTracksAdapter.swapDataSet(listOf())
                    }
                }
                is Error -> logE("artist top tracks error: ${result.error}")
            }
        }
        artistDetailsViewModel.getArtistAlbums().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("artist albums loading...")
                is Success -> {
                    if (result.data.isNotEmpty()) {
                        albumHorizontalAdapter.swapDataSet(result.data)
                    } else {
                        albumHorizontalAdapter.swapDataSet(listOf())
                    }
                }
                is Error -> logE("artist albums error: ${result.error}")
            }
        }
        artistDetailsViewModel.isFollowingArtists().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    logD("Check followed artist loading...")
                }
                is Success -> {
                    for (isFollowed in result.data) {
                        if (isFollowed) {
                            binding.fragmentArtistContent.followArtistAction.isGone()
                            binding.fragmentArtistContent.unfollowArtistAction.isVisible()
                        } else {
                            binding.fragmentArtistContent.followArtistAction.isVisible()
                            binding.fragmentArtistContent.unfollowArtistAction.isGone()
                        }
                    }
                }
                is Error -> logE("Check followed artist error: ${result.error}")
            }
        }
        setupRecyclerView()
        setupButtonFollowArtist()

        binding.fragmentArtistContent.playAction.apply {
            setOnClickListener {
                AppRemoteHelper.playUri(artist.uri, false)
            }
            accentColor(requireContext())
        }
        binding.fragmentArtistContent.shuffleAction.apply {
            setOnClickListener {
                AppRemoteHelper.playUri(artist.uri, true)
            }
            elevatedAccentColor(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceActivity?.removeSpotifyServiceEventListener(artistDetailsViewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_album_details, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            toolbar,
            menu,
            getToolbarBackgroundColor(toolbar)
        )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.action_add_to_playlist -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val playlists = get<RealRepository>().getMyPlaylistsForDialog()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, trackIds)
                            .show(childFragmentManager, "AddToPlaylistDialog")
                    }
                }
                return true
            }
            R.id.action_share -> {
                startActivity(
                    Intent.createChooser(
                        Utils.shareContentOfSpotify(
                            artist.external_urls[SpotifyService.SPOTIFY_URL]!!,
                            false
                        ),
                        String.format(getString(R.string.text_share_artist), artist.name)
                    )
                )
                return true
            }
            R.id.action_see_details_on_spotify -> {
                startActivity(
                    Utils.seeDetailsOnSpotify(
                        artist.external_urls[SpotifyService.SPOTIFY_URL]!!,
                        false
                    )
                )
                return true
            }
        }
        return false
    }

    override fun setOnAlbumClickListener(albumId: String?, view: View) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(
            R.id.albumDetailsFragment,
            bundleOf(EXTRA_ALBUM_ID to albumId),
            null,
            FragmentNavigatorExtras(view to albumId!!)
        )
    }

    private fun showArtistInfo(artist: Artist) {
        this.artist = artist
        binding.title.text = artist.name
        binding.text.text = getStringFormat(artist)
        Glide.with(requireContext())
            .load(Utils.getImageUrl(artist.images)).dontAnimate()
            .artistImageOptions(artist)
            .into(binding.image)

        binding.fragmentArtistContent.apply {
            clickableArea.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.moreAlbumsFragment,
                    bundleOf(
                        EXTRA_ARTIST_ID to artistId,
                        EXTRA_ARTIST_NAME to artist.name
                    )
                )
            }
        }
    }

    private fun getStringFormat(artist: Artist): String {
        val followersCount = Utils.formatValue(artist.followers.total.toFloat())
        return String.format("%s %s", followersCount, getString(R.string.followers))
    }

    private fun setupRecyclerView() {
        artistTopTracksAdapter = ArtistTopTracksAdapter(requireActivity(), ArrayList())
        binding.fragmentArtistContent.recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this.context)
            adapter = artistTopTracksAdapter
        }

        albumHorizontalAdapter = AlbumHorizontalAdapter(requireActivity(), ArrayList(), this)
        binding.fragmentArtistContent.albumRecyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumHorizontalAdapter
        }
    }

    private fun setupButtonFollowArtist() {
        binding.fragmentArtistContent.followArtistAction.apply {
            elevatedAccentColor(requireContext())
            setOnClickListener {
                artistDetailsViewModel.followArtists()
            }
        }
        binding.fragmentArtistContent.unfollowArtistAction.apply {
            accentColor(requireContext())
            setOnClickListener {
                artistDetailsViewModel.unfollowArtists()
            }
        }
    }
}