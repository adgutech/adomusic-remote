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

/**
 * Created by Adolfo Gutierrez on 03/14/2025.
 */

class AlbumParcelable() : Parcelable {

    var addedAt: String? = null
    var albumType: String? = null
    var artist: String? = null
    var artistId: String? = null
    var artists: List<ArtistSimple>? = null
    var copyrights: String? = null
    var id: String? = null
    var imageUrl: String? = null
    var name: String? = null
    var releaseDate: String? = null
    var releaseDatePrecision: String? = null
    var trackTotal: Int = 0
    var type: String? = null
    var uri: String? = null

    constructor(
        addedAt: String,
        albumType: String,
        artist: String,
        artistId: String,
        artists: List<ArtistSimple>,
        copyrights: String,
        id: String,
        imageUrl: String,
        name: String,
        releaseDate: String,
        releaseDatePrecision: String,
        trackTotal: Int,
        type: String,
        uri: String
    ) : this() {
        this.addedAt = addedAt
        this.albumType = albumType
        this.artist = artist
        this.artistId = artistId
        this.artists = artists
        this.copyrights = copyrights
        this.id = id
        this.imageUrl = imageUrl
        this.name = name
        this.releaseDate = releaseDate
        this.releaseDatePrecision = releaseDatePrecision
        this.trackTotal = trackTotal
        this.type = type
        this.uri = uri
    }

    constructor(parcel: Parcel) : this() {
        addedAt = parcel.readString()
        albumType = parcel.readString()
        artist = parcel.readString()
        artistId = parcel.readString()
        artists = parcel.createTypedArrayList(ArtistSimple.CREATOR)
        copyrights = parcel.readString()
        id = parcel.readString()
        imageUrl = parcel.readString()
        name = parcel.readString()
        releaseDate = parcel.readString()
        releaseDatePrecision = parcel.readString()
        trackTotal = parcel.readInt()
        type = parcel.readString()
        uri = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addedAt)
        parcel.writeString(albumType)
        parcel.writeString(artist)
        parcel.writeString(artistId)
        parcel.writeTypedList(artists)
        parcel.writeString(copyrights)
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(releaseDate)
        parcel.writeString(releaseDatePrecision)
        parcel.writeInt(trackTotal)
        parcel.writeString(type)
        parcel.writeString(uri)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<AlbumParcelable> {
            override fun createFromParcel(parcel: Parcel): AlbumParcelable {
                return AlbumParcelable(parcel)
            }

            override fun newArray(size: Int): Array<AlbumParcelable?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        val empty = AlbumParcelable(
            addedAt = "",
            albumType = "",
            artist = "",
            artistId = "",
            artists = listOf(),
            copyrights = "",
            id = "",
            imageUrl = "",
            name = "",
            releaseDate = "",
            releaseDatePrecision = "",
            trackTotal = -1,
            type = "",
            uri = ""
        )
    }
}