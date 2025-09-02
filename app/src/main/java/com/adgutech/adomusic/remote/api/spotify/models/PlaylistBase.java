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

import java.util.List;
import java.util.Map;

/**
 * Base class for {@link Playlist} and
 * {@link PlaylistSimple}
 */
public abstract class PlaylistBase implements Parcelable {
    public Boolean collaborative;
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public List<Image> images;
    public String name;
    public UserPublic owner;
    @SerializedName("public")
    public Boolean is_public;
    public String snapshot_id;
    public String type;
    public String uri;

    protected PlaylistBase() {
    }

    protected PlaylistBase(Parcel in) {
        this.collaborative = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.href = (String) in.readValue(String.class.getClassLoader());
        this.id = (String) in.readValue(String.class.getClassLoader());
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = (String) in.readValue(String.class.getClassLoader());
        this.owner = in.readParcelable(UserPublic.class.getClassLoader());
        this.is_public = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.snapshot_id = (String) in.readValue(String.class.getClassLoader());
        this.type = (String) in.readValue(String.class.getClassLoader());
        this.uri = (String) in.readValue(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.collaborative);
        dest.writeMap(this.external_urls);
        dest.writeValue(this.href);
        dest.writeValue(this.id);
        dest.writeTypedList(this.images);
        dest.writeValue(this.name);
        dest.writeParcelable(owner, flags);
        dest.writeValue(is_public);
        dest.writeValue(snapshot_id);
        dest.writeValue(type);
        dest.writeValue(uri);
    }
}
