package com.ruddell.museumofthebible.Exhibits.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/27/16.
 */

public class Exhibit implements Parcelable {
    public int id;
    public String title;
    public String audioFile;
    public String imageName;
    public String description;

    public Exhibit(final int id, final String title, final String audioFile, final String imageName, final String description) {
        this.id = id;
        this.title = title;
        this.audioFile = audioFile;
        this.imageName = imageName;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Exhibit{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", audioFile='" + audioFile + '\'' +
                ", imageName='" + imageName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.audioFile);
        dest.writeString(this.imageName);
        dest.writeString(this.description);
    }

    protected Exhibit(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.audioFile = in.readString();
        this.imageName = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Exhibit> CREATOR = new Parcelable.Creator<Exhibit>() {
        @Override
        public Exhibit createFromParcel(Parcel source) {
            return new Exhibit(source);
        }

        @Override
        public Exhibit[] newArray(int size) {
            return new Exhibit[size];
        }
    };
}
