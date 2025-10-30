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

import androidx.annotation.NonNull;

import java.util.List;

/**
 * <a href="https://developer.spotify.com/documentation/web-api/reference/get-queue">Playlist track object model</a>
 */
public class QueueTrack implements Parcelable {
    public static final Creator<QueueTrack> CREATOR = new Creator<QueueTrack>() {
        @Override
        public QueueTrack createFromParcel(Parcel in) {
            return new QueueTrack(in);
        }

        @Override
        public QueueTrack[] newArray(int size) {
            return new QueueTrack[size];
        }
    };
    public Track currently_playing;
    public List<Track> queue;

    public QueueTrack() {
    }

    protected QueueTrack(Parcel in) {
        this.currently_playing = in.readParcelable(Track.class.getClassLoader());
        this.queue = in.createTypedArrayList(Track.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(this.currently_playing, 0);
        dest.writeTypedList(this.queue);
    }
}