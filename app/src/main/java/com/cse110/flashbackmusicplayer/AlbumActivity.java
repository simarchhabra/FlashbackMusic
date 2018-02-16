package com.cse110.flashbackmusicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class AlbumActivity extends AppCompatActivity {

    ArrayList<Song> songs = null;
    List<String> trackTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get the name of the song we are playing.
        String albumName = getIntent().getExtras().getString("NAME");
        songs = songDB.getAlbum(albumName);

        ArrayList<Song> songs = songDB.getAlbum(albumName);
        for(int i = 0; i<songs.size();i++)
        {
            trackTitles.add(songs.get(i).getTitle());
        }
        // Play the song.
        musicSystem.playTracks(this::nextSong);


        final Button pauseButton = (Button) findViewById(R.id.pauseButton);
        if(musicSystem.isPaused()) {
            pauseButton.setBackgroundResource(R.drawable.play);
        }
        else{
            pauseButton.setBackgroundResource(R.drawable.pause);
        }
        pauseButton.setOnClickListener( view -> {
                musicSystem.togglePause();
                if(musicSystem.isPaused()) {
                    pauseButton.setBackgroundResource(R.drawable.pause);
                }
                else{
                    pauseButton.setBackgroundResource(R.drawable.play);
                }
            }
        );

        final Button switchScreen = (Button) findViewById(R.id.backButton);
        switchScreen.setOnClickListener(view -> finish());
    }

    public Song nextSong() {
        // If there are no songs left to play in the album, finish.
        if (songs.isEmpty()) return null;

        // Get the very first song that we will play.
        Song next = songs.get(0); songs.remove(0);

        // Draw the metadata for the song.
        displaySong(next);

        return next;
    }

    private void displaySong(Song song) {
        // Access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= song.getTitle();
        if(song.getArtist()!=null)
        {
            songTitleStr = songTitleStr+ "\n"+ song.getArtist();
        }
        songTitle.setText(songTitleStr);

        // Access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
        songAlbum.setText(songAlbumStr);
        ListAdapter songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trackTitles);
        final ListView tracksView = (ListView) findViewById(R.id.track_list);
        tracksView.setAdapter(songAdapter);

        // Create image for album cover
        ImageView albumcover = (ImageView) findViewById(R.id.album_cover);
        byte[] albumArtData = song.getAlbumCover();
        if (albumArtData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.length);
            albumcover.setImageBitmap(bitmap);
            albumcover.setAdjustViewBounds(true);
        }
        else {
            albumcover.setImageResource(R.drawable.nocover);
            albumcover.setAdjustViewBounds(true);
        }
    }
}
