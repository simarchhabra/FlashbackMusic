package com.cse110.flashbackmusicplayer;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class FlashbackActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

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

}
