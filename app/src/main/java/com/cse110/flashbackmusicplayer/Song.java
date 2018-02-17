package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

public class Song {

    // The data defining this song.
    private String filename, title, album, artist, trackNumber, trackDuration;
    // The track in the album.
    private byte[] albumCover;

    // Whether the song was favorited or disliked. Should not both be true at the same time.
    private boolean favorited = false;
    private boolean disliked = false;

    // Last time, date, and place this song was played.
    private long systemTime = 0;
    private String time = null;
    private String date = null;
    private String place = null;

    // Lists of all the locations, times, and days this song has been played.
    private boolean[] daysOfWeek = new boolean[7]; // There are 7 days in the week.
    private boolean[] timeSegments = new boolean[TimeSegment.numSegments];
    private ArrayList<Location> locations = new ArrayList<>();
    
    public Song(String filename, String title, String album, String artist, String trackNumber,
                byte[] albumCover) {
        this.filename = filename;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
        this.albumCover = albumCover;
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

    public String getFilename() { return filename; }
    public String getTrackNumber() { return trackNumber; }
    public byte[] getAlbumCover() { return albumCover; }

    public boolean isFavorited() { return favorited; }
    public void setFavorited(boolean favorited) { this.favorited = favorited; }

    public boolean isDisliked() { return disliked; }
    public void setDisliked(boolean disliked) { this.disliked = disliked; }
}
