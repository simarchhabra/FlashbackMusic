package com.cse110.flashbackmusicplayer;

public class UserState {

    // The current up-to-date position of the user.
    double latitude = 0.0;
    double longitude = 0.0;

    public UserState() {}

    public void locationUpdated(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
