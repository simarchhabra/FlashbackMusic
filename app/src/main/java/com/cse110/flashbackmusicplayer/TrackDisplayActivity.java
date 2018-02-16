package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class TrackDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);

        // Get the name of the song we are playing.
        String name = getIntent().getExtras().getString("NAME");
        Song song = songDB.get(name);

        // Draw the song metadata on the screen.
        displayTrack(song);

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

    private void displayTrack(Song song) {
        // Access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= song.getTitle();
        if(song.getArtist()!=null)
        {
            songTitleStr = songTitleStr+ "\n"+ song.getArtist();
        }
        songTitle.setText(songTitleStr);

        // access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
        songAlbum.setText(songAlbumStr);

        // create image for album cover
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
