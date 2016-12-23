package org.asbjorjo.splittimer.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.asbjorjo.splittimer.SplitTimerConstants.EVENT_TYPE;

import java.util.Objects;

/**
 * Created by AJohansen2 on 12/23/2016.
 */

public class Event implements Parcelable {
    private long id;
    private String name;
    private long time;
    private EVENT_TYPE type;

    public Event(long id, String name, long time, EVENT_TYPE type) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.type = type;
    }

    public Event(long id, String name, long time, String type) {
        new Event(id, name, time, EVENT_TYPE.valueOf(type));
    }

    private Event(Parcel in) {
        id = in.readLong();
        name = in.readString();
        time = in.readLong();
        type = EVENT_TYPE.valueOf(in.readString());
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public EVENT_TYPE getType() {
        return type;
    }

    public void setType(EVENT_TYPE type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(time);
        dest.writeString(type.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                time == event.time &&
                Objects.equals(name, event.name) &&
                Objects.equals(type, event.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, time, type);
    }
}