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
 * <a href="https://developer.spotify.com/web-api/object-model/#user-object-private">User object (private) model</a>
 */
public class UserPrivate extends UserPublic {
    public static final Creator<UserPrivate> CREATOR = new Creator<UserPrivate>() {
        public UserPrivate createFromParcel(Parcel source) {
            return new UserPrivate(source);
        }

        public UserPrivate[] newArray(int size) {
            return new UserPrivate[size];
        }
    };
    public String birthdate;
    public String country;
    public String email;
    public String product;

    public UserPrivate() {
    }

    protected UserPrivate(Parcel in) {
        super(in);
        this.birthdate = in.readString();
        this.country = in.readString();
        this.email = in.readString();
        this.product = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.birthdate);
        dest.writeString(this.country);
        dest.writeString(this.email);
        dest.writeString(this.product);
    }
}
