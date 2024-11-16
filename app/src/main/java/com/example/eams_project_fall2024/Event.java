package com.example.eams_project_fall2024;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Parcelable {
    private String eventName;
    private Date eventDate; // Date of the event
    private Date startTime;
    private Date endTime;
    private List<Attendee> approvedAttendees;

    public Event(String eventName, Date startTime, Date endTime) {
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.approvedAttendees = new ArrayList<>(); // Initialize with an empty list
    }

    protected Event(Parcel in) {
        eventName = in.readString();
        eventDate = new Date(in.readLong());
        startTime = new Date(in.readLong());
        endTime = new Date(in.readLong());
        approvedAttendees = in.createTypedArrayList(Attendee.CREATOR);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(String title, Date eventDate, Date startTime, Date endTime) {
    }

    public String getEventName() {
        return eventName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public List<Attendee> getApprovedAttendees() {
        return approvedAttendees;
    }

    public void setApprovedAttendees(List<Attendee> approvedAttendees) {
        this.approvedAttendees = approvedAttendees;
    }

    // Setter methods to allow modifying event date and times
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    // Method to check if the event can be deleted
    public boolean canBeDeleted() {
        return approvedAttendees == null || approvedAttendees.isEmpty();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeLong(eventDate != null ? eventDate.getTime() : 0);
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime.getTime());
        dest.writeTypedList(approvedAttendees);
    }
}
