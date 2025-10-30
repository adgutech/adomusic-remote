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

package com.adgutech.adomusic.remote.ui.fragments.albums

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import com.adgutech.adomusic.remote.COMPILATION
import com.adgutech.adomusic.remote.EXTRA_ALBUM_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_ID
import com.adgutech.adomusic.remote.EXTRA_ARTIST_NAME
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.adapters.album.AlbumHorizontalAdapter
import com.adgutech.adomusic.remote.adapters.album.AlbumTracksAdapter
import com.adgutech.adomusic.remote.databinding.FragmentAlbumDetailsBinding
import com.adgutech.adomusic.remote.extensions.findActivityNavController
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.glide.GlideExtension.albumCoverOptions
import com.adgutech.adomusic.remote.glide.GlideExtension.artistImageOptions
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.api.Result.*
import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.repositories.RealRepository
import com.adgutech.adomusic.remote.ui.dialogs.AddToPlaylistDialog
import com.adgutech.adomusic.remote.ui.fragments.ReloadType
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsMainActivityFragment
import com.adgutech.adomusic.remote.utils.Utils
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.elevatedAccentColor
import com.adgutech.commons.extensions.isGone
import com.adgutech.commons.extensions.surfaceColor
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get

/**
 * Created by Adolfo Gutiérrez on 06/04/2025.
 */

abstract class AbsAlbumDetailsFragment : AbsMainActivityFragment(R.layout.fragment_album_details),
    AlbumHorizontalAdapter.OnAlbumClickListener {

    companion object {
        val TAG: String = AbsAlbumDetailsFragment::class.java.simpleName
    }

    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private val toolbar: Toolbar
        get() = binding.toolbar

    private val saveLibraryButton: ImageView
        get() = binding.fragmentAlbumContent.saveLibraryButton

    abstract val albumDetailsViewModel: AlbumDetailsViewModel
    abstract val albumId: String?
    private lateinit var album: Album
    private lateinit var albumTracksAdapter: AlbumTracksAdapter
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
        _binding = FragmentAlbumDetailsBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        serviceActivity?.addSpotifyServiceEventListener(albumDetailsViewModel)
        binding.toolbar.title = null
        binding.image.transitionName = albumId
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        postponeEnterTransition()
        albumDetailsViewModel.getAlbumDetails().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("album details loading...")
                is Success -> {
                    view.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                    val album = result.data
                    showAlbumInfo(album)
                }

                is Error -> logE("album details error: ${result.error}")
            }
        }
        albumDetailsViewModel.getAlbumTracks().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("album tracks loading...")
                is Success -> {
                    val tracks = result.data
                    if (tracks.isNotEmpty()) {
                        albumTracksAdapter.swapDataSet(tracks)
                        for (track in tracks) {
                            (trackIds as ArrayList).add(track.uri)
                        }
                    } else {
                        albumTracksAdapter.swapDataSet(listOf())
                    }
                }
                is Error -> logE("album tracks error: ${result.error}")
            }
        }
        albumDetailsViewModel.checkAlbumSaved().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> { logD("check album saved loading...") }
                is Success -> {
                    for (isSaved in result.data) {
                        updateIsSavedAlbum(isSaved)
                        saveLibraryButton.setOnClickListener {
                            if (isSaved) {
                                albumDetailsViewModel.removeFromMySavedAlbums(albumId!!)
                            } else {
                                albumDetailsViewModel.addToMySavedAlbums(albumId!!)
                            }
                            libraryViewModel.forceReload(ReloadType.ALBUMS)
                        }
                    }
                }
                is Error -> logE("check album saved error: ${result.error}")
            }
        }
        setupRecyclerView()

        binding.fragmentAlbumContent.playAction.apply {
            setOnClickListener {
                AppRemoteHelper.playUri(album.uri, false)
            }
            accentColor(requireContext())
        }
        binding.fragmentAlbumContent.shuffleAction.apply {
            setOnClickListener {
                AppRemoteHelper.playUri(album.uri, true)
            }
            elevatedAccentColor(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceActivity?.removeSpotifyServiceEventListener(albumDetailsViewModel)
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
                            album.external_urls[SpotifyService.SPOTIFY_URL]!!,
                            false
                        ),
                        String.format(getString(R.string.text_share_album), album.name)
                    )
                )
                return true
            }
            R.id.action_see_details_on_spotify -> {
                startActivity(
                    Utils.seeDetailsOnSpotify(
                        album.external_urls[SpotifyService.SPOTIFY_URL]!!,
                        false
                    )
                )
                return true
            }
        }
        return false
    }

    override fun setOnAlbumClickListener(albumId: String?, view: View) {
        findNavController().navigate(
            R.id.albumDetailsFragment,
            bundleOf(EXTRA_ALBUM_ID to albumId),
            null,
            FragmentNavigatorExtras(view to albumId!!)
        )
    }

    private fun showAlbumInfo(album: Album) {
        this.album = album

        val trackCount = Utils.getTrackCountString(requireContext(), album.tracks.total)
        val duration = Utils.getTotalDuration(albumTracksAdapter.dataSet)
        val durationFormat = Utils.getReadableDurationStringNew(duration)

        val albumInfoString =
            String.format("%s • %s", trackCount, durationFormat)

        binding.text.text = albumInfoString
        binding.title.text = album.name

        Glide.with(requireContext())
            .load(Utils.getImageUrl(album.images)).dontAnimate()
            .albumCoverOptions(album)
            .into(binding.image)

        binding.fragmentAlbumContent.apply {
            releaseDateText.text = Utils.formatDate(album.release_date)
            copyrightText.text = album.copyrights[0].text
        }

        val artistId = album.artists[0].id

        albumDetailsViewModel.getArtistDetails(artistId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("artist details loading...")
                is Success -> {
                    val artist = result.data
                    setupArtistProfile(artist)
                }
                is Error -> logE("artist details error: ${result.error}")
            }
        }
        albumDetailsViewModel.getArtistAlbums(artistId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> logD("artist albums loading...")
                is Success -> {
                    val albums = result.data
                    val albumFilter = albums.filter {
                        it.id != album.id
                    }
                    if (albumFilter.isNotEmpty()) {
                        albumHorizontalAdapter.swapDataSet(albumFilter)
                    } else {
                        albumHorizontalAdapter.swapDataSet(listOf())
                    }
                }
                is Error -> logE("artist albums error: ${result.error}")
            }
        }

        val artistName = album.artists[0].name

        binding.fragmentAlbumContent.apply {
            title.text = String.format(getString(R.string.title_more_albums_from), artistName)
            clickableArea.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(requireView())
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.moreAlbumsFragment,
                    bundleOf(
                        EXTRA_ARTIST_ID to artistId,
                        EXTRA_ARTIST_NAME to artistName
                    )
                )
            }
        }
    }

    private fun setupRecyclerView() {
        albumTracksAdapter = AlbumTracksAdapter(requireActivity(), ArrayList())
        binding.fragmentAlbumContent.recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this.context)
            adapter = albumTracksAdapter
        }

        albumHorizontalAdapter = AlbumHorizontalAdapter(requireActivity(), ArrayList(), this)
        binding.fragmentAlbumContent.albumRecyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumHorizontalAdapter
        }
    }

    private fun setupArtistProfile(artist: Artist) {
        binding.fragmentAlbumContent.apply {
            if (album.album_type == COMPILATION) {
                artistText.isGone()
                artistImage.isGone()
                artistName.text = artist.name
                artistProfile.apply {
                    isClickable = false
                    isFocusable = false
                }
                clickableArea.isGone()
                albumRecyclerView.isGone()
            } else {
                artistName.text = artist.name
                artistText.text = Utils.getFollowersFormat(requireContext(), artist.followers.total)

                Glide.with(requireContext())
                    .load(Utils.getImageUrl(artist.images))
                    .artistImageOptions(artist)
                    .dontAnimate()
                    .dontTransform()
                    .into(artistImage)

                artistImage.transitionName = artist.id
                artistProfile.setOnClickListener {
                    findActivityNavController(R.id.fragment_container)
                        .navigate(
                            R.id.artistDetailsFragment,
                            bundleOf(EXTRA_ARTIST_ID to artist.id),
                            null,
                            FragmentNavigatorExtras(artistImage to artist.id)
                        )
                }
            }
        }
    }

    private fun updateIsSavedAlbum(isSaved: Boolean) {
        if (isSaved) {
            saveLibraryButton.setImageResource(R.drawable.ic_favorite_24dp)
        } else {
            saveLibraryButton.setImageResource(R.drawable.ic_favorite_border_24dp)
        }
    }
}