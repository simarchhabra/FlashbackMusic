package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class UserStateSnapshot implements UserState {

    private Location location;
    private String place;
    private TimeSegment timeSegment;
    private int dayOfWeek;
    private int weekOfYear;
    private String date;
    private String time;
    private long systemTime;
    private String user;

    public UserStateSnapshot(UserState state) {
        Log.d("UserStateSnapshot", "Snapshot created at " + state.getSystemTime());
        this.location = new Location(state.getLocation());
        this.place = state.getPlace();
        this.timeSegment = state.getTimeSegment();
        this.dayOfWeek = state.getDayOfWeek();
        this.weekOfYear = state.getWeekOfYear();
        this.date = state.getDate();
        this.time = state.getTime();
        this.systemTime = state.getSystemTime();
        this.user = state.getUser();
    }

    @Override
    public UserState snapshot() {
        return new UserStateSnapshot(this);
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
    public int getWeekOfYear() {
        return weekOfYear;
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

    @Override
    public String getUser() {
        return user;
    }
}
