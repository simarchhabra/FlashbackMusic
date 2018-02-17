package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.Calendar;

public interface UserState {

    // All of the information associated with the user at the given moment.
    static UserStateImpl userState = new UserStateImpl();

    public static UserState getInstance() {
        return userState;
    }

    public static UserState snapshot() {
        return new UserStateSnapshot(userState);
    }

    public void locationUpdated(double latitude, double longitude, String place);

    public Location getLocation();

    public String getPlace();

    public TimeSegment getTimeSegment();

    public int getDayOfWeek();

    public String getDate();

    public String getTime();

    public long getSystemTime();
}
