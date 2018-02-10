package com.cse110.flashbackmusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // All of the information associated with the user.
    UserState userState = null;

    // A database of all the songs that are stored in the res folder.
    SongDatabase songDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the user.
        userState = new UserState();

        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        populateSongs(); // TODO: actually make this method work. It's just skeleton code atm.

        // Create a location listener and make it update user state on change.
        setUpLocation();
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
                userState.locationUpdated(lat, lon);
                // Signify to the database that the location has changed and song priorities need
                // to be recalculated.
                songDB.userStateChanged();


                // Completely temporary code that tests that location is updated correctly.
                TextView locationText = (TextView) findViewById(R.id.location);
                locationText.setText("Latitude: " + lat + " Longitude: " + lon);
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
