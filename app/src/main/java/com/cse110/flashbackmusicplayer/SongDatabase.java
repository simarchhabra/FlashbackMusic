package com.cse110.flashbackmusicplayer;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This is a priority queue of songs that orders song by how likely they are to be played
 * by the user.
 */
public class SongDatabase {

    // The information associated with the user.
    private UserState userState = null;

    // When set to true, signifies that the user state has changed and heap needs to be updated.
    private boolean hasStateChanged = false;

    // The max heap that stores ALL the loaded songs. All the songs are ordered by priority.
    private PriorityQueue<Song> queue;

    public SongDatabase(UserState userState) {
        // Store a reference to user state to use it in the future.
        this.userState = userState;
        // Creates max heap that will store all the songs.
        this.queue = new PriorityQueue<>(10, new SongComparator());
    }

    private class SongComparator implements Comparator<Song>
    {
        @Override
        public int compare(Song s1, Song s2)
        {

            if (s1.calculatePriority(userState) < s2.calculatePriority(userState))
            {
                return -1;
            }
            if (s1.calculatePriority(userState) > s2.calculatePriority(userState))
            {
                return 1;
            }

            // TODO: break ties using favorited songs and if both are favorited or neither, then by
            // which one was most recently played.
            return 0;
        }
    }

    public void userStateChanged() {
        hasStateChanged = true;
    }

    public boolean hasStateChanged() {
        return hasStateChanged;
    }

    // Redo heap using the new priorities.
    public void recalculate() {
        // Get the internal array representation of the heap.
        Song songs[] = (Song[]) queue.toArray();

        // Get rid of all the elements in the heap and re-add them with new priorities.
        queue.clear();
        for (Song song : songs) {
            this.insert(song);
        }

        // The heap is now up to date with the user state.
        hasStateChanged = false;
    }

    // Adds an element to the heap, reordering as necessary.
    public void insert(Song song) {
        queue.add(song);
    }

    // Return a COPY of the song at the head.
    public Song top() {
        return queue.peek();
    }

    // Simply remove the head node of the heap.
    public void pop() {
        queue.poll();
    }


    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public Song get(String name) {
        // Go through all the songs in the queue and check if any matc
        for (Song song : queue) {
            if (song.getName().equals(name))
                return song;
        }

        return null;
    }

}
