package com.cse110.flashbackmusicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;

public class TrackDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);

        // Set the callback so that the music system can update the UI.
        SongCallback ui = new SongCallbackUI(TrackDisplayActivity.this);
        musicSystem.setSongCallback(ui);
    }
}
