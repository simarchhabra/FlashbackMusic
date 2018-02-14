package com.cse110.flashbackmusicplayer;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    // List of all the song filenames in res/raw.
    List<String> albumsList = new ArrayList<>();
    // List of the names of the songs in res/raw/
    List<String> albumsTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        ListView albumView = (ListView) findViewById(R.id.albumsView);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String filename = field.getName();
            albumsList.add(filename);

            // Get the metadata from the song.
            Uri source = Uri.parse("android.resource://" + getPackageName() + "/raw/" + filename);
            mmr.setDataSource(this, source);

            // Extract information from the metadata.
            String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            albumsTitles.add(albumName);
            albumsList.add(albumName);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String track_num = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            byte[] album_art = mmr.getEmbeddedPicture();

            // Create the song object from the metadata, and insert it into the database.
            Song song = new Song(filename, songTitle, albumName, artist, track_num, album_art);
            songDB.insert(song);
        }
        // Display the songs list on the screen.
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        songsView.setAdapter(adapter);


    }

}
