package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // All of the information associated with the user.
    static UserState userState = null;

    // In charge of handling all requests to play music.
    static MusicSystem musicSystem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the user.
        userState = new UserState();
        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        // Create the system that will play all the music.
        musicSystem = new MusicSystem(MainActivity.this);
        // Create a location listener and make it update user state on change.
        setUpLocation();


        Intent serviceIntent1 = new Intent(this, MediaService.class);

        // List of the names of the songs in res/raw/
        List<String> songTitles = new ArrayList<>();
        // List of all the albums in res/raw
        List<String> albumsList = new ArrayList<>();

        // For every single file in the res/raw folder...
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            // Get the name of the song file.
            String filename = field.getName();

            // Create the song object from file.
            Song song = createSongFromFile(filename);
            // Add the song to the database.
            songDB.insert(song);

            // Record this songs title to display it.
            songTitles.add(song.getTitle());
            // Add this song's album to the albums listview if it doesn't already exist.
            if (!albumsList.contains(song.getAlbum())) albumsList.add(song.getAlbum());
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
        launchFlashbackActivity.setOnClickListener(view -> {
            // Stop the music from playing.
            Intent serviceIntent = new Intent(this, MediaService.class);
            stopService(serviceIntent);

            // Open the flashback mode.
            Intent intent = new Intent(MainActivity.this, FlashbackActivity.class);
            startActivity(intent);

        });

        Button albumButton = (Button) findViewById(R.id.albumsDisplayButton);
        albumButton.setOnClickListener(view -> {
            // Hide the track listview and unhide the album listview.
            songsView.setVisibility(View.GONE);
            albumsView.setVisibility(View.VISIBLE);
        });

        Button tracksButton = (Button) findViewById(R.id.tracksDisplayButton);
        tracksButton.setOnClickListener(view -> {
            // Unhide the track listview and hide the album listview.
            albumsView.setVisibility(View.GONE);
            songsView.setVisibility(View.VISIBLE);
        });

        // Play the song whenever it's name is clicked on the list.
        songsView.setOnItemClickListener((adapterView, view, pos, l) -> {
            // Get the name of the song to play.
            String name = adapterView.getItemAtPosition(pos).toString();

            // Play the song.
            musicSystem.playTrack(name);

            // Open a new activity for displaying song metadata and addressing user functionality
            Intent intent = new Intent(MainActivity.this, TrackDisplayActivity.class);
            intent.putExtra("NAME", name);
            startActivity(intent);
        });

        // Play the songs in this album if an album is clicked.
        albumsView.setOnItemClickListener((adapterView, view, pos, l) -> {
            // Get the name of the song to play.
            String name = adapterView.getItemAtPosition(pos).toString();

            // Open a new activity for displaying song metadata and addressing user functionality
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra("NAME", name);
            startActivity(intent);
        });
    }

    private Song createSongFromFile(String filename) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        // Get the metadata from the song.
        Uri source = Uri.parse("android.resource://" + getPackageName() + "/raw/" + filename);
        mmr.setDataSource(this, source);

        // Extract information from the metadata.
        String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String track_num = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        byte[] album_art = mmr.getEmbeddedPicture();

        // Create the song object from the metadata.
        return new Song(filename, songTitle, albumName, artist, track_num, album_art);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        musicSystem.destroy();
    }

}
