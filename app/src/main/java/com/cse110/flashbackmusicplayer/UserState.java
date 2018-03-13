package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.Calendar;

public interface UserState {

    UserState snapshot();

    void locationUpdated(double latitude, double longitude, String place);

    Location getLocation();
    String getUser();
    int getDayOfYear();

    long getSystemTime();
}
