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

package com.adgutech.adomusic.remote.models

import com.adgutech.adomusic.remote.api.spotify.models.ArtistSimple
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumTrackParcelable(
    override val artistId: String,
    override val artists: List<ArtistSimple>,
    override val discNumber: Int,
    override val duration: Long,
    override val id: String,
    override val isExplicit: Boolean,
    override val spotifyUrl: String?,
    override val name: String,
    override val trackNumber: Int,
    override val type: String,
    override val uri: String
) : TrackParcelable(
    "",
    "",
    "",
    artistId,
    artists,
    discNumber,
    duration,
    id,
    "",
    isExplicit,
    spotifyUrl,
    name,
    trackNumber,
    type,
    uri
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackParcelable

        if (artistId != other.artistId) return false
        if (artists != other.artists) return false
        if (discNumber != other.discNumber) return false
        if (duration != other.duration) return false
        if (id != other.id) return false
        if (isExplicit != other.isExplicit) return false
        if (spotifyUrl != other.spotifyUrl) return false
        if (name != other.name) return false
        if (trackNumber != other.trackNumber) return false
        if (type != other.type) return false
        if (uri != other.uri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = artistId.hashCode()
        result = 31 * result + artists.hashCode()
        result = 31 * result + discNumber.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + isExplicit.hashCode()
        result = 31 * result + spotifyUrl.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + trackNumber.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + uri.hashCode()
        return result
    }

    companion object {

        @JvmStatic
        val empty = AlbumTrackParcelable(
            artistId = "",
            artists = listOf(),
            discNumber = 0,
            duration = 0L,
            id = "",
            isExplicit = false,
            spotifyUrl = "",
            name = "",
            trackNumber = 0,
            type = "",
            uri = ""
        )
    }
}