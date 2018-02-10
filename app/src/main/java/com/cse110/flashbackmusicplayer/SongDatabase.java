package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * This is a priority queue of songs that orders song by how likely they are to be played
 * by the user.
 */
public class SongDatabase {

    // The information associated with the user.
    private UserState userState = null;

    // When set to true, signifies that the user state has changed and heap needs to be updated.
    private boolean hasStateChanged = false;

    // The internal array representation of the heap.
    ArrayList<Song> songs;

    public SongDatabase(UserState userState) {
        // Store a reference to user state to use it in the future.
        this.userState = userState;
        // Create an empty list of songs.
        songs = new ArrayList<>();
    }

    public void userStateChanged() {
        hasStateChanged = true;
    }

    public boolean hasStateChanged() {
        return hasStateChanged;
    }

    // Perform heap sort on songs using new priorities.
    public void calculateFlashBackOrder() {

    }

    // Adds an element to the heap, reordering as necessary.
    public void insert(Song song) {

    }

    // Return a COPY of the song at the head.
    public Song top() {
        return null;
    }

    // Simply remove the head node of the heap.
    public void pop() {

    }

    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public void get(String name) {

    }

}
