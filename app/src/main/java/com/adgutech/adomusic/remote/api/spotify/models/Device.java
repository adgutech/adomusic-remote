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

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Device implements Parcelable {
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    public String id;
    public boolean is_active;
    public boolean is_private_session;
    public boolean is_restricted;
    public String name;
    public String type;
    public int volume_percent;
    public boolean supports_volume;

    public Device() {
    }

    protected Device(Parcel in) {
        id = in.readString();
        is_active = readBooleanVersionQ(in);
        is_private_session = readBooleanVersionQ(in);
        is_restricted = readBooleanVersionQ(in);
        name = in.readString();
        type = in.readString();
        volume_percent = in.readInt();
        supports_volume = readBooleanVersionQ(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        writeBooleanVersionQ(dest, is_active);
        writeBooleanVersionQ(dest, is_private_session);
        writeBooleanVersionQ(dest, is_restricted);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(volume_percent);
        writeBooleanVersionQ(dest, supports_volume);
    }

    private void writeBooleanVersionQ(Parcel dest, boolean b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(b);
        } else {
            dest.writeInt(b ? 1 : 0);
        }
    }

    private boolean readBooleanVersionQ(Parcel in) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return in.readBoolean();
        }
        return in.readInt() != 0;
    }
}
