package com.cse110.flashbackmusicplayer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // All of the information associated with the user.
    static UserState userState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the user.
        userState = new UserState();
        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        // Create a location listener and make it update user state on change.
        setUpLocation();

        // List of the names of the songs in res/raw/
        List<String> songTitles = new ArrayList<>();
        // List of all the albums in res/raw
        List<String> albumsList = new ArrayList<>();

        // For every single file in the res/raw folder...
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            // Get the name of the song file.
            String filename = field.getName();

            // Get the metadata from the song.
            Uri source = Uri.parse("android.resource://" + getPackageName() + "/raw/" + filename);
            mmr.setDataSource(this, source);

            // Extract information from the metadata.
            String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            songTitles.add(songTitle);
            String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String track_num = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            byte[] album_art = mmr.getEmbeddedPicture();

            // Add this song's album to the albums listview if it doesn't already exist.
            if (!albumsList.contains(albumName)) albumsList.add(albumName);

            // Create the song object from the metadata, and insert it into the database.
            Song song = new Song(filename, songTitle, albumName, artist, track_num, album_art);
            songDB.insert(song);
        }

        // Display the songs list on the screen.
        ListAdapter songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        final ListView songsView = (ListView) findViewById(R.id.songsView);
        songsView.setAdapter(songAdapter);

        // Display the album list on the screen.
        ListAdapter albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumsList);
        final ListView albumsView = (ListView) findViewById(R.id.albumsView);
        albumsView.setAdapter(albumAdapter);

        // If the flashback button is pressed, open the flashback activity.
        Button launchFlashbackActivity = (Button) findViewById(R.id.switchMode);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the flashback mode.
                Intent intent = new Intent(MainActivity.this, FlashbackActivity.class);
                startActivity(intent);
            }
        });

        Button albumButton = (Button) findViewById(R.id.albumsDisplayButton);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the track listview and unhide the album listview.
                songsView.setVisibility(View.GONE);
                albumsView.setVisibility(View.VISIBLE);
            }
        });

        Button tracksButton = (Button) findViewById(R.id.tracksDisplayButton);
        tracksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Unhide the track listview and hide the album listview.
                albumsView.setVisibility(View.GONE);
                songsView.setVisibility(View.VISIBLE);
            }
        });

        // Play the song whenever it's name is placed on the list.
        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // Get the name of the song to play.
                String[] playing = new String[1];
                String toPlay = adapterView.getItemAtPosition(pos).toString();
                // create an intent for MediaService
                Intent serviceIntent = new Intent(MainActivity.this, MediaService.class);
                // Create a receiver to store the name of the current song being played
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        playing[0] = intent.getStringExtra("SONG_NAME");
                    }
                };
                registerReceiver(broadcastReceiver, new IntentFilter(MediaService.ACTION_BROADCAST));

                // if song is already playing and a different one chosen, stop playback and create
                // new service for playback
                if (checkSongPlaying(playing[0], toPlay) == ServicePlaybackState.DIFF_SONG) {
                    unregisterReceiver(broadcastReceiver);
                    stopService(serviceIntent);
                    serviceIntent = new Intent(MainActivity.this, MediaService.class);
                    serviceIntent.setAction("START");
                    serviceIntent.putExtra("NAME", toPlay);
                    startService(serviceIntent);
                }
                // if no song is currently being played, start new service for playback
                else if (checkSongPlaying(playing[0], toPlay) == ServicePlaybackState.NO_SONG){
                    serviceIntent = new Intent(MainActivity.this, MediaService.class);
                    serviceIntent.setAction("START");
                    serviceIntent.putExtra("NAME", toPlay);
                    startService(serviceIntent);
                }

                Intent currIntent = new Intent(MainActivity.this, CurrentTrackDisplay.class);
                currIntent.putExtra("NAME", toPlay);
                // Open a new activity for displaying song metadata and addressing user functionality
                startActivity(currIntent);
            }
        });

        // Play the songs in this album.
        albumsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // Get the name of the song to play.
                String name = adapterView.getItemAtPosition(pos).toString();
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra("NAME", name);
                // Open a new activity, where the song will play.
                startActivity(intent);
            }
        });
    }
    // Enum for possible states of song playback
    private enum ServicePlaybackState{
        NO_SONG, SAME_SONG, DIFF_SONG
    };

    /**
     * Determines if the user-clicked song is the same as the one already playing or if no
     * song is currently playing
     */
    private ServicePlaybackState checkSongPlaying(String playing, String toPlay) {
        Log.d("Check", "Goes in function checkSongPlaying");
        if (playing != null) {
            Log.d("Access", "Knows current Song playing");
            if (playing.equals(toPlay)) {
                return ServicePlaybackState.SAME_SONG;
            }
            return ServicePlaybackState.DIFF_SONG;
        }
        Log.d("Access", "Does not know current song playing");
        return ServicePlaybackState.NO_SONG;
    }

    private void setUpLocation() {
        // Record the user's location whenever it changes.
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Get the new updated location of the user.
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                // Update the user state with the new location.
                userState.locationUpdated(lat, lon, "templocation");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        // Assert that we have permissions to get location data.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

}
