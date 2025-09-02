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

package com.adgutech.adomusic.remote.api.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PlaylistFollowPrivacy implements Parcelable {
    public static final Creator<PlaylistFollowPrivacy> CREATOR = new Creator<PlaylistFollowPrivacy>() {
        public PlaylistFollowPrivacy createFromParcel(Parcel source) {
            return new PlaylistFollowPrivacy(source);
        }

        public PlaylistFollowPrivacy[] newArray(int size) {
            return new PlaylistFollowPrivacy[size];
        }
    };
    @SerializedName("public")
    public Boolean is_public;

    public PlaylistFollowPrivacy() {
    }

    protected PlaylistFollowPrivacy(Parcel in) {
        this.is_public = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.is_public);
    }
}
