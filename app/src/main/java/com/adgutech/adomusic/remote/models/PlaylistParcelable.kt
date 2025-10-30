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
import com.adgutech.adomusic.remote.api.spotify.models.Image
import com.adgutech.commons.extensions.readBooleanVersionQ
import com.adgutech.commons.extensions.writeBooleanVersionQ

class PlaylistParcelable() : Parcelable {

    var isCollaborative: Boolean = false
    var id: String? = null
    var userId: String? = null
    var uri: String? = null
    var title: String? = null
    var displayName: String? = null
    var images: List<Image>? = null
    var link: String? = null
    var trackTotal: Int = -1
    var isPublic: Boolean = false
    var snapshotId: String? = null
    var type: String? = null

    constructor(
        isCollaborative: Boolean,
        id: String,
        userId: String,
        uri: String,
        title: String,
        displayName: String,
        images: List<Image>,
        link: String?,
        trackTotal: Int,
        isPublic: Boolean,
        snapshotId: String,
        type: String
    ) : this() {
        this.isCollaborative = isCollaborative
        this.id = id
        this.userId = userId
        this.uri = uri
        this.title = title
        this.displayName = displayName
        this.images = images
        this.link = link
        this.trackTotal = trackTotal
        this.isPublic = isPublic
        this.snapshotId = snapshotId
        this.type = type
    }

    constructor(parcel: Parcel) : this() {
        isCollaborative = parcel.readByte() != 0.toByte()
        id = parcel.readString()
        userId = parcel.readString()
        uri = parcel.readString()
        title = parcel.readString()
        displayName = parcel.readString()
        images =
            parcel.createTypedArrayList(Image.CREATOR)
        trackTotal = parcel.readInt()
        isPublic = parcel.readBooleanVersionQ()
        snapshotId = parcel.readString()
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isCollaborative) 1 else 0)
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(uri)
        parcel.writeString(title)
        parcel.writeTypedList(images)
        parcel.writeInt(trackTotal)
        parcel.writeBooleanVersionQ(isPublic)
        parcel.writeString(snapshotId)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<PlaylistParcelable> {
            override fun createFromParcel(parcel: Parcel): PlaylistParcelable {
                return PlaylistParcelable(parcel)
            }

            override fun newArray(size: Int): Array<PlaylistParcelable?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        val empty = PlaylistParcelable(
            isCollaborative = false,
            id = "",
            userId = "",
            uri = "",
            title = "",
            displayName = "",
            images = listOf(),
            link = "",
            trackTotal = -1,
            isPublic = false,
            snapshotId = "",
            type = ""
        )
    }
}