package com.cse110.flashbackmusicplayer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // A database of all the songs that are stored in the res folder.
    static SongDatabase songDB = null;

    // In charge of handling all requests to play music.
    static MusicSystem musicSystem = null;

    // All of the parameters of the user.
    static UserState userState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "MainActivity has been created");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Initialize the user.
        userState = new UserStateImpl();
        // Create a database of songs and populate it.
        songDB = new SongDatabase(userState);
        // Create the system that will play all the music.
        musicSystem = new MusicSystem(MainActivity.this);
        // Create a location listener and make it update user state on change.
        new LocationSystem(this, userState);

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
        ListAdapter songAdapter = new ArrayAdapter<>(this, R.layout.list_white_text,R.id.list_content, songTitles);
        final ListView songsView = (ListView) findViewById(R.id.songsView);
        //songsView.setAdapter(songAdapter);

        // Display the album list on the screen.
        ListAdapter albumAdapter = new ArrayAdapter<>(this, R.layout.list_white_text,R.id.list_content, albumsList);
        final ListView albumsView = (ListView) findViewById(R.id.albumsView);
        //albumsView.setAdapter(albumAdapter);

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
        Button launchFlashbackActivity = (Button) findViewById(R.id.switchMode);
        launchFlashbackActivity.setOnClickListener(view -> {
            // Stop the music from playing.
            musicSystem.destroy();

            // Open the flashback mode.
            Intent intent = new Intent(MainActivity.this, FlashbackActivity.class);
            startActivityForResult(intent, 1);
            Log.d("MainActivity", "Starting flashback mode");
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
                        //AsyncTask<String, Void, String> loader = new TrackLoader(MainActivity.this);
                        //AsyncTask<String, Void, String> unzipper = new TrackUnzipper(loader);
                        AsyncTask<String, Void, String> downloader = new TrackDownloader(MainActivity.this);
                        downloader.execute(url);
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

        Log.d("MainActivity", "Loaded song from " + filename + " <" +
                songTitle + ", " + albumName + ", " + artist + ", " + track_num + ">");

        // Create the song object from the metadata.
        return new Song(filename, songTitle, albumName, artist, track_num, album_art);
    }

    public void saveSong(Song song) {
        SharedPreferences settings;
        Editor editor;

        settings = getSharedPreferences(song.getFilename(), MODE_PRIVATE);
        editor = settings.edit();

        //Using the filename of the song as the filename stored in the sharedPreferences file
        //editor.putString("filename", song.getFilename());
        editor.putString("title", song.getTitle());
        editor.putString("album", song.getAlbum());
        editor.putString("artist", song.getArtist());
        editor.putString("trackNumber", song.getTrackNumber());
        editor.putString("albumCover", song.getAlbumCover().toString());
        editor.commit();
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
        super.onDestroy();
        Log.d("MainActivity", "MainActivity has been destroyed");
    }

}