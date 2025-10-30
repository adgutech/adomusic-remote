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

class ArtistParcelable() : Parcelable {

    var id: String? = null
    var name: String? = null
    var imageUrl: String? = null
    var followers: Int = 0
    var popularity: Int = 0

    constructor(
        id: String,
        name: String,
        imageUrl: String,
        followers: Int,
        popularity: Int
    ) : this() {
        this.id = id
        this.name = name
        this.imageUrl = imageUrl
        this.followers = followers
        this.popularity = popularity
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        imageUrl = parcel.readString()
        followers = parcel.readInt()
        popularity = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeInt(followers)
        parcel.writeInt(popularity)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ArtistParcelable> {
            override fun createFromParcel(parcel: Parcel): ArtistParcelable {
                return ArtistParcelable(parcel)
            }

            override fun newArray(size: Int): Array<ArtistParcelable?> {
                return arrayOfNulls(size)
            }
        }

        @JvmField
        val empty = ArtistParcelable(
            id = "",
            name = "",
            imageUrl = "",
            followers = 0,
            popularity = 0
        )
    }
}