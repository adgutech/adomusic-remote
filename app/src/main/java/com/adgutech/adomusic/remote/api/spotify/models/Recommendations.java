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

import java.util.List;

public class Recommendations implements Parcelable {

    public static final Creator<Recommendations> CREATOR = new Creator<Recommendations>() {
        @Override
        public Recommendations createFromParcel(Parcel in) {
            return new Recommendations(in);
        }

        @Override
        public Recommendations[] newArray(int size) {
            return new Recommendations[size];
        }
    };
    public List<Seed> seeds;
    public List<Track> tracks;

    public Recommendations() {
    }

    protected Recommendations(Parcel in) {
        seeds = in.createTypedArrayList(Seed.CREATOR);
        tracks = in.createTypedArrayList(Track.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(seeds);
        dest.writeTypedList(tracks);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
