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

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#playlist-object-simplified">Playlist object model (simplified)</a>
 */
public class PlaylistSimple extends PlaylistBase {
    public static final Creator<PlaylistSimple> CREATOR = new Creator<PlaylistSimple>() {
        public PlaylistSimple createFromParcel(Parcel source) {
            return new PlaylistSimple(source);
        }

        public PlaylistSimple[] newArray(int size) {
            return new PlaylistSimple[size];
        }
    };
    public PlaylistTracksInformation tracks;

    public PlaylistSimple() {
    }

    protected PlaylistSimple(Parcel in) {
        super(in);
        this.tracks = in.readParcelable(PlaylistTracksInformation.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.tracks, flags);
    }
}
