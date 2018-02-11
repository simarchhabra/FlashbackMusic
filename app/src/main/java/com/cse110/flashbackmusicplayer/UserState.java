package com.cse110.flashbackmusicplayer;

public class UserState {
    
    enum Time
    {
        MORNING, AFTERNOON, NIGHT
    }
    
    enum DAY
    {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, 
        SATURDAY
    }

    // Current Time of hour and Day of the week for user
    Time time; 
    Day day;

    // The current up-to-date position of the user.
    double latitude = 0.0;
    double longitude = 0.0;
   

    public UserState() {}

    
    public void timeUpdated(int hour)
    {
        if (hour >= 5 && hour <= 10)
        {
            time = Time.MORNING;
        }
        else if (hour >= 11 && hour <= 16)
        {
            time = Time.AFTERNOON;
        }
        else
        {
            time = Time.NIGHT;
        }
    }
    
    public void dayUpdated(String day)
    {
        day = day.toLowerCase();
        switch (day)
        {
            case "sunday".:
                this.day = SUNDAY;
                break;
            case "monday":
                this.day = MONDAY;
                break;
            case "tueday":
                this.day = TUESDAY;
                break;
            case "wednesday":
                this.day = WEDNESDAY;
                break;
            case "thursday":
                this.day = THURSDAY;
                break;
            case "friday":
                this.day = FRIDAY;
                break;
            case "saturday":
                this.day = SATURDAY;
                break;
            default:
                this.day = null;
                break;
    }

    public void locationUpdated(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getTime() {
        return this.time;
    }

    public double getDay() {
        return this.day;
    }

    public double getLatitute() {
        return this.latitute;
    }

    public double getLongitude() {
        return this.longitude;
    }


}
