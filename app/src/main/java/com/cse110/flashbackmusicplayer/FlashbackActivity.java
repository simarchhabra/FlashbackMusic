package com.cse110.flashbackmusicplayer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class FlashbackActivity extends AppCompatActivity {

    // In charge of handling all requests to play music.
    MusicSystem musicSystem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        // Create the system that will play all the music.
        musicSystem = new MusicSystem(FlashbackActivity.this);

        // Create the play list.
        songDB.generateFlashbackList();

        // Play the song with the nighest priority.
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

        // Return back to normal mode.
        Button switchScreen = (Button) findViewById(R.id.switchBack);
        switchScreen.setOnClickListener(view -> finish());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicSystem.destroy();
    }

    public Song nextSong() {
        // If there are no songs in the flashback playlist or the state of the user has changed
        // since the last time the playlist was generated, attempt to generate it again.
        if (songDB.isEmpty() || songDB.hasStateChanged()) {
            songDB.generateFlashbackList();

            // If the list is still empty, then there are no possible songs to play.
            if (songDB.isEmpty()) return null;
        }

        // Get the very first song that we will play.
        Song next = songDB.top(); songDB.pop();

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
