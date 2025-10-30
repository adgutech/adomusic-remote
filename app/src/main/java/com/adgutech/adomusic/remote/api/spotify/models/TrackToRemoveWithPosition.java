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

import java.util.ArrayList;
import java.util.List;

public class TrackToRemoveWithPosition implements Parcelable {
    public static final Creator<TrackToRemoveWithPosition> CREATOR = new Creator<TrackToRemoveWithPosition>() {
        public TrackToRemoveWithPosition createFromParcel(Parcel source) {
            return new TrackToRemoveWithPosition(source);
        }

        public TrackToRemoveWithPosition[] newArray(int size) {
            return new TrackToRemoveWithPosition[size];
        }
    };
    public String uri;
    public List<Integer> positions;

    public TrackToRemoveWithPosition() {
    }

    protected TrackToRemoveWithPosition(Parcel in) {
        this.uri = in.readString();
        this.positions = new ArrayList<Integer>();
        in.readList(this.positions, List.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
        dest.writeList(this.positions);
    }
}
