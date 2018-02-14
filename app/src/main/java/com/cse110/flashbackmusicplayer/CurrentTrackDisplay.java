package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;

import org.w3c.dom.Text;

public class CurrentTrackDisplay extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button switchScreen;
    Button pauseButton;
    boolean pauseDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // access metadata
        String[] metadata = getIntent().getStringArrayExtra("METADATA");

        // access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= metadata[0] + "\n" + metadata[2];
        songTitle.setText(songTitleStr);

        // access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = metadata[1] + "\nTrack #: " + metadata[3];
        songAlbum.setText(songAlbumStr);

        // create image for album cover
        ImageView albumcover = (ImageView) findViewById(R.id.album_cover);
        byte[] albumArtData = getIntent().getByteArrayExtra("ALBUM_ART");
        if (albumArtData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.length);
            albumcover.setImageBitmap(bitmap);
            albumcover.setAdjustViewBounds(true);
        }
        else {
            albumcover.setImageResource(R.drawable.nocover);
            albumcover.setAdjustViewBounds(true);
        }

        switchScreen = (Button) findViewById(R.id.backButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);

        // call media player
        launchInit();

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
    public void launchInit() {
        int resID = getIntent().getIntExtra("RES_ID", 0);
        mediaPlayer = MediaPlayer.create(CurrentTrackDisplay.this, resID);
        mediaPlayer.start();
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
