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

import android.os.Parcel
import android.os.Parcelable
import com.adgutech.adomusic.remote.api.spotify.models.ArtistSimple

class ArtistAlbumParcelable() : Parcelable {

    var albumType: String? = null
    var artist: String? = null
    var artistId: String? = null
    var artists: List<ArtistSimple>? = null
    var id: String? = null
    var imageUrl: String? = null
    var name: String? = null
    var releaseDate: String? = null
    var type: String? = null
    var uri: String? = null

    constructor(
        albumType: String,
        artist: String,
        artistId: String,
        artists: List<ArtistSimple>,
        id: String,
        imageUrl: String,
        name: String,
        releaseDate: String,
        type: String,
        uri: String
    ) : this() {
        this.albumType = albumType
        this.artist = artist
        this.artistId = artistId
        this.artists = artists
        this.id = id
        this.imageUrl = imageUrl
        this.name = name
        this.releaseDate = releaseDate
        this.type = type
        this.uri = uri
    }

    constructor(parcel: Parcel) : this() {
        albumType = parcel.readString()
        artist = parcel.readString()
        artistId = parcel.readString()
        artists = parcel.createTypedArrayList(ArtistSimple.CREATOR)
        id = parcel.readString()
        imageUrl = parcel.readString()
        name = parcel.readString()
        releaseDate = parcel.readString()
        type = parcel.readString()
        uri = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(albumType)
        parcel.writeString(artist)
        parcel.writeString(artistId)
        parcel.writeTypedList(artists)
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(releaseDate)
        parcel.writeString(type)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ArtistAlbumParcelable> {
            override fun createFromParcel(parcel: Parcel): ArtistAlbumParcelable {
                return ArtistAlbumParcelable(parcel)
            }

            override fun newArray(size: Int): Array<ArtistAlbumParcelable?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        val empty = ArtistAlbumParcelable(
            albumType = "",
            artist = "",
            artistId = "",
            artists = listOf(),
            id = "",
            imageUrl = "",
            name = "",
            releaseDate = "",
            type = "",
            uri = ""
        )
    }
}