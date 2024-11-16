package com.example.eams_project_fall2024;

import android.os.Parcel;
import android.os.Parcelable;

public class Attendee implements Parcelable {
    private String name;
    private String status;

    public Attendee(String name) {
        this.name = name;
        this.status = "pending"; // default status
    }

    protected Attendee(Parcel in) {
        name = in.readString();
        status = in.readString();
    }

    public static final Creator<Attendee> CREATOR = new Creator<Attendee>() {
        @Override
        public Attendee createFromParcel(Parcel in) {
            return new Attendee(in);
        }

        @Override
        public Attendee[] newArray(int size) {
            return new Attendee[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(status);
    }
}
