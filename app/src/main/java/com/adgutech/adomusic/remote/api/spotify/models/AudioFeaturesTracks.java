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

public class AudioFeaturesTracks implements Parcelable {

    public static final Creator<AudioFeaturesTracks> CREATOR = new Creator<AudioFeaturesTracks>() {
        @Override
        public AudioFeaturesTracks createFromParcel(Parcel in) {
            return new AudioFeaturesTracks(in);
        }

        @Override
        public AudioFeaturesTracks[] newArray(int size) {
            return new AudioFeaturesTracks[size];
        }
    };
    public List<AudioFeaturesTrack> audio_features;

    public AudioFeaturesTracks() {
    }

    protected AudioFeaturesTracks(Parcel in) {
        audio_features = in.createTypedArrayList(AudioFeaturesTrack.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedList(audio_features);
    }
}
