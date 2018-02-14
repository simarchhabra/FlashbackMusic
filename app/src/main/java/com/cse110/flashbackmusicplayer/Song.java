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
    private long systemTime = 0;
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

    public void startedPlaying(UserState state) {
        // Record when and where this song was played.
        daysOfWeek[state.getDayOfWeek() - 1] = true;
        timeSegments[state.getTimeSegment().getIndex()] = true;
        locations.add(state.getLocation());

        // Store the most recent time and place this song was played.
        time = state.getTime();
        date = state.getDate();
        place = state.getPlace();
        systemTime = state.getSystemTime();
    }
    public String getName() {return title;}
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getPlace() { return place; }
    public long getSystemTime() { return systemTime; }

    public boolean[] getDaysOfWeek() { return daysOfWeek; }
    public boolean[] getTimeSegments() { return timeSegments; }
    public ArrayList<Location> getLocations() { return locations; }

    public String getTitle() { return title; }
    public String getAlbum() { return album; }
    public String getArtist() { return artist; }
}
