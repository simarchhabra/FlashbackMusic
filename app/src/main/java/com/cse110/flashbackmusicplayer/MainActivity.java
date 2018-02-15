package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // All of the information associated with the user.
    static UserState userState = null;

    // List of all the song filenames in res/raw.
    List<String> songsList = new ArrayList<>();
    // List of the names of the songs in res/raw/
    List<String> songTitles = new ArrayList<>();

    public List<String> albums = new ArrayList<>();

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

        // For every single file in the res/raw folder...
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        ListView songsView = (ListView) findViewById(R.id.songsView);
        ListView albumsView = (ListView) findViewById(R.id.albumsView);
        Button albumButton = (Button) findViewById(R.id.albumsDisplayButton);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String filename = field.getName();
            songsList.add(filename);

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
            int albumIteration = 0;
            boolean albumIsPresent = false;
            while((albumIteration<albums.size())&&!albums.isEmpty()&& albumIsPresent == false) {
                if(albums.get(albumIteration).equals(albumName))
                {
                    albumIsPresent = true;
                }
                albumIteration++;
            }

            if(albumIsPresent == false)
            {
                albums.add(albumName);
            }
            // Create the song object from the metadata, and insert it into the database.
            Song song = new Song(filename, songTitle, albumName, artist, track_num, album_art);
            songDB.insert(song);
        }
        // Display the songs list on the screen.
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        songsView.setAdapter(adapter);



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



        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // Play the song whenever it's name is placed on the list.
        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // Get the name of the song to play.
                String name = adapterView.getItemAtPosition(pos).toString();
                Intent intent = new Intent(MainActivity.this, CurrentTrackDisplay.class);
                intent.putExtra("NAME", name);
                // Open a new activity, where the song will play.
                startActivity(intent);
            }
        });
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
