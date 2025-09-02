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

public class SeedsGenres implements Parcelable {

    public static final Creator<SeedsGenres> CREATOR = new Creator<SeedsGenres>() {
        @Override
        public SeedsGenres createFromParcel(Parcel in) {
            return new SeedsGenres(in);
        }

        @Override
        public SeedsGenres[] newArray(int size) {
            return new SeedsGenres[size];
        }
    };
    public List<String> genres;

    public SeedsGenres() {
    }

    protected SeedsGenres(Parcel in) {
        genres = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(genres);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
