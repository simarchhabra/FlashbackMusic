package com.cse110.flashbackmusicplayer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class FlashbackActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    boolean pauseDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        // Create the play list.
        songDB.generateFlashbackList();

        // Play the song with the nighest priority.
        playNextSong();

        // Return back to normal mode.
        Button switchScreen = (Button) findViewById(R.id.switchBack);
        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Stop the current song from playing.
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                finish();
            }
        });

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
    }

    public void playNextSong() {
        // If there are no songs in the flashback playlist or the state of the user has changed
        // since the last time the playlist was generated, attempt to generate it again.
        if (songDB.isEmpty() || songDB.hasStateChanged()) {
            songDB.generateFlashbackList();

            // If the list is still empty, then there are no possible songs to play.
            if (songDB.isEmpty()) return;
        }

        // Get the very first song that we will play.
        Song next = songDB.top(); songDB.pop();
        int resID = getResources().getIdentifier(next.getFilename(), "raw", getPackageName());
        displaySong(next);

        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(FlashbackActivity.this, resID);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNextSong();
            }
        });

        next.startedPlaying(userState);
        mediaPlayer.start();
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
