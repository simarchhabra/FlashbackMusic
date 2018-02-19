package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.Calendar;

public class UserStateImpl implements UserState {

    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location(LocationManager.GPS_PROVIDER);
    String place = "";

    public UserStateImpl() {}

    @Override
    public UserState snapshot() {
        return new UserStateSnapshot(this);
    }

    public void locationUpdated(double latitude, double longitude, String place) {
        Log.d("UserStateImpl", "Set the latitude and longitude to " + latitude + " " + longitude);
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
            Log.d("UserStateImpl", "Returned Morning. The hour is: " + day);
            return TimeSegment.MORNING;
        }
        else if (day >= 11 && day < 17) {
            Log.d("UserStateImpl", "Returned Noon. The hour is: " + day);
            return TimeSegment.NOON;
        }
        else {
            Log.d("UserStateImpl", "Returned Evening. The hour is: " + day);
            return TimeSegment.EVENING;
        }
    }

    public int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("UserStateImpl", "The day of week is " + day_of_week);
        return day_of_week;
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // January is 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = "" + month + "/" + day + "/" + year;
        Log.d("UserStateImpl", "The date is " + date);
        return date;
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

        String time = hours_string + ":" + minutes_string + " " + am_pm_string;
        Log.d("UserStateImpl", "The time is " + time);
        return time;
    }

    public long getSystemTime() {
        return System.currentTimeMillis();
    }

}
