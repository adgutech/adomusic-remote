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

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#cursor-based-paging-object">Cursor-based paging object model</a>
 *
 * @param <T> expected object that is paged
 */
public class CursorPager<T> implements Parcelable {
    public static final Creator<CursorPager> CREATOR = new Creator<CursorPager>() {
        public CursorPager createFromParcel(Parcel source) {
            return new CursorPager(source);
        }

        public CursorPager[] newArray(int size) {
            return new CursorPager[size];
        }
    };
    public String href;
    public List<T> items;
    public int limit;
    public String next;
    public Cursor cursors;
    public int total;

    public CursorPager() {
    }

    protected CursorPager(Parcel in) {
        this.href = in.readString();
        this.items = in.readArrayList(ArrayList.class.getClassLoader());
        this.limit = in.readInt();
        this.next = in.readString();
        this.cursors = in.readParcelable(Cursor.class.getClassLoader());
        this.total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(href);
        dest.writeList(items);
        dest.writeInt(limit);
        dest.writeString(next);
        dest.writeParcelable(this.cursors, flags);
        dest.writeInt(total);
    }

}
