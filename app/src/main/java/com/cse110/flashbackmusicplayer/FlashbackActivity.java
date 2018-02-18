package com.cse110.flashbackmusicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class FlashbackActivity extends AppCompatActivity {

    // In charge of handling all requests to play music.
    MusicSystem musicSystem = null;
    // Draws and updates the UI.
    SongCallback ui = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        // Create the play list.
        songDB.generateFlashbackList();

        // Get the first song in the album, and add it to the intent.
        if (!songDB.isEmpty()) {
            getIntent().putExtra("TRACK_NAME", songDB.top().getTitle());
        }
        else {
            getIntent().putExtra("TRACK_NAME", "empty_track");
        }

        // Create the system that will play all the music.
        musicSystem = new MusicSystem(FlashbackActivity.this);
        // Set the callback so that the music system can update the UI.
        ui = new SongCallbackUI(FlashbackActivity.this);
        musicSystem.setSongCallback(ui);

        // Play the song with the nighest priority.
        musicSystem.playTracks(this::nextSong);

        // Go to the next song.
        final Button skip = (Button) findViewById(R.id.nextButton);
        skip.setOnClickListener(view -> {
            musicSystem.skipTrack();
        });
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

        // Record the name of teh track.
        getIntent().putExtra("TRACK_NAME", next.getTitle());

        // Draw the metadata for the song.
        ui.redraw();

        return next;
    }


}
