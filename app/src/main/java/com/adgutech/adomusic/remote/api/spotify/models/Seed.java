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

public class Seed implements Parcelable {

    public static final Creator<Seed> CREATOR = new Creator<Seed>() {
        @Override
        public Seed createFromParcel(Parcel in) {
            return new Seed(in);
        }

        @Override
        public Seed[] newArray(int size) {
            return new Seed[size];
        }
    };
    public int afterFilteringSize;
    public int afterRelinkingSize;
    public String href;
    public String id;
    public int initialPoolSize;
    public String type;

    public Seed() {
    }

    protected Seed(Parcel in) {
        afterFilteringSize = in.readInt();
        afterRelinkingSize = in.readInt();
        href = in.readString();
        id = in.readString();
        initialPoolSize = in.readInt();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(afterFilteringSize);
        dest.writeInt(afterRelinkingSize);
        dest.writeString(href);
        dest.writeString(id);
        dest.writeInt(initialPoolSize);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
