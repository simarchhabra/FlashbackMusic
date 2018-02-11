package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.PriorityQueue;

import java.util.Comparator;

public class SongComparator implements Comparator<Song>
{
    @Override
    public int compare(Song s1, Song s2)
    {

        if (s1.calculatePriority() < s2.calculatePriority())
        {
            return -1;
        }
        if (s1.calculatePriority() > s2.calculatePriority()
        {
            return 1;
        }
        return 0;
    }
}

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
    private PriorityQueue<Song> queue;

    Comparator<Song> comparator;

    public SongDatabase(UserState userState) {
        // Store a reference to user state to use it in the future.
        this.userState = userState;
        // Create an empty list of songs.
        //TODO - not an empty list but a list of all songs?
        songs = new ArrayList<>();
        queue = new PriorityQueue<Song>(10, comparator);

        comparator = new SongComparator();
    }

    public void userStateChanged() {
        hasStateChanged = true;
    }

    public boolean hasStateChanged() {
        return hasStateChanged;
    }

    // Perform heap sort on songs using new priorities.
    public void calculateFlashBackOrder() {
        Song song_arr[] = queue.toArray();
        queue.clear();

        for(int i; i<song_arr.length; i++) {
            this.insert(song_arr[i]));
        }
        //assuming this needs to be here
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
        //return the value deleted or null if nothing is deleted
        return queue.poll()
    }

    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public void get(String name) {
        Iterator<Song> itr = queue.iterator();
        boolean found = false;
        while (itr.hasNext()) {
            Song temp = itr.next();
            if (temp.getName().equals(name))
                return temp;

        }
    }

}
