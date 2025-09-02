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
import java.util.Map;

public class AlbumSimple implements Parcelable {
    public static final Creator<AlbumSimple> CREATOR = new Creator<AlbumSimple>() {
        public AlbumSimple createFromParcel(Parcel source) {
            return new AlbumSimple(source);
        }

        public AlbumSimple[] newArray(int size) {
            return new AlbumSimple[size];
        }
    };
    public String album_type;
    public List<String> available_markets;
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public List<Image> images;
    public String name;
    public String type;
    public String uri;

    public AlbumSimple() {
    }

    protected AlbumSimple(Parcel in) {
        this.album_type = in.readString();
        this.available_markets = in.createStringArrayList();
        this.external_urls = in.readHashMap(ClassLoader.getSystemClassLoader());
        this.href = in.readString();
        this.id = in.readString();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.album_type);
        dest.writeStringList(this.available_markets);
        dest.writeMap(this.external_urls);
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeTypedList(images);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }
}
