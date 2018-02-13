package com.cse110.flashbackmusicplayer;


import android.media.MediaPlayer;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // we should eliminate this, use vv
    // String selectedFromList = (lv.getItemAtPosition(position));
    List<String> songsList;

    ListView songsView;

    ListAdapter adapter;

    MediaPlayer mediaPlayer;

    // All of the information associated with the user.
    UserState userState = null;

    // A database of all the songs that are stored in the res folder.
    SongDatabase songDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchFlashbackActivity = (Button) findViewById(R.id.switchMode);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });

        songsView = (ListView) findViewById(R.id.songsView);

        songsList = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            songsList.add(fields[i].getName());
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
            }
        });

        // Create the user.
        userState = new UserState();

        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        populateSongs(); // TODO: actually make this method work. It's just skeleton code atm.

        // Create a location listener and make it update user state on change.
        setUpLocation();
    }

    public void launchActivity() {
        Intent intent = new Intent(this, FlashbackActivity.class);
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

    private void populateSongs() {}

}
