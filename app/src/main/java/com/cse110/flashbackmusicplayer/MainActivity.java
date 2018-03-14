package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TrackContainer {

    static UserSystem user = null;
    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // The online database where information about all songs played by
    // all users is played.
    static FirebaseManager db = null;

    // In charge of handling all requests to play music.
    static MusicSystem musicSystem = null;

    // All of the parameters of the user.
    static UserState userState = null;

    // In charge of downloading all of the songs.
    static DownloadSystem downloadSystem = null;

    // List of the names of all the songs.
    List<String> songTitles;
    ArrayAdapter songAdapter;
    // List of all the albums.
    List<String> albumsList;
    ArrayAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "MainActivity has been created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the user.
        if (GoogleSignIn.getLastSignedInAccount(MainActivity.this) == null) {
            Log.d("NoLogIn", "login");
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
        }
        // uncomment all of these after figuring out GoogleSignInResult Error
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        //user = new UserSystem(MainActivity.this);
        //user.initService();
        //String[] profileInfo = user.getProfileInfo();
        //String[] contactsInfo = user.getContactsInfo();
//        Log.d("profile_info", profileInfo.toString());
  //      Log.d("contacts_info", contactsInfo.toString());
        //user.destroy();
        userState = new UserStateImpl(null/*profileInfo[0]*/);
        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        // Create the system that will play all the music.
        musicSystem = new MusicSystem(MainActivity.this);
        // Create the system that will download everything.
        downloadSystem = new DownloadSystem(MainActivity.this, MainActivity.this);
        registerReceiver(downloadSystem, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // Create a location listener and make it update user state on change.
        new LocationSystem(this, userState);

        // Get a reference to the firebase manager.
        db = new FirebaseManager(songDB);

        // Initialize list views that will display the tracks and the albums.
        songTitles = new ArrayList<>();
        albumsList = new ArrayList<>();

        // Display the songs list on the screen.
        songAdapter = new ArrayAdapter<>(this, R.layout.list_white_text,R.id.list_content, songTitles);
        final ListView songsView = (ListView) findViewById(R.id.songsView);
        songsView.setAdapter(songAdapter);

        // Display the album list on the screen.
        albumAdapter = new ArrayAdapter<>(this, R.layout.list_white_text,R.id.list_content, albumsList);
        final ListView albumsView = (ListView) findViewById(R.id.albumsView);
        albumsView.setAdapter(albumAdapter);

        Button albumButton = (Button) findViewById(R.id.albumsDisplayButton);
        albumButton.setSelected(true);
        Button tracksButton = (Button) findViewById(R.id.tracksDisplayButton);
        albumButton.setOnClickListener(view -> {
            // Hide the track listview and unhide the album listview.
            albumButton.setSelected(true);
            tracksButton.setSelected(false);
            songsView.setVisibility(View.GONE);
            albumsView.setVisibility(View.VISIBLE);
            Log.d("MainActivity", "Opened album list");
        });


        tracksButton.setOnClickListener(view -> {
            // Unhide the track listview and hide the album listview.
            albumButton.setSelected(false);
            tracksButton.setSelected(true);
            albumsView.setVisibility(View.GONE);
            songsView.setVisibility(View.VISIBLE);
            Log.d("MainActivity", "Opened tracks list");
        });

        // Play the song whenever it's name is clicked on the list.
        songsView.setOnItemClickListener((adapterView, view, pos, l) -> {
            // Get the name of the song to play.
            String name = adapterView.getItemAtPosition(pos).toString();
            Log.d("MainActivity", "Clicked on song " + name);

            // Open a new activity for displaying song metadata and addressing user functionality
            Intent intent = new Intent(MainActivity.this, TrackDisplayActivity.class);
            intent.putExtra("TRACK_NAME", name);
            startActivity(intent);

            // Play the song.
            musicSystem.playTrack(name);
        });

        // Play the songs in this album if an album is clicked.
        albumsView.setOnItemClickListener((adapterView, view, pos, l) -> {
            // Get the name of the song to play.
            String name = adapterView.getItemAtPosition(pos).toString();
            Log.d("MainActivity", "Playing album " + name);

            // Open a new activity for displaying song metadata and addressing user functionality
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra("ALBUM_NAME", name);
            startActivity(intent);
        });

        // If the flashback button is pressed, open the flashback activity.
        Button launchVibeActivity = (Button) findViewById(R.id.switchMode);
        launchVibeActivity.setOnClickListener(view -> {
            // Stop the music from playing.
            musicSystem.destroy();

            // Open the flashback mode.
            Intent intent = new Intent(MainActivity.this, VibeActivity.class);
            startActivityForResult(intent, 1);
            Log.d("MainActivity", "Starting vibe mode");
        });

        // If the download songs button is pressed, open an activity that lets you download.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // https://developer.android.com/guide/topics/ui/dialogs.html
                // Create a popup window asking the user to enter a URL.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Download song(s)");
                builder.setMessage("Enter URL:");

                // Create a place to enter the URL.
                final EditText urlInput = new EditText(MainActivity.this);
                builder.setView(urlInput);

                // Create the accept and cancel buttons.
                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = urlInput.getText().toString();
                        downloadSystem.downloadTrack(url);
                    }
                });
                builder.setNegativeButton("Cancel", null);

                //builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                musicSystem = new MusicSystem(MainActivity.this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        musicSystem.destroy();
        unregisterReceiver(downloadSystem);
        super.onDestroy();
        Log.d("MainActivity", "MainActivity has been destroyed");
    }

    @Override
    public void addTrack(Song song) {
        // If this track is in the database, don't add it again.
        if (songDB.get(song.getTitle()) == null) {
            // Add the song to the database.
            songDB.insert(song);
            // Add the firebase database as an observer of the song.
            song.registerObserver(db);
            db.registerObserver(song);
        }

        // If this song is in the list, don't add it again.
        if (!songTitles.contains(song.getTitle())) {
            // Record this songs title to display it.
            songTitles.add(song.getTitle());
            songAdapter.notifyDataSetChanged();
            // Add this song's album to the albums listview if it doesn't already exist.
            if (!albumsList.contains(song.getAlbum())) {
                albumsList.add(song.getAlbum());
                albumAdapter.notifyDataSetChanged();
            }
        }
    }
}