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

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#album-object-full">Album object model</a>
 */
public class Album extends AlbumSimple implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    public List<ArtistSimple> artists;
    public List<Copyright> copyrights;
    public Map<String, String> external_ids;
    public List<String> genres;
    public Integer popularity;
    public String release_date;
    public String release_date_precision;
    public Pager<TrackSimple> tracks;

    public Album() {
    }

    protected Album(Parcel in) {
        super(in);
        this.artists = in.createTypedArrayList(ArtistSimple.CREATOR);
        this.copyrights = in.createTypedArrayList(Copyright.CREATOR);
        this.external_ids = in.readHashMap(ClassLoader.getSystemClassLoader());
        this.genres = in.createStringArrayList();
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.release_date = in.readString();
        this.release_date_precision = in.readString();
        this.tracks = in.readParcelable(Pager.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(artists);
        dest.writeTypedList(copyrights);
        dest.writeMap(this.external_ids);
        dest.writeStringList(this.genres);
        dest.writeValue(this.popularity);
        dest.writeString(this.release_date);
        dest.writeString(this.release_date_precision);
        dest.writeParcelable(this.tracks, flags);
    }
}