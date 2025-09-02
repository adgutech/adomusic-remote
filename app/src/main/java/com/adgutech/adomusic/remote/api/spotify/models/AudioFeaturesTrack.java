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

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#audio-features-object">Audio Features Object</a>
 */
public class AudioFeaturesTrack implements Parcelable {

    public static final Creator<AudioFeaturesTrack> CREATOR = new Creator<AudioFeaturesTrack>() {
        @Override
        public AudioFeaturesTrack createFromParcel(Parcel in) {
            return new AudioFeaturesTrack(in);
        }

        @Override
        public AudioFeaturesTrack[] newArray(int size) {
            return new AudioFeaturesTrack[size];
        }
    };
    public float acousticness;
    public String analysis_url;
    public float danceability;
    public int duration_ms;
    public float energy;
    public String id;
    public float instrumentalness;
    public int key;
    public float liveness;
    public float loudness;
    public int mode;
    public float speechiness;
    public float tempo;
    public int time_signature;
    public String track_href;
    public String type;
    public String uri;
    public float valence;

    public AudioFeaturesTrack() {
    }

    protected AudioFeaturesTrack(Parcel in) {
        acousticness = in.readFloat();
        analysis_url = in.readString();
        danceability = in.readFloat();
        duration_ms = in.readInt();
        energy = in.readFloat();
        id = in.readString();
        instrumentalness = in.readFloat();
        key = in.readInt();
        liveness = in.readFloat();
        loudness = in.readFloat();
        mode = in.readInt();
        speechiness = in.readFloat();
        tempo = in.readFloat();
        time_signature = in.readInt();
        track_href = in.readString();
        type = in.readString();
        uri = in.readString();
        valence = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(acousticness);
        dest.writeString(analysis_url);
        dest.writeFloat(danceability);
        dest.writeInt(duration_ms);
        dest.writeFloat(energy);
        dest.writeString(id);
        dest.writeFloat(instrumentalness);
        dest.writeInt(key);
        dest.writeFloat(liveness);
        dest.writeFloat(loudness);
        dest.writeInt(mode);
        dest.writeFloat(speechiness);
        dest.writeFloat(tempo);
        dest.writeInt(time_signature);
        dest.writeString(track_href);
        dest.writeString(type);
        dest.writeString(uri);
        dest.writeFloat(valence);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
