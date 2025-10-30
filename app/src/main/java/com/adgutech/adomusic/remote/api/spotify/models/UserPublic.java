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
 * <a href="https://developer.spotify.com/web-api/object-model/#user-object-public">User object (public) model</a>
 */
public class UserPublic implements Parcelable {
    public static final Creator<UserPublic> CREATOR = new Creator<UserPublic>() {
        public UserPublic createFromParcel(Parcel source) {
            return new UserPublic(source);
        }

        public UserPublic[] newArray(int size) {
            return new UserPublic[size];
        }
    };
    public String display_name;
    public Map<String, String> external_urls;
    public Followers followers;
    public String href;
    public String id;
    public List<Image> images;
    public String type;
    public String uri;

    public UserPublic() {
    }

    protected UserPublic(Parcel in) {
        this.display_name = in.readString();
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.followers = in.readParcelable(Followers.class.getClassLoader());
        this.href = in.readString();
        this.id = in.readString();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.display_name);
        dest.writeMap(this.external_urls);
        dest.writeParcelable(this.followers, 0);
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeTypedList(images);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }
}
