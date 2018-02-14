package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.net.Uri;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // All of the information associated with the user.
    static UserState userState = null;

    // The object that plays the music.
    MediaPlayer mediaPlayer;

    List<String> songsList;
    List<String> songTitles;
    List<List<String>> songsMetaData;
    List<ByteArrayOutputStream> songsAlbumArt;

    ListView songsView;

    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the user.
        userState = new UserState();

        //metadata
        String songTitle;
        String albumName;
        String artist;
        String track_num;
        byte[] album_art;


        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);

        // Create a location listener and make it update user state on change.
        setUpLocation();

        songsView = (ListView) findViewById(R.id.songsView);
        songsList = new ArrayList<>();
        songTitles = new ArrayList<>();
        songsMetaData = new ArrayList<>();
        songsAlbumArt = new ArrayList<>();

        //create metadata, source objects
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri source;
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            songsList.add(name);

            //extract metadata
            source=Uri.parse("android.resource://"+getPackageName()+"/raw/" + name);
            Log.d("URI", source.toString());
            mmr.setDataSource(this, source);

            songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            songTitles.add(songTitle);
            albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            track_num = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            album_art = mmr.getEmbeddedPicture();

            // format metadata
            String[] songMetaDataArr = new String[]{songTitle, albumName, artist, track_num};
            songsMetaData.add(Arrays.asList(songMetaDataArr));

            // format cover art
            ByteArrayOutputStream artByteStream;
            try {
                artByteStream = new ByteArrayOutputStream();
                artByteStream.write(album_art, 0, album_art.length);
                songsAlbumArt.add(artByteStream);
            }
            catch (NullPointerException e) {
                songsAlbumArt.add(null);
            }
        }
        // Create a location listener and make it update user state on change.
        setUpLocation();

        // If the flashback button is pressed, open the flashback activity.
        Button launchFlashbackActivity = (Button) findViewById(R.id.switchMode);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop the current song from playing.
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                // Open the flashback mode.
                Intent intent = new Intent(MainActivity.this, FlashbackActivity.class);
                startActivity(intent);
            }
        });



        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        // Upload all of the songs from the raw folder.
        ListView songsView = (ListView) findViewById(R.id.songsView);
        ArrayList<String> songNames = new ArrayList<>();
        for (Field field : R.raw.class.getFields()) {
            // Add each song in the folder to the database.
            Song song = new Song(field.getName(), "temp", "temp", 0);
            songNames.add(field.getName());
            // Record the name of the song so that we can display it on the screen.
            songDB.insert(song);
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songNames);
        songsView.setAdapter(adapter);

        // Play the song whenever it's name is placed on the list.
        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                int resID = getResources().getIdentifier(songsList.get(pos), "raw", getPackageName());
                String name = songsList.get(pos);
                String[] specificSongData = (String[]) songsMetaData.get(pos).toArray();
                byte[] album_art = null;
                if (songsAlbumArt.get(pos) != null) {
                    album_art = songsAlbumArt.get(pos).toByteArray();
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                // switching display to current track display
                launchTrackDisplay(resID, name, specificSongData, album_art);
                String name = adapterView.getItemAtPosition(index).toString();
                Song song = songDB.get(name);
                song.startedPlaying(userState);
                int resID = getResources().getIdentifier(name, "raw", getPackageName());

                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                mediaPlayer.start();
            }
        });

    }

    /**
     * Method to switch display from tracks/albums display to current track display
     */
    public void launchTrackDisplay(int resID, String name, String[] metadata, byte[] songsAlbumArt){
        Intent intent = new Intent(this, CurrentTrackDisplay.class);
        intent.putExtra("NAME", name);
        intent.putExtra("RES_ID", resID);
        intent.putExtra("METADATA", metadata);
        intent.putExtra("ALBUM_ART", songsAlbumArt);

        startActivity(intent);
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
                // Signify to the database that the location has changed and song priorities need
                // to be recalculated.
                songDB.userStateChanged();
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
