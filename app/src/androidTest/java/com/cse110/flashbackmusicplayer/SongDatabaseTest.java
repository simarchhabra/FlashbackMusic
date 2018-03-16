package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SongDatabaseTest {

    private MockUserState state;
    private SongDatabase songDB;

    @Before
    public void init() {
        state = new MockUserState();
        songDB = new SongDatabase(state);
    }

    @Test
    public void hasStateChanged() throws Exception {
        // Test if the state is marked as changed immediately after database creation.
        assertFalse(songDB.hasStateChanged());

        // Change the time of the user state and test if the state has changed.
        state.setTimeSegment(TimeSegment.EVENING);
        assertTrue(songDB.hasStateChanged());

        // Update the database and check if user state has changed since last update.
        songDB.generateFlashbackList();
        assertFalse(songDB.hasStateChanged());

        // Change location and day of week.
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLongitude(12.0); loc.setLatitude(-114.5);
        state.setLocation(loc);
        state.setDayOfWeek(Calendar.FRIDAY);
        assertTrue(songDB.hasStateChanged());

        // Update the database and check if user state has changed since last update.
        songDB.generateFlashbackList();
        assertFalse(songDB.hasStateChanged());
    }

    @Test
    public void generateFlashbackList() throws Exception {
        // Create and add songs to the database.
        byte[] albumCover = {0, 1, 0, 1};
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        // Test that generateFlashbackList caches the user state.
        assertFalse(songDB.hasStateChanged());
        state.setDayOfWeek(Calendar.WEDNESDAY);
        assertTrue(songDB.hasStateChanged());
        songDB.generateFlashbackList();
        assertFalse(songDB.hasStateChanged());

        // The flashback list should be empty since none of the songs have been played yet.
        assertTrue(songDB.isEmpty());

        // Play the songs so that they are added to the database.
        state.setSystemTime(1);
        s1.donePlaying(state);
        state.setSystemTime(2);
        s2.donePlaying(state);
        state.setSystemTime(3);
        s3.donePlaying(state);
        state.setSystemTime(4);
        s4.donePlaying(state);
        state.setSystemTime(5);
        s5.donePlaying(state);

        songDB.generateFlashbackList();
        // The flashback list should now have songs in it.
        assertFalse(songDB.isEmpty());

        // Get the top element and pop it.
        assertEquals(s5, songDB.top()); songDB.pop();
        assertEquals(s4, songDB.top()); songDB.pop();
        assertEquals(s3, songDB.top()); songDB.pop();
        assertEquals(s2, songDB.top()); songDB.pop();
        assertEquals(s1, songDB.top());
    }

    @Test
    public void insert() throws Exception {
        // Create and add songs to the database.
        byte[] albumCover = {0, 1, 0, 1};
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        // Make sure that you can access these songs.
        assertEquals(s1, songDB.get("s1"));
        assertEquals(s2, songDB.get("s2"));
        assertEquals(s3, songDB.get("s3"));
        assertEquals(s4, songDB.get("s4"));
        assertEquals(s5, songDB.get("s5"));

        state.setSystemTime(1);
        s1.donePlaying(state);
        state.setSystemTime(2);
        s2.donePlaying(state);
        state.setSystemTime(3);
        s3.donePlaying(state);
        state.setSystemTime(4);
        s4.donePlaying(state);
        state.setSystemTime(5);
        s5.donePlaying(state);

        songDB.generateFlashbackList();

        // Get the top element and pop it.
        assertEquals(s5, songDB.top()); songDB.pop();
        assertEquals(s4, songDB.top()); songDB.pop();
        assertEquals(s3, songDB.top()); songDB.pop();
        assertEquals(s2, songDB.top()); songDB.pop();
        assertEquals(s1, songDB.top());
    }

    @Test
    public void top() throws Exception {
        // Create and add songs to the database.
        byte[] albumCover = {0, 1, 0, 1};
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        state.setSystemTime(1);
        s1.donePlaying(state);
        state.setSystemTime(2);
        s2.donePlaying(state);
        state.setSystemTime(3);
        s3.donePlaying(state);
        state.setSystemTime(4);
        s4.donePlaying(state);
        state.setSystemTime(5);
        s5.donePlaying(state);

        songDB.generateFlashbackList();

        // Get the top element a couple times, to make sure that it is not removed.
        assertEquals(s5, songDB.top());
        assertEquals(s5, songDB.top());

        // Get the top element and pop it.
        assertEquals(s5, songDB.top()); songDB.pop();
        assertEquals(s4, songDB.top()); songDB.pop();
        assertEquals(s3, songDB.top()); songDB.pop();
        assertEquals(s2, songDB.top()); songDB.pop();
        assertEquals(s1, songDB.top());
    }

    @Test
    public void pop() throws Exception {
        // Create and add songs to the database.
        byte[] albumCover = {0, 1, 0, 1};
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        state.setSystemTime(1);
        s1.donePlaying(state);
        state.setSystemTime(2);
        s2.donePlaying(state);
        state.setSystemTime(3);
        s3.donePlaying(state);
        state.setSystemTime(4);
        s4.donePlaying(state);
        state.setSystemTime(5);
        s5.donePlaying(state);

        songDB.generateFlashbackList();

        // Get the top element and pop it.
        assertEquals(s5, songDB.top()); songDB.pop();
        assertEquals(s4, songDB.top()); songDB.pop();
        assertEquals(s3, songDB.top()); songDB.pop();
        assertEquals(s2, songDB.top()); songDB.pop();
        assertEquals(s1, songDB.top());
    }

    @Test
    public void isEmpty() throws Exception {
        // Make sure that the database is empty before any songs are added to it.
        assertTrue(songDB.isEmpty());

        // Add 1 song with a priority of 0, generate the flashback list and test if the list is empty.
        byte[] albumCover = {0, 1, 0, 1};
        Song song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
        songDB.insert(song);
        songDB.generateFlashbackList();
        assertTrue(songDB.isEmpty());

        // Play the added song, so that it now has a priority > 0.
        song.donePlaying(state);
        songDB.generateFlashbackList();
        assertFalse(songDB.isEmpty());

        // Remove the song from the database.
        songDB.pop();
        assertTrue(songDB.isEmpty());

        // Add a couple songs, and then remove them
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        s1.donePlaying(state);

        songDB.generateFlashbackList();
        assertFalse(songDB.isEmpty());

        // Remove the songs.
        songDB.pop();
        assertFalse(songDB.isEmpty());
        songDB.pop();
        assertTrue(songDB.isEmpty());

        // Play a song and then completely change the state.
        s2.donePlaying(state);
        songDB.generateFlashbackList();
        assertFalse(songDB.isEmpty());
        // Change the state, so that it has nothing in common with the state when the song was played.
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLongitude(12.0); loc.setLatitude(-114.5);
        state.setLocation(loc);
        state.setDayOfWeek(Calendar.FRIDAY);
        state.setTimeSegment(TimeSegment.EVENING);
        // Generate the list and make sure that the list is empty.
        songDB.generateFlashbackList();
        assertTrue(songDB.isEmpty());
    }

    @Test
    public void get() throws Exception {
        // Search for a song not in the database.
        Song song = songDB.get("not here");
        assertNull(song);

        // Add a couple songs and search for them.
        byte[] albumCover = {0, 1, 0, 1};
        Song s1 = new Song("filename", "s1", "album", "artist", "trackNumber", albumCover);
        Song s2 = new Song("filename", "s2", "album", "artist", "trackNumber", albumCover);
        Song s3 = new Song("filename", "s3", "album", "artist", "trackNumber", albumCover);
        Song s4 = new Song("filename", "s4", "album", "artist", "trackNumber", albumCover);
        Song s5 = new Song("filename", "s5", "album", "artist", "trackNumber", albumCover);

        songDB.insert(s1);
        songDB.insert(s2);
        songDB.insert(s3);
        songDB.insert(s4);
        songDB.insert(s5);

        assertEquals(s1, songDB.get("s1"));
        assertEquals(s2, songDB.get("s2"));
        assertEquals(s3, songDB.get("s3"));
        assertEquals(s4, songDB.get("s4"));
        assertEquals(s5, songDB.get("s5"));
        assertNull(songDB.get("doesn't exist"));
    }

    @Test
    public void calculatePriority() throws Exception {
        // Create a song.
        byte[] albumCover = {0, 1, 0, 1};
        Song song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);

        // If the song has never been played, it should have a priority of zero.
        assertEquals(0, songDB.calculatePriority(song));

        // Play the song at the current time, date, and location.
        song.donePlaying(state);
        assertEquals(3, songDB.calculatePriority(song));

        // Move to a completely new location, and check if the priority is back to zero.
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLongitude(12.0); loc.setLatitude(-114.5);
        state.setLocation(loc);
        state.setDayOfWeek(Calendar.FRIDAY);
        state.setTimeSegment(TimeSegment.EVENING);
        songDB.generateFlashbackList();
        assertEquals(0, songDB.calculatePriority(song));

        // Play the song, and move to a new day of week.
        song.donePlaying(state);
        state.setDayOfWeek(Calendar.THURSDAY);
        songDB.generateFlashbackList();
        assertEquals(2, songDB.calculatePriority(song));

        // Play the song in multiple location and test that the first location is properly stored.
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc2.setLongitude(112.0); loc2.setLatitude(-14.51);
        state.setLocation(loc2);
        song.donePlaying(state);

        Location loc3 = new Location(LocationManager.GPS_PROVIDER);
        loc3.setLongitude(-62.0); loc3.setLatitude(-145.1);
        state.setLocation(loc3);
        song.donePlaying(state);

        Location loc4 = new Location(LocationManager.GPS_PROVIDER);
        loc4.setLongitude(-36.1); loc4.setLatitude(-1.51);
        state.setLocation(loc4);

        songDB.generateFlashbackList();
        assertEquals(2, songDB.calculatePriority(song));

        song.donePlaying(state);
        assertEquals(3, songDB.calculatePriority(song));

        state.setLocation(loc);
        songDB.generateFlashbackList();
        assertEquals(3, songDB.calculatePriority(song));
    }

}