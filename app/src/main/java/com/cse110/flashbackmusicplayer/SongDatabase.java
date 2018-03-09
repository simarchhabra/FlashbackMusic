package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This is a priority queue of songs that orders song by how likely they are to be played
 * by the user.
 */
public class SongDatabase {

    // The state of the user, last time songDatabase was updated.
    private int dayOfTheWeek;
    private int weekOfYear;
    private TimeSegment timeSegment;
    private Location location;

    // The array that stores all the loaded songs.
    private ArrayList<Song> songs;
    // The max heap is generated when the user enters flashback mode and needs to play a song.
    private PriorityQueue<Song> flashbackList;

    private PriorityQueue<Song> vibeList;

    private UserState state;


    public SongDatabase(UserState state) {
        this.state = state;

        // Cache the state of the user for future reference.
        this.dayOfTheWeek = state.getDayOfWeek();
        this.timeSegment = state.getTimeSegment();
        this.location = state.getLocation();

        // Creates an array and a max heap that will store all the songs.
        this.songs = new ArrayList<>();
        this.flashbackList = new PriorityQueue<>(50, new SongComparator());

        this.vibeList = new PriorityQueue<>(50, new SongComparator());
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

            // Try to break ties by seeing if one (and only one) is favorited.
            if (s1.isFavorited() != s2.isFavorited()) {
                // Return whichever song is favorited.
                return s1.isFavorited() ? -1 : 1;
            }

            // Otherwise, the one that was most recently played has a greater priority.
            return s1.getSystemTime() > s2.getSystemTime() ? -1 : 1;
        }
    }

    public boolean hasStateChanged() {
        // Compared the cached state with the actual one.

        boolean changed = state.getTimeSegment() != timeSegment ||
                state.getLocation().equals(location) ||
                state.getDayOfWeek() != dayOfTheWeek;
        Log.d("SongDatabase", "The database state has " + (changed ? "changed" : "not changed"));
        return changed;

    }

    // Creates a list of songs ordered by priority.
    public void generateFlashbackList() {
        // Cache the state of the user for future reference.
        dayOfTheWeek = state.getDayOfWeek();
        timeSegment = state.getTimeSegment();
        location = state.getLocation();

        // Get rid of all the elements in the heap and re-add them with new priorities.
        flashbackList.clear();
        flashbackList.addAll(songs);

        Log.d("SongDatabase", "Generated FlashbackList");
    }

    public void generateVibeFlashbackList() {

        dayOfTheWeek = state.getDayOfWeek();
        weekOfYear = state.getWeekOfYear();
        location = state.getLocation();

        vibeList.clear();
        vibeList.addAll(songs);

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
        boolean empty = flashbackList.isEmpty() || calculatePriority(flashbackList.peek()) == 0;
        Log.d("SongDatabase", "The list is " + (empty ? "empty" : "not empty"));
        return empty;
    }

    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public Song get(String name) {
        // Go through all the songs in the queue and check if any match.
        for (Song song : songs) {
            if (song.getTitle().equals(name)) {
                return song;
            }
        }

        return null;
    }

    public ArrayList<Song> getAlbum(String albumName) {
        // Go through all the songs in the queue and check if any album names match.
        ArrayList<Song> album = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbum().equals(albumName)) {
                album.add(song);
            }
        }
        album.sort((s1, s2) -> {
            int track1 = Integer.parseInt(s1.getTrackNumber().split("/")[0]);
            int track2 = Integer.parseInt(s2.getTrackNumber().split("/")[0]);
            return track1 < track2 ? -1 : 1;
        });
        return album;
    }

    /**
     * Calculates the priority of this song using the current state of the user
     * @return How likely this song is to be played.
     */
    public int calculatePriority(Song song) {
        int priority = 0;

        // If the song is disliked, its priority is zero.
        if (song.isDisliked()) return 0;

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

    /**
     * Calculates the priority of this song using the current state of the user
     * @return How likely this song is to be played.
     */
    public int calculatePriority2(Song song) {
        int priority = 0;

        // If the song is disliked, its priority is zero.
        if (song.isDisliked()) return 0;

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
        //if (song.getDaysOfWeek()[dayOfTheWeek - 1]) priority++;

        if(song.getWeeksOfYear()[weekOfYear]) priority++;



        return priority;
    }

}
