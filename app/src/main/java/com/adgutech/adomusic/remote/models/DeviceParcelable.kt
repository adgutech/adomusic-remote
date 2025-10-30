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
import com.adgutech.commons.extensions.readBooleanVersionQ
import com.adgutech.commons.extensions.writeBooleanVersionQ

class DeviceParcelable() : Parcelable {

    var id: String? = null
    var isActive: Boolean = false
    var isPrivateSession: Boolean = false
    var isRestricted: Boolean = false
    var name: String? = null
    var type: String? = null
    var volumePercent: Int = 0
    var supportsVolume: Boolean = false

    constructor(
        id: String,
        isActive: Boolean,
        isPrivateSession: Boolean,
        isRestricted: Boolean,
        name: String,
        type: String,
        volumePercent: Int,
        supportsVolume: Boolean
    ) : this() {
        this.id = id
        this.isActive = isActive
        this.isPrivateSession = isPrivateSession
        this.isRestricted = isRestricted
        this.name = name
        this.type = type
        this.volumePercent = volumePercent
        this.supportsVolume = supportsVolume
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        isActive = parcel.readBooleanVersionQ()
        isPrivateSession = parcel.readBooleanVersionQ()
        isRestricted = parcel.readBooleanVersionQ()
        name = parcel.readString()
        type = parcel.readString()
        volumePercent = parcel.readInt()
        supportsVolume = parcel.readBooleanVersionQ()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeBooleanVersionQ(isActive)
        parcel.writeBooleanVersionQ(isPrivateSession)
        parcel.writeBooleanVersionQ(isRestricted)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeInt(volumePercent)
        parcel.writeBooleanVersionQ(supportsVolume)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeviceParcelable> {
        override fun createFromParcel(parcel: Parcel): DeviceParcelable {
            return DeviceParcelable(parcel)
        }

        override fun newArray(size: Int): Array<DeviceParcelable?> {
            return arrayOfNulls(size)
        }
    }
}