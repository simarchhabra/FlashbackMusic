package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.media.MediaPlayer;

import org.w3c.dom.Text;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class CurrentTrackDisplay extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    boolean pauseDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);

        // Get the name of the song we are playing.
        String name = getIntent().getExtras().getString("NAME");
        Song song = songDB.get(name);

        // access and display title, artist metadata
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

        // call media player
        startPlaying(song);

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
    public void startPlaying(Song song) {
        int resID = getResources().getIdentifier(song.getFilename(), "raw", getPackageName());
        song.startedPlaying(userState);

        if (mediaPlayer != null) mediaPlayer.release();
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
