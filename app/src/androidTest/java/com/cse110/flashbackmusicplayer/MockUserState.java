package com.cse110.flashbackmusicplayer;

import android.location.Location;

public class MockUserState extends UserState {
    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location("gps");
    String place = "";
    TimeSegment timeSegment = TimeSegment.MORNING;
    int dayOfWeek = 1;
    String date = "";
    String time = "";
    long systemTime = 0;

    public MockUserState() {}

    public Location getLocation() { return new Location(location); }
    public String getPlace() { return place; }
    public TimeSegment getTimeSegment() { return timeSegment; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public long getSystemTime() {
        return systemTime;
    }

    public void setLocation(Location location) { this.location = location; }
    public void setPlace(String place) { this.place = place; }
    public void setTimeSegment(TimeSegment timeSegment) { this.timeSegment = timeSegment; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setSystemTime(long systemTime) { this.systemTime = systemTime; }
}
