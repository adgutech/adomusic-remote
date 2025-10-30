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

package com.adgutech.adomusic.remote.ui.fragments.playlists

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spanned
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.common.ATHToolbarActivity.getToolbarBackgroundColor
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.playlist.PlaylistTracksAdapter
import com.adgutech.adomusic.remote.databinding.FragmentPlaylistDetailsBinding
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.glide.GlideExtension.playlistImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.api.Result
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import com.adgutech.adomusic.remote.ui.dialogs.EditPlaylistWithDescDialog
import com.adgutech.adomusic.remote.ui.dialogs.RemovePlaylistDialog
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.clearText
import com.adgutech.commons.extensions.elevatedAccentColor
import com.adgutech.commons.extensions.isVisible
import com.adgutech.commons.extensions.showCircularProgress
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.extensions.surfaceColor
import com.bumptech.glide.Glide
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get

abstract class AbsPlaylistDetailsFragment :
    AbsMainActivityFragment(R.layout.fragment_playlist_details) {

    companion object {
        val TAG: String = AbsPlaylistDetailsFragment::class.java.simpleName
    }

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val toolbar: Toolbar
        get() = binding.toolbar

    abstract val playlistDetailsViewModel: PlaylistDetailsViewModel
    abstract val playlistId: String?

    private lateinit var playlist: Playlist
    private lateinit var playlistTracksAdapter: PlaylistTracksAdapter
    private var description: Spanned? = null

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
        _binding = FragmentPlaylistDetailsBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        serviceActivity?.addSpotifyServiceEventListener(playlistDetailsViewModel)
        binding.toolbar.title = null
        binding.image.transitionName = playlistId
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        playlistDetailsViewModel.getPlaylistDetails().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    view.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                    binding
                        .progressImage
                        .showCircularProgress(requireContext(), false)
                    showPlaylist(result.data)
                }

                is Result.Loading -> {
                    binding
                        .progressImage
                        .showCircularProgress(requireContext(), true)
                }

                is Result.Error -> {
                    binding
                        .progressImage
                        .showCircularProgress(requireContext(), false)
                    logE("Error loading playlist details : ${result.error}")
                }
            }
        }
        playlistDetailsViewModel.getPlaylistTracks().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding
                        .fragmentPlaylistContent
                        .progress.showCircularProgress(requireContext(), false)
                    val tracks = result.data
                    if (tracks.isNotEmpty()) {
                        playlistTracksAdapter.swapDataSet(tracks)
                        for (track in result.data) {
                            (trackIds as ArrayList).add(track.uri)
                        }
                    } else {
                        playlistTracksAdapter.swapDataSet(listOf())
                    }
                }
                is Result.Loading -> {
                    binding
                        .fragmentPlaylistContent
                        .progress.showCircularProgress(requireContext(), true)
                }
                is Result.Error -> {
                    binding
                        .fragmentPlaylistContent
                        .progress.showCircularProgress(requireContext(), false)
                    logE("Error loading playlist tracks : ${result.error}")
                }
            }
        }
        setupRecyclerView()

        binding.fragmentPlaylistContent.playAction.apply {
            accentColor(requireContext())
            setOnClickListener { AppRemoteHelper.playUri(playlist.uri, true) }
        }
        binding.fragmentPlaylistContent.shuffleAction.apply {
            elevatedAccentColor(requireContext())
            setOnClickListener { AppRemoteHelper.playUri(playlist.uri, true) }
        }

        binding.fragmentPlaylistContent.descriptionText.setOnClickListener {
            if (binding.fragmentPlaylistContent.descriptionText.maxLines == 4) {
                binding.fragmentPlaylistContent.descriptionText.maxLines = Integer.MAX_VALUE
            } else {
                binding.fragmentPlaylistContent.descriptionText.maxLines = 4
            }
        }
        binding.appBarLayout?.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
    }

    private fun showPlaylist(playlist: Playlist) {
        this.playlist = playlist
        loadPlaylistDetails(playlist)
    }

    private fun loadPlaylistDetails(playlist: Playlist) {
        if (playlist.name != null && playlist.description != null) {
            binding.title.text = playlist.name
            binding.text.text = Utils.getInfoString(
                requireContext(),
                playlistTracksAdapter.dataSet,
                playlist.tracks.total
            )
            if (playlist.description.trim { it <= ' ' }.isNotEmpty()) {
                binding.fragmentPlaylistContent.run {
                    descriptionTitle.isVisible()
                    descriptionText.isVisible()
                    description = playlist.description.parseAsHtml()
                    descriptionText.text = description
                }
            }
            Glide.with(requireContext())
                .load(Utils.getImageUrl(playlist.images ?: listOf()))
                .playlistImageOptions(playlist).dontAnimate()
                .into(binding.image)
        }
    }

    private fun setupRecyclerView() {
        playlistTracksAdapter = PlaylistTracksAdapter(requireActivity(), ArrayList())
        binding.fragmentPlaylistContent.recyclerView.apply {
            adapter = playlistTracksAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DraggableItemAnimator()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.searchView.clearText()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        serviceActivity?.removeSpotifyServiceEventListener(playlistDetailsViewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_playlist_details, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            toolbar,
            menu,
            getToolbarBackgroundColor(toolbar)
        )
        if (::playlist.isInitialized) {
            val isCurrentUserId = playlist.owner.id == userId
            menu.findItem(R.id.action_edit).isVisible = isCurrentUserId
            //menu.findItem(R.id.action_upload_image).isVisible = isCurrentUserId
            if (playlist.owner.id == userId) {
                menu.findItem(R.id.action_save_playlist_to_library).isVisible = false
                menu.findItem(R.id.action_remove_playlist_from_library).isVisible = false
            } else {
                AppRemoteHelper.getLibraryState(playlist.uri!!) {
                    menu.findItem(R.id.action_save_playlist_to_library).isVisible = !it
                    menu.findItem(R.id.action_remove_playlist_from_library).isVisible = it
                }
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.action_add_to_playlist -> {
                if (playlistTracksAdapter.dataSet.isEmpty()) {
                    showToast(String.format(getString(R.string.empty_playlist_tracks), playlist.name))
                    return false
                }
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
                            playlist.external_urls[SpotifyService.SPOTIFY_URL]!!,
                            false
                        ),
                        String.format(getString(R.string.text_share_playlist), playlist.name)
                    )
                )
                return true
            }
            R.id.action_see_details_on_spotify -> {
                startActivity(
                    Utils.seeDetailsOnSpotify(
                        playlist.external_urls[SpotifyService.SPOTIFY_URL]!!,
                        false
                    )
                )
                return true
            }
            R.id.action_edit -> {
                EditPlaylistWithDescDialog.create(playlist)
                    .show(childFragmentManager, "EditPlaylistWithDescDialog")
                return true
            }
            /*R.id.action_upload_image -> {
                return true
            }*/
            R.id.action_save_playlist_to_library -> {
                libraryViewModel.followPlaylist(playlist.name, playlist.id)
                return true
            }
            R.id.action_remove_playlist_from_library -> {
                RemovePlaylistDialog.create(playlist).show(
                    childFragmentManager, "RemovePlaylistDialog"
                )
                return true
            }
        }
        return false
    }
}