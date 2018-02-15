package com.cse110.flashbackmusicplayer;

import android.location.Location;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This is a priority queue of songs that orders song by how likely they are to be played
 * by the user.
 */
public class SongDatabase {

    // The information associated with the user.
    private UserState userState = null;

    // The state of the user, last time songDatabase was updated.
    private int dayOfTheWeek;
    private TimeSegment timeSegment;
    private Location location;

    // The array that stores all the loaded songs.
    private ArrayList<Song> songs;
    // The max heap is generated when the user enters flashback mode and needs to play a song.
    private PriorityQueue<Song> flashbackList;


    public SongDatabase(UserState userState) {
        // Store a reference to user state to use it in the future.
        this.userState = userState;
        // Cache the state of the user for future reference.
        this.dayOfTheWeek = userState.getDayOfWeek();
        this.timeSegment = userState.getTimeSegment();
        this.location = userState.getLocation();

        // Creates an array and a max heap that will store all the songs.
        this.songs = new ArrayList<>();
        this.flashbackList = new PriorityQueue<>(50, new SongComparator());
    }

    private class SongComparator implements Comparator<Song>
    {
        @Override
        public int compare(Song s1, Song s2)
        {

            if (calculatePriority(s1) > calculatePriority(s2))
            {
                return -1;
            }
            if (calculatePriority(s1) < calculatePriority(s2))
            {
                return 1;
            }

            // TODO: break ties using favorited songs and if both are favorited or neither, then by
            // which one was most recently played.
            return s1.getSystemTime() > s2.getSystemTime() ? -1 : 1;
        }
    }

    public boolean hasStateChanged() {
        // Compared the cached state with the actual one.
        return userState.getTimeSegment() != timeSegment ||
                userState.getLocation().equals(location) ||
                userState.getDayOfWeek() != dayOfTheWeek;
    }

    // Creates a list of songs ordered by priority.
    public void generateFlashbackList() {
        // Cache the state of the user for future reference.
        dayOfTheWeek = userState.getDayOfWeek();
        timeSegment = userState.getTimeSegment();
        location = userState.getLocation();

        // Get rid of all the elements in the heap and re-add them with new priorities.
        flashbackList.clear();
        flashbackList.addAll(songs);
    }

    // Adds an element to the list of songs.
    public void insert(Song song) {
        songs.add(song);
    }

    // Return the song with the greatest priority.
    public Song top() {
        return flashbackList.peek();
    }

    // Remove the song with the greatest priority.
    public void pop() {
        flashbackList.poll();
    }

    public boolean isEmpty() {
        // If there is nothing left in the queue, or if the priority of all elements is 0.
        return flashbackList.isEmpty() || calculatePriority(flashbackList.peek()) == 0;
    }


    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public Song get(String name) {
        // Go through all the songs in the queue and check if any match.
        for (Song song : songs) {
            if (song.getTitle().equals(name))
                return song;
        }

        return null;
    }

    public ArrayList<Song> getAllSongs()
    {
        return songs;
    }
    /**
     * Calculates the priority of this song using the current state of the user
     * @return How likely this song is to be played.
     */
    public int calculatePriority(Song song) {
        int priority = 0;

        // Check if the current user location is near one where this song was played.
        for (Location songLocation : song.getLocations()) {
            // Get the location of the two points we are finding the distance between.
            double lat1 = songLocation.getLatitude();
            double long1 = songLocation.getLongitude();
            double lat2 = location.getLatitude();
            double long2 = location.getLongitude();

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
        if (song.getDaysOfWeek()[dayOfTheWeek - 1]) priority++;

        // If this song was played in the same time segment, increase priority.
        if (song.getTimeSegments()[timeSegment.getIndex()]) priority++;

        return priority;
    }

}