package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;

public class Song {

    // The data defining this song.
    private String title, album, artist;
    // The track in the album.
    private int trackNumber;

    // Optional metadata
    private String genre = "";
    private String comments = "";
    private int year = 0;

    // Last time, date, and place this song was played.
    private String time = "";
    private String date = "";
    private String place = "";

    // Lists of all the locations, times, and days this song has been played.
    private boolean[] daysOfWeek = new boolean[7]; // There are 7 days in the week.
    private boolean[] timeSegments = new boolean[TimeSegment.numSegments];
    private ArrayList<Location> locations = new ArrayList<>();
    
    public Song(String title, String album, String artist, int trackNumber) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
    }

    public Song(String title, String album, String artist, int trackNumber,
            String genre, String comments, int year)
    {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
        this.genre = genre;
        this.comments = comments;
        this.year = year;
    }

    /**
     * Calculates the priority of this song using the current state of the user
     * @param state the user's current location, time, day of the week, etc.
     * @return How likely this song is to be played.
     */
    // TODO: probably move this to the song database class-A song doesn't need to know its priority.
    public int calculatePriority(UserState state) {
        int priority = 0;

        // Check if the current user location is near one where this song was played.
        Location userLoc = state.getLocation();
        for (Location location : locations) {
            // Get the location of the two points we are finding the distance between.
            double lat1 = location.getLatitude();
            double long1 = location.getLongitude();
            double lat2 = userLoc.getLatitude();
            double long2 = userLoc.getLongitude();

            double theta = long1 - long2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            // Turn degrees to minutes, turn minutes to nautical miles, turn nautical miles to feet.
            dist = dist * 60 * 1.1515 * 6076.12;

            if (dist < 1000) {
                // Increase the priority, and stop calculating any other locations.
                priority++;
                break;
            }
        }

        // If this song was played on the same day of the week, increase priority.
        if (daysOfWeek[state.getDayOfWeek() - 1]) priority++;

        // If this song was played in the same time segment, increase priority.
        if (timeSegments[state.getTimeSegment().getIndex()]) priority++;

        return priority;
    }

    public void startedPlaying(UserState state) {
        // Record when and where this song was played.
        daysOfWeek[state.getDayOfWeek() - 1] = true;
        timeSegments[state.getTimeSegment().getIndex()] = true;
        locations.add(state.getLocation());

        // Store the most recent time and place this song was played.
        time = state.getTime();
        date = state.getDate();
        place = state.getPlace();
    }

    // TODO: Should get rid the last entry in the array lists and return time, date, and place
    // to their previous values.
    public void abortedPlaying() {

    }
    public String getName() {return title;}
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getPlace() { return place; }

}
