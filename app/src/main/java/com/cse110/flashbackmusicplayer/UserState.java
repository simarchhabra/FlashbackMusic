package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.Calendar;

public class UserState {

    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location("gps");
    String place = "";

    public UserState() {}

    public void locationUpdated(double latitude, double longitude, String place) {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.place = place;
    }

    public Location getLocation() {
        return new Location(location);
    }

    public String getPlace() {
        return place;
    }

    public TimeSegment getTimeSegment() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.HOUR_OF_DAY);

        // Get the current time segment.
        if (day >= 5 && day < 11) {
            return TimeSegment.MORNING;
        }
        else if (day >= 11 && day < 17) {
            return TimeSegment.NOON;
        }
        else {
            return TimeSegment.EVENING;
        }
    }

    public int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return "" + month + "/" + day + "/" + year;
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return "" + hours + ":" + minutes + ":" + seconds;
    }

    public long getSystemTime() {
        return System.currentTimeMillis();
    }
}
