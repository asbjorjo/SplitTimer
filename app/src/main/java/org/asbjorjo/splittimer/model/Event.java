package org.asbjorjo.splittimer.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.asbjorjo.splittimer.SplitTimerConstants.EVENT_TYPE;

import java.util.Objects;

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * Created by AJohansen2 on 12/23/2016.
 */

public class Event implements Parcelable {
    private long id;
    private String name;
    private long time;
    private EVENT_TYPE type;

    public Event() {
        this.id = NO_ACTIVE_EVENT;
    }

    public Event(long id, String name, long time, EVENT_TYPE type) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.type = type;
    }

    public Event(long id, String name, long time, String type) {
        this(id, name, time, EVENT_TYPE.valueOf(type));
    }

    private Event(Parcel in) {
        id = in.readLong();
        name = in.readString();
        time = in.readLong();
        String typeString = in.readString();
        type = typeString != null ? EVENT_TYPE.valueOf(typeString) : null;
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
        dest.writeString(type != null ? type.toString() : null);
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", time=").append(time);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}