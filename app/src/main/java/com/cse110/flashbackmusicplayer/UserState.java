package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.Calendar;

public interface UserState {

    public UserState snapshot();

    public void locationUpdated(double latitude, double longitude, String place);

    public Location getLocation();

    public String getPlace();

    public TimeSegment getTimeSegment();

    public int getDayOfWeek();

    public String getDate();

    public String getTime();

    public long getSystemTime();
}
