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

package com.adgutech.adomusic.remote.application

import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.api.spotify.SpotifyApi
import com.adgutech.adomusic.remote.api.spotify.provideSpotifyRest
import com.adgutech.adomusic.remote.repositories.*
import com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
import com.adgutech.adomusic.remote.ui.fragments.albums.AlbumDetailsViewModel
import com.adgutech.adomusic.remote.ui.fragments.artists.ArtistDetailsViewModel
import com.adgutech.adomusic.remote.ui.fragments.playlists.PlaylistDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Created by Adolfo Gutiérrez on 03/03/2025.
 */

private val networkModule = module {
    single {
        SpotifyApi(get())
    }
    factory {
        provideSpotifyRest(get(), get())
    }
}

private val preferenceModule = module {
    single {
        androidContext().preference
    }
}

private val dataModule = module {
    single {
        RealRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    } bind Repository::class

    single {
        RealAlbumRepository(get(), get())
    } bind AlbumRepository::class

    single {
        RealArtistRepository(get(), get())
    } bind ArtistRepository::class

    single {
        RealPlayerRepository(get())
    } bind PlayerRepository::class

    single {
        RealPlaylistRepository(get(), get())
    } bind PlaylistRepository::class

    single {
        RealTrackRepository(get())
    } bind TrackRepository::class

    single {
        RealTopPlayedRepository(get(), get())
    } bind TopPlayedRepository::class

    single {
        RealUserRepository(get())
    } bind UserRepository::class

    single {
        RealSearchRepository(get())
    } bind SearchRepository::class

}

@Suppress("DEPRECATION")
private val viewModelModule = module {
    viewModel {
        LibraryViewModel(get(), get(), get())
    }

    viewModel { (albumId: String) ->
        AlbumDetailsViewModel(get(), albumId)
    }

    viewModel { (artistId: String) ->
        ArtistDetailsViewModel(get(), artistId)
    }

    viewModel { (playlistId: String) ->
        PlaylistDetailsViewModel(get(), playlistId)
    }
}

val appModules = listOf(dataModule, viewModelModule, networkModule, preferenceModule)