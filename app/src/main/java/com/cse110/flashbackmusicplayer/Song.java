package com.cse110.flashbackmusicplayer;

public class Song {

    String title;

    public Song(String title) {
        this.title = title;
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
