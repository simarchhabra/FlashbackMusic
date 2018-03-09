package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.os.Bundle;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observer;

@IgnoreExtraProperties
public class Song implements SongSubject{

    // The data defining this song.
    private String filename, title, album, artist, trackNumber;
    // The track in the album.
    @Exclude
    private byte[] albumCover;

    @Exclude
    private ArrayList<DBObserver> obs;

    @Override
    public void registerObserver(DBObserver o) {
        obs.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        obs.remove(o);
    }

    @Override
    public void notifyObservers() {
        for(DBObserver ob : obs) {
            ob.update(this);
        }
    }

    // Whether the song was favorited or disliked. Should not both be true at the same time.
    public enum LIKED_STATUS {
        FAVORITED,
        DISLIKED,
        NEUTRAL
    }
    private LIKED_STATUS likedStatus = LIKED_STATUS.NEUTRAL;

    // Last time, date, and place this song was played.
    private long systemTime = 0;
    private String time = null;
    private String date = null;
    private String place = null;

    // Lists of all the locations, times, and days this song has been played.
    private boolean[] daysOfWeek = new boolean[7]; // There are 7 days in the week.
    private boolean[] timeSegments = new boolean[TimeSegment.numSegments];
    private boolean[] weeksOfYear = new boolean[Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR)];
    @Exclude
    private ArrayList<Location> locations = new ArrayList<>();
    
    public Song(String filename, String title, String album, String artist, String trackNumber,
                byte[] albumCover) {
        this.filename = filename;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
        this.albumCover = albumCover;
        obs = new ArrayList<DBObserver>();
    }

    public void startedPlaying(UserState state) {
        // Record when and where this song was played.
        daysOfWeek[state.getDayOfWeek() - 1] = true;
        weeksOfYear[state.getWeekOfYear() - 1] = true;
        timeSegments[state.getTimeSegment().getIndex()] = true;
        locations.add(state.getLocation());

        // Store the most recent time and place this song was played.
        time = state.getTime();
        date = state.getDate();
        place = state.getPlace();
        systemTime = state.getSystemTime();
        notifyObservers();
    }

    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getPlace() { return place; }
    public long getSystemTime() { return systemTime; }

    public boolean[] getDaysOfWeek() { return daysOfWeek; }
    public boolean[] getWeeksOfYear() { return weeksOfYear; }
    public boolean[] getTimeSegments() { return timeSegments; }

    @Exclude
    public ArrayList<Location> getLocations() { return locations; }

    public String getTitle() { return title; }
    public String getAlbum() { return album; }
    public String getArtist() { return artist; }

    public String getFilename() { return filename; }
    public String getTrackNumber() { return trackNumber; }

    @Exclude
    public byte[] getAlbumCover() { return albumCover; }

    public boolean isFavorited() { return likedStatus == LIKED_STATUS.FAVORITED; }
    public boolean isDisliked() { return likedStatus == LIKED_STATUS.DISLIKED; }

    @Exclude
    public LIKED_STATUS getLikedStatus() { return likedStatus; }
    public void setLikedStatus(LIKED_STATUS likedStatus) { this.likedStatus = likedStatus; }
}
