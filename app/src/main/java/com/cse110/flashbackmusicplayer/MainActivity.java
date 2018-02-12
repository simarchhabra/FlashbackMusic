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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // TODO: we should eliminate this, use:
    // String selectedFromList = (lv.getItemAtPosition(position));
    List<String> songsList;

    ListView songsView;

    ListAdapter adapter;

    MediaPlayer mediaPlayer;

    // TODO: causing error, commented out to work on UI
    // All of the information associated with the user.
    //UserState userState = null;

    // TODO: causing error, commented out to work on UI
    // A database of all the songs that are stored in the res folder.
    SongDatabase songDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        songsView = (ListView) findViewById(R.id.songsView);

        songsList = new ArrayList<>();

        String songTitle;
        String albumName;

        // TODO: extract metadata
       //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
       //mmr.setDataSource("/Users/vale_g/AndroidStudioProjects/cse-110-team-project-team-7/app/src/main/res/raw");

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            songsList.add(fields[i].getName());

            // TODO: metadata
            //songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            //albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            //songsList.add(songTitle);
        }



        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songsList);
        songsView.setAdapter(adapter);

        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                // remove first parameter call, use line below instead
                // String selectedFromList = (lv.getItemAtPosition(position));
                int resID = getResources().getIdentifier(songsList.get(i), "raw", getPackageName());

                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);

                mediaPlayer.start();

                // switching display to current track display
                launchTrackDisplay();
            }
        });

        /* Trash. Attempting to enable play and pause while on current track display.
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            finish();
        }
        else{
            mediaPlayer.start();
            finish();
        }*/
    }

    /**
     * Method to switch display from tracks/albums display to current track display
     */
    public void launchTrackDisplay(){
        Intent intent = new Intent(this, CurrentTrackDisplay.class);
        startActivity(intent);
    }



        // Create the user.
        UserState userState = new UserState();

        // TODO: causing error, commented out to work on UI
        // Create a database of songs and populate it.
        // Song songDB = new SongDatabase(userState);
        // TODO: actually make this method work. It's just skeleton code atm.
        //populateSongs();

        // TODO: method call causing error. Is not implemented?
        // Create a location listener and make it update user state on change.
       // setUpLocation();
    //}

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

    private void populateSongs() {}

}
