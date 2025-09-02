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

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#playlist-track-object">Playlist track object model</a>
 */
public class PlaylistTrack implements Parcelable {
    public static final Creator<PlaylistTrack> CREATOR = new Creator<PlaylistTrack>() {
        public PlaylistTrack createFromParcel(Parcel source) {
            return new PlaylistTrack(source);
        }

        public PlaylistTrack[] newArray(int size) {
            return new PlaylistTrack[size];
        }
    };
    public String added_at;
    public UserPublic added_by;
    public Track track;
    public Boolean is_local;

    public PlaylistTrack() {
    }

    protected PlaylistTrack(Parcel in) {
        this.added_at = in.readString();
        this.added_by = in.readParcelable(UserPublic.class.getClassLoader());
        this.track = in.readParcelable(Track.class.getClassLoader());
        this.is_local = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.added_at);
        dest.writeParcelable(this.added_by, flags);
        dest.writeParcelable(this.track, 0);
        dest.writeValue(this.is_local);
    }
}
