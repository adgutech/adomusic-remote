package com.adgutech.adomusic.remote.api.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class TrackPosition implements Parcelable {

    @SerializedName("position")
    public int position;

    public TrackPosition() {
    }

    protected TrackPosition(Parcel in) {
        position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(position);
    }

    public static final Creator<TrackPosition> CREATOR = new Creator<TrackPosition>() {
        @Override
        public TrackPosition createFromParcel(Parcel in) {
            return new TrackPosition(in);
        }

        @Override
        public TrackPosition[] newArray(int size) {
            return new TrackPosition[size];
        }
    };
}
