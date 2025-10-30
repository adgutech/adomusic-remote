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

package com.adgutech.adomusic.remote.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.AnimRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.glide.palette.BitmapPaletteWrapper
import com.adgutech.adomusic.remote.glide.trackcoverimage.TrackCoverImage
import com.adgutech.adomusic.remote.models.AlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistAlbumParcelable
import com.adgutech.adomusic.remote.models.ArtistParcelable
import com.adgutech.adomusic.remote.models.PlaylistParcelable
import com.adgutech.adomusic.remote.api.spotify.models.Album
import com.adgutech.adomusic.remote.api.spotify.models.Artist
import com.adgutech.adomusic.remote.api.spotify.models.Playlist
import com.adgutech.adomusic.remote.api.spotify.models.UserPrivate
import com.adgutech.adomusic.remote.api.spotify.models.UserPublic
import com.adgutech.adomusic.remote.utils.ImageSignatureUtil
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.spotify.protocol.types.Track

/**
 * Created by Adolfo Gutierrez on 03/20/25.
 */

object GlideExtension {

    private val DEFAULT_ALBUM_IMAGE: Int
        @DrawableRes get() = R.drawable.img_default_album_art
    private val DEFAULT_ARTIST_IMAGE: Int
        @DrawableRes get() = R.drawable.img_default_artist_art
    private val DEFAULT_TRACK_IMAGE: Int
        @DrawableRes get() = R.drawable.img_default_audio_art

    private val DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.RESOURCE

    private val DEFAULT_ANIMATION: Int
        @AnimRes get() = android.R.anim.fade_in

    fun RequestManager.asBitmapPalette(): RequestBuilder<BitmapPaletteWrapper> {
        return this.`as`(BitmapPaletteWrapper::class.java)
    }

    fun getTrackCoverModel(bitmap: Bitmap): Any {
        return TrackCoverImage(bitmap)
    }

    fun <T> RequestBuilder<T>.albumCoverOptions(album: Album): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ALBUM_IMAGE))
            .placeholder(getDrawable(DEFAULT_ALBUM_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(album))
    }

    fun <T> RequestBuilder<T>.albumCoverOptions(album: AlbumParcelable): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ALBUM_IMAGE))
            .placeholder(getDrawable(DEFAULT_ALBUM_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(album))
    }

    fun <T> RequestBuilder<T>.albumCoverOptions(album: ArtistAlbumParcelable): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ALBUM_IMAGE))
            .placeholder(getDrawable(DEFAULT_ALBUM_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(album))
    }

    fun <T> RequestBuilder<T>.artistImageOptions(artist: Artist): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ARTIST_IMAGE))
            .placeholder(getDrawable(DEFAULT_ARTIST_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(artist))
    }

    fun <T> RequestBuilder<T>.artistImageOptions(artist: ArtistParcelable): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ARTIST_IMAGE))
            .placeholder(getDrawable(DEFAULT_ARTIST_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(artist))
    }

    fun <T> RequestBuilder<T>.playlistImageOptions(playlist: Playlist): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_TRACK_IMAGE))
            .placeholder(getDrawable(DEFAULT_TRACK_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(playlist))
    }

    fun <T> RequestBuilder<T>.playlistImageOptions(playlist: PlaylistParcelable): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_TRACK_IMAGE))
            .placeholder(getDrawable(DEFAULT_TRACK_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(playlist))
    }

    fun <T> RequestBuilder<T>.trackImageOptions(): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_TRACK_IMAGE))
            .placeholder(getDrawable(DEFAULT_TRACK_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
    }

    fun <T> RequestBuilder<T>.trackCoverOptions(track: Track): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_TRACK_IMAGE))
            .placeholder(getDrawable(DEFAULT_TRACK_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(track))
    }

    fun <T> RequestBuilder<T>.userImageOptions(user: UserPrivate): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ARTIST_IMAGE))
            .placeholder(getDrawable(DEFAULT_ARTIST_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(user))
    }

    fun <T> RequestBuilder<T>.userImageOptions(user: UserPublic): RequestBuilder<T> {
        return diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ARTIST_IMAGE))
            .placeholder(getDrawable(DEFAULT_ARTIST_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(user))
    }

    private fun createSignature(album: Album): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getAlbumSignature(album.name)
    }

    private fun createSignature(album: AlbumParcelable): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getAlbumSignature(album.name)
    }

    private fun createSignature(album: ArtistAlbumParcelable): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getAlbumSignature(album.name)
    }

    private fun createSignature(artist: Artist): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getArtistSignature(artist.name)
    }

    private fun createSignature(artist: ArtistParcelable): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getArtistSignature(artist.name)
    }

    private fun createSignature(playlist: Playlist): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getPlaylistSignature(playlist.name)
    }

    private fun createSignature(playlist: PlaylistParcelable): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getPlaylistSignature(playlist.title)
    }

    private fun createSignature(track: Track): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getTrackCoverSignature(track.name)
    }

    private fun createSignature(user: UserPrivate): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getTrackCoverSignature(user.display_name)
    }

    private fun createSignature(user: UserPublic): Key {
        return ImageSignatureUtil.getInstance(App.getInstance())
            .getTrackCoverSignature(user.display_name)
    }

    fun <T> getDefaultTransition(): GenericTransitionOptions<T> {
        return GenericTransitionOptions<T>().transition(DEFAULT_ANIMATION)
    }

    private fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(App.getInstance(), id)
    }
}

// https://github.com/bumptech/glide/issues/527#issuecomment-148840717
fun RequestBuilder<Drawable>.crossfadeListener(): RequestBuilder<Drawable> {
    return listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            return if (isFirstResource) {
                false // thumbnail was not shown, do as usual
            } else DrawableCrossFadeFactory.Builder()
                .setCrossFadeEnabled(true).build()
                .build(dataSource, isFirstResource)
                .transition(resource, target as Transition.ViewAdapter)
        }
    })
}