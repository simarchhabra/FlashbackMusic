package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;

import java.util.Calendar;

public class UserStateImpl implements UserState {

    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location(LocationManager.GPS_PROVIDER);
    String place = "";

    public UserStateImpl() {}

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
        int month = calendar.get(Calendar.MONTH) + 1; // January is 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return "" + month + "/" + day + "/" + year;
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR);
        String hours_string = String.valueOf(hours);
        if (hours / 10 == 0) hours_string = "0" + hours_string;

        int minutes = calendar.get(Calendar.MINUTE);
        String minutes_string = String.valueOf(minutes);
        if (minutes / 10 == 0) minutes_string = "0" + minutes_string;

        int am_pm = calendar.get(Calendar.AM_PM);
        String am_pm_string = am_pm == Calendar.AM ? "am" : "pm";

        return hours_string + ":" + minutes_string + " " + am_pm_string;
    }

    public long getSystemTime() {
        return System.currentTimeMillis();
    }

}
