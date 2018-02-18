package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;

public class UserStateSnapshot implements UserState {

    private Location location;
    private String place;
    private TimeSegment timeSegment;
    private int dayOfWeek;
    private String date;
    private String time;
    private long systemTime;

    public UserStateSnapshot(UserState state) {
        this.location = state.getLocation();
        this.place = state.getPlace();
        this.timeSegment = state.getTimeSegment();
        this.dayOfWeek = state.getDayOfWeek();
        this.date = state.getDate();
        this.time = state.getTime();
        this.systemTime = state.getSystemTime();
    }

    @Override
    public void locationUpdated(double latitude, double longitude, String place) {
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.place = place;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getPlace() {
        return place;
    }

    @Override
    public TimeSegment getTimeSegment() {
        return timeSegment;
    }

    @Override
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public long getSystemTime() {
        return systemTime;
    }
}
