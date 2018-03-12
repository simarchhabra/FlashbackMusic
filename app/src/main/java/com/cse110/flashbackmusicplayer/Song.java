package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.ArrayList;

public class Song implements SongSubject, FirebaseObserver {

    // The data defining this song.
    private String filename, url, title, album, artist, trackNumber;
    // The track in the album.
    private byte[] albumCover;

    // Whether or not this track is downloaded to the users phone or not.
    private boolean downloaded = false;

    // List of all locations and times this song has been played at and the respective users.
    // The lengths of these arrays are the same and at each index the triple shows one play of song.
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    public Song(String filename, String url, String title, String album, String artist, String trackNumber,
                byte[] albumCover) {
        this.filename = filename;
        this.url = url;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
        this.albumCover = albumCover;

        obs = new ArrayList<>();
    }

    public void donePlaying(UserState state) {
        // Notify that the information about the song has finished.
        notifyObservers(state.getUser(), state.getSystemTime(), state.getLocation());
    }

    public String getTime() { return "placeholder_time"; }
    public String getDate() { return "placeholder_date"; }
    public String getPlace() { return "placeholder_place"; }
    public long getSystemTime() { return 0; }

    public ArrayList<Location> getLocations() { return locations; }
    public ArrayList<Long> getTimes() { return times; }
    public ArrayList<String> getUsers() { return users; }

    public String getTitle() { return title; }
    public String getAlbum() { return album; }
    public String getArtist() { return artist; }
    public String getFilename() { return filename; }
    public String getURL() { return url; }
    public String getTrackNumber() { return trackNumber; }

    public byte[] getAlbumCover() { return albumCover; }

    // Whether the song was favorited or disliked. Should not both be true at the same time.
    public enum LIKED_STATUS { FAVORITED, DISLIKED, NEUTRAL }
    private LIKED_STATUS likedStatus = LIKED_STATUS.NEUTRAL;

    public LIKED_STATUS getLikedStatus() { return likedStatus; }
    public void setLikedStatus(LIKED_STATUS likedStatus) { this.likedStatus = likedStatus; }
    public boolean isFavorited() { return likedStatus == LIKED_STATUS.FAVORITED; }
    public boolean isDisliked() { return likedStatus == LIKED_STATUS.DISLIKED; }

    @Override
    public void update(String user, Location location, long time) {
        users.add(user);
        locations.add(location);
        times.add(time);
    }

    private ArrayList<SongObserver> obs;

    @Override
    public void registerObserver(SongObserver o) {
        obs.add(o);
    }

    @Override
    public void removeObserver(SongObserver o) {
        obs.remove(o);
    }

    @Override
    public void notifyObservers(String user, long time, Location location) {
        for(SongObserver ob : obs) {
            ob.update(this, user, time, location);
        }
    }
}
