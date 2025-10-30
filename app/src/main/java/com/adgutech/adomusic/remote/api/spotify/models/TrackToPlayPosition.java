package com.adgutech.adomusic.remote.api.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackToPlayPosition implements Parcelable {

    @SerializedName("context_uri")
    public String contextUri;
    @SerializedName("uris")
    public List<String> uris;
    public TrackPosition offset;

    public TrackToPlayPosition() {
    }

    protected TrackToPlayPosition(Parcel in) {
        contextUri = in.readString();
        uris = in.createStringArrayList();
        offset = in.readParcelable(TrackPosition.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(contextUri);
        dest.writeStringList(uris);
        dest.writeParcelable(offset, 0);
    }

    public static final Creator<TrackToPlayPosition> CREATOR = new Creator<TrackToPlayPosition>() {
        @Override
        public TrackToPlayPosition createFromParcel(Parcel in) {
            return new TrackToPlayPosition(in);
        }

        @Override
        public TrackToPlayPosition[] newArray(int size) {
            return new TrackToPlayPosition[size];
        }
    };
}
