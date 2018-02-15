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

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class AlbumActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    boolean pauseDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get the name of the song we are playing.
        String albumName = getIntent().getExtras().getString("NAME");
        ArrayList<Song> songs = songDB.getAlbum(albumName);

        playNextSong(songs);

        // Draw all of the songs in this album.
        //ArrayList<String> songTitles = new ArrayList<>();
        //for (Song song : songs) songTitles.add(song.getTitle());
        //ListAdapter songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        //final ListView songsView = (ListView) findViewById(R.id.songsInAlbum);
        //songsView.setAdapter(songAdapter);

        final Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        launchPlayPause();
                        if(pauseDisplayed) {
                            pauseButton.setBackgroundResource(R.drawable.play);
                            pauseDisplayed = false;
                        }
                        else{
                            pauseButton.setBackgroundResource(R.drawable.pause);
                            pauseDisplayed = true;
                        }
                    }
                }
        );

        final Button switchScreen = (Button) findViewById(R.id.backButton);
        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                finish();
            }
        });
    }

    /**
     * Method for launching playback
     */
    public void playNextSong(ArrayList<Song> songs) {
        // If there are no songs left to play in the album, finish.
        if (songs.isEmpty()) return;

        // Get the very first song that we will play.
        Song next = songs.get(0); songs.remove(0);
        int resID = getResources().getIdentifier(next.getFilename(), "raw", getPackageName());
        displaySong(next);

        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(AlbumActivity.this, resID);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNextSong(songs);
            }
        });

        next.startedPlaying(userState);
        mediaPlayer.start();
    }

    private void displaySong(Song song) {
        // Access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= song.getTitle() + "\n" + song.getArtist();
        songTitle.setText(songTitleStr);

        // Access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
        songAlbum.setText(songAlbumStr);

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

    /**
     * Method to enable play and pause
     */
    public void launchPlayPause(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        else {
            mediaPlayer.start();
        }
    }
}
