package com.cse110.flashbackmusicplayer;

public class Song {

    String title, album, artist;
    int track_number;

    // optional metadata
    String genre = "";
    String comments = "";
    int year = -1;
    
    public Song(String title, String album, String artist, int track_number) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.track_number = track_number;
    }

    public Song(String title, String album, String artist, int track_number,
            String genre, String comments, int year)
    {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.track_number = track_number;
        this.genre = genre;
        this.comments = comments;
        this.year = year;
    }

    /**
     * Calculates the priority of this song using the current state of the user
     * @param state the user's current location, time, day of the week, etc.
     * @return How likely this song is to be played.
     */
    public int calculatePriority(UserState state) {

        return 0;
    }

}
